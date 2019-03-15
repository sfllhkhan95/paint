package sfllhkhan95.doodle.core

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.crashlytics.android.Crashlytics
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.messenger.MessengerUtils
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.google.firebase.analytics.FirebaseAnalytics
import org.json.JSONException
import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.core.models.PaintCanvas
import sfllhkhan95.doodle.core.models.tools.*
import sfllhkhan95.doodle.core.utils.ActionBarManager
import sfllhkhan95.doodle.core.utils.DialogFactory
import sfllhkhan95.doodle.core.utils.OnColorPickedListener
import sfllhkhan95.doodle.core.utils.OnToolSelectedListener
import sfllhkhan95.doodle.core.views.*
import sfllhkhan95.doodle.core.views.ColorPicker
import sfllhkhan95.doodle.projects.utils.DoodleDatabase
import sfllhkhan95.doodle.projects.utils.DoodleFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * @version 2.0.0
 * @since 1.0.0
 */
class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, OnToolSelectedListener,
        OnColorPickedListener, View.OnTouchListener {

    // Brush controller
    private var brushController: SeekBar? = null

    // A custom OpenGL ES canvas to draw on
    private var paintView: PaintView? = null

    // Toolbox contains the drawing tools
    private var toolbox: ToolboxView? = null

    // Dialog boxes to confirm certain permanent actions (i.e. revert and save)
    private var dialogFactory: DialogFactory? = null

    // Action bars
    private var toolbar: CustomToolbar? = null
    private var isMaximized: Boolean = false
    private var stickyMaximized: Boolean = false

    // Are we in a REPLY flow?
    private var mReplying: Boolean = false
    private var messengerShareButton: MessengerShareButton? = null

    // Firebase Analytics (For event logging)
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    // Is this a new or existing project?
    private var isExisting = false

    // Is the project opened in read-only mode?
    private var isViewing = false

    private var projectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as DoodleApplication).setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.toolbar = CustomToolbar()

        this.brushController = findViewById(R.id.brushController)
        this.brushController!!.setOnSeekBarChangeListener(this)

        // Obtain the FirebaseAnalytics instance.
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Obtain device display metrics (used to setup project resolution)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val intent = intent
        isViewing = intent.getBooleanExtra("READ_ONLY", false)

        // Initialize canvas where everything is drawn
        paintView = findViewById(R.id.canvas)
        paintView!!.setOnTouchListener(this)
        val messengerAction = intent.getBooleanExtra("MESSENGER", false)
        messengerShareButton = findViewById(R.id.messenger_share_button)
        if (Intent.ACTION_PICK == intent.action || messengerAction) {
            messengerShareButton!!.visibility = View.VISIBLE
            messengerShareButton!!.setOnClickListener { onShareClicked(true) }
            if (Intent.ACTION_PICK == intent.action) {
                val logParams = Bundle()
                mFirebaseAnalytics!!.logEvent("reply_messenger", logParams)

                mReplying = true
                messengerShareButton!!.setActionText("Replying to")
                messengerShareButton!!.setDescriptionText("Messenger Conversation")

                val mThreadParams = MessengerUtils.getMessengerThreadParamsForIntent(intent)
                if (mThreadParams != null) {
                    val participantIds = mThreadParams.participants
                    if (participantIds != null && participantIds.size > 0) {
                        val replyingTo = participantIds[0]
                        val request = GraphRequest.newGraphPathRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/$replyingTo"
                        ) { response ->
                            val recipientName: String
                            try {
                                recipientName = response.jsonObject.get("name").toString()
                                messengerShareButton!!.setDescriptionText(recipientName)
                            } catch (ignored: NullPointerException) {

                            } catch (ignored: JSONException) {
                            }
                        }
                        request.executeAsync()
                    }
                }
            }

            initCanvas(startFromScratch(metrics))
        } else {
            initCanvas(getPaintCanvas(metrics))
        }

        // Create confirmation dialogs
        dialogFactory = DialogFactory(this, paintView)

        // Add click event listeners to toolbox buttons
        toolbox = findViewById(R.id.toolbox)
        toolbox!!.updatePenColorPicker(paintView!!.brush.strokeColor)
    }

    override fun onStart() {
        super.onStart()
        onToggleReadMode(isViewing)
        if (intent.getBooleanExtra("SHARE", false)) {
            dialogFactory!!.shareDialog(this).show()
        }
    }

    private fun onToggleReadMode(isViewing: Boolean) {
        this.isViewing = isViewing
        setMaximized(isViewing)
        if (isViewing) {
            findViewById<View>(R.id.editButton).visibility = View.VISIBLE
            findViewById<View>(R.id.deleteButton).visibility = View.VISIBLE
            paintView?.isEnabled = false
            toolbar?.secondary?.visibility = View.GONE
        } else {
            findViewById<View>(R.id.editButton).visibility = View.GONE
            findViewById<View>(R.id.deleteButton).visibility = View.GONE
            paintView?.isEnabled = true

            // Select pen
            findViewById<View>(R.id.pen).performClick()
        }
    }

    private fun initCanvas(canvas: PaintCanvas) {
        if (paintView != null) {
            paintView?.canvas = canvas
            paintView?.shapeType = Pen::class.java  // Select Pen by default
            paintView?.brush?.size = brushController!!.progress
        }
    }

    private fun getPaintCanvas(metrics: DisplayMetrics): PaintCanvas {
        val savedDoodle = intent.getStringExtra("DOODLE")
        val cameraImage = intent.getStringExtra("FROM_CAMERA")
        val galleryImage = intent.getParcelableExtra<Intent>("FROM_GALLERY")

        return if (savedDoodle != null && !savedDoodle.isEmpty()) {
            projectName = savedDoodle
            resumeProject(metrics, savedDoodle)
        } else if (galleryImage != null) {
            startFromGallery(metrics, galleryImage)
        } else if (cameraImage != null) {
            projectName = cameraImage
            startFromCamera(metrics, cameraImage)
        } else {
            startFromScratch(metrics)
        }
    }

    private fun startFromGallery(metrics: DisplayMetrics, galleryImage: Intent): PaintCanvas {
        val logParams = Bundle()
        var success = false

        var canvas: PaintCanvas
        try {
            val selectedImage = galleryImage.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            assert(selectedImage != null)
            val cursor = contentResolver.query(selectedImage!!,
                    filePathColumn, null, null, null)!!

            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            val bitmapFromFile = DoodleFactory.loadFromPath(picturePath, metrics.widthPixels, metrics.heightPixels)
            canvas = PaintCanvas.loadFromBitmap(this, metrics, bitmapFromFile)

            projectName = picturePath
            success = true
        } catch (ex: Exception) {
            canvas = startFromScratch(metrics)
        } finally { // Log event
            logParams.putBoolean("success", success)
            mFirebaseAnalytics!!.logEvent("from_gallery", logParams)
        }
        return canvas
    }

    private fun startFromCamera(metrics: DisplayMetrics, cameraImage: String): PaintCanvas {
        val logParams = Bundle()
        var success = false

        var canvas: PaintCanvas
        try {
            val bitmapFromFile = DoodleFactory.loadFromPath(cameraImage, metrics.widthPixels, metrics.heightPixels)
            canvas = PaintCanvas.loadFromBitmap(this, metrics, bitmapFromFile)

            success = true
        } catch (ex: Exception) {
            canvas = startFromScratch(metrics)
        } finally { // Log event
            logParams.putBoolean("success", success)
            mFirebaseAnalytics!!.logEvent("from_camera", logParams)
        }
        return canvas
    }

    private fun startFromScratch(metrics: DisplayMetrics): PaintCanvas {
        val canvas = PaintCanvas(this, metrics)

        // Log event
        val logParams = Bundle()
        logParams.putBoolean("success", true)
        mFirebaseAnalytics!!.logEvent("from_scratch", logParams)

        return canvas
    }

    private fun resumeProject(metrics: DisplayMetrics, savedDoodle: String): PaintCanvas {
        isExisting = true
        return PaintCanvas.loadFromPath(this, metrics, savedDoodle)
    }

    private fun share(tempFile: File) {
        // Get a shareable file URI
        val contentType = "image/jpeg"
        val contentUri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".sfllhkhan95.doodle.provider",
                tempFile)

        // Start share sequence
        val share = Intent(Intent.ACTION_SEND)
        share.type = contentType
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        share.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivityForResult(Intent.createChooser(share, "Share Doodle"), REQUEST_CODE_SHARE)
    }

    private fun shareOnMessenger(tempFile: File) {
        // Get a shareable file URI
        val contentType = "image/jpeg"
        val contentUri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".sfllhkhan95.doodle.provider",
                tempFile
        )

        // Start share sequence
        if (mReplying) {
            messengerShareButton!!.sendReply(this, contentType, contentUri)
        } else {
            messengerShareButton!!.sendMessage(this, contentType, contentUri, REQUEST_CODE_SHARE_TO_MESSENGER)
        }
    }

    /**
     * @param isMaximized
     * @since 3.4.3
     */
    private fun setMaximized(isMaximized: Boolean) {
        this.isMaximized = isMaximized
        onMaximizeToggled(this.isMaximized)
    }

    private fun onMaximizeToggled(isMaximized: Boolean) {
        findViewById<View>(R.id.toolbox).visibility = if (isMaximized) View.GONE else View.VISIBLE
        findViewById<View>(R.id.brushSizeBar).visibility = if (isMaximized) View.GONE else View.VISIBLE
        toolbar!!.configure(isMaximized)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar == brushController) {
            paintView!!.brush.size = progress
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!isMaximized) {
            val inflater = menuInflater
            inflater.inflate(R.menu.main, menu)

            if (mReplying) {
                val item = menu.findItem(R.id.share)
                item.isVisible = false
            }

            if (isExisting) {
                val item = menu.findItem(R.id.canvas)
                item.isVisible = false
            }

            val mActionBarManager = ActionBarManager(menu)
            paintView!!.setCanvasActionListener(mActionBarManager)
            mActionBarManager.sync(paintView!!)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (isViewing) {
            return false
        }

        when (item.itemId) {
            android.R.id.home -> {
                setMaximized(!isMaximized)
                stickyMaximized = !stickyMaximized
            }

            R.id.canvas -> {
                val canvasColorPicker = CanvasColorPicker(this, paintView!!.canvas!!.color, (application as DoodleApplication).getDialogTheme())
                canvasColorPicker.setOnColorPickedListener(object : OnColorPickedListener {
                    override fun onColorPicked(color: Int) {
                        paintView!!.canvas!!.color = color
                        this@MainActivity.onColorPicked(color.inv() or -0x1000000)
                        paintView!!.invalidate()
                    }
                })
                canvasColorPicker.show()
                return true
            }

            R.id.undo -> {
                paintView!!.undo()
                return true
            }

            R.id.redo -> {
                paintView!!.redo()
                return true
            }

            R.id.revert -> {
                dialogFactory!!.revertConfirmationDialog(this).show()
                return true
            }

            R.id.save -> {
                if (paintView!!.isModified) {
                    if (isExisting) {
                        dialogFactory!!.saveAsConfirmationDialog(this).show()
                    } else {
                        dialogFactory!!.saveConfirmationDialog(this).show()
                    }
                }
                return true
            }

            R.id.share -> {
                dialogFactory!!.shareDialog(this).show()
                return true
            }
        }

        return false
    }

    fun onShareClicked(messengerExpression: Boolean) {
        if (isExisting || paintView!!.isModified) {
            try {
                // Get the drawn bitmap from paint canvas
                val canvas = paintView!!.canvas
                val bitmapToShare = canvas!!.bitmap

                // Compress bitmap
                val bytes = ByteArrayOutputStream()
                bitmapToShare.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                // Write to a temporary file
                val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val path = storageDir!!.toString() + File.separator + "SHARE_IMAGE.jpg"
                val tempFile = File(path)
                val created = tempFile.createNewFile()
                Log.i(DoodleApplication.TAG, if (created) "New temporary file created." else "Temporary file already exists. Overwriting!")

                val fo = FileOutputStream(tempFile)
                fo.write(bytes.toByteArray())

                if (messengerExpression) {
                    shareOnMessenger(tempFile)
                } else {
                    share(tempFile)
                }
            } catch (e: Exception) { // If, for some reason, sharing fails
                // Log exception for error tracking
                Crashlytics.logException(e)

                // Display a nice error message to the user
                Snackbar.make(
                        messengerShareButton!!,
                        getString(R.string.error_unknown),
                        BaseTransientBottomBar.LENGTH_INDEFINITE
                ).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SHARE || requestCode == REQUEST_CODE_SHARE_TO_MESSENGER) {
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + "SHARE_IMAGE.jpg"
            val tempFile = File(path)
            if (tempFile.exists()) {
                val deleted = tempFile.delete()
                if (!deleted) {
                    Log.e(DoodleApplication.TAG, "Failed to remove temporary file.")
                }
            }
        }
    }

    override fun onBackPressed() {
        if (paintView!!.isModified) {
            dialogFactory!!.exitConfirmationDialog(this).show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onToolSelected(reset: Boolean, id: Int) {
        if (isViewing) {
            return
        }

        if (reset) {
            paintView!!.shapeType = null
        }
        when (id) {
            R.id.pen -> paintView!!.shapeType = Pen::class.java

            R.id.colorPicker -> {
                paintView!!.shapeType = sfllhkhan95.doodle.core.models.tools.ColorPicker::class.java
                paintView!!.setOnColorPickedListener(this)
            }

            R.id.line -> paintView!!.shapeType = Line::class.java
            R.id.rect -> paintView!!.shapeType = Quad2D::class.java
            R.id.box -> paintView!!.shapeType = Quad3D::class.java
            R.id.circle -> paintView!!.shapeType = Circle::class.java

            R.id.penColorPicker -> {
                val strokePicker = ColorPicker(this, paintView!!.brush.strokeColor, (application as DoodleApplication).getDialogTheme())
                strokePicker.setOnColorPickedListener(this)
                strokePicker.show()
            }
            R.id.fillColorPicker -> {
                val fillPicker = ColorPicker(this, paintView!!.brush.fillColor, (application as DoodleApplication).getDialogTheme())
                fillPicker.setOnColorPickedListener(object : OnColorPickedListener {
                    override fun onColorPicked(color: Int) {
                        paintView!!.brush.fillColor = color
                        toolbox!!.updateFillColorPicker(color)
                    }
                })
                fillPicker.show()
            }

            R.id.eraser -> paintView!!.shapeType = Eraser::class.java
        }

        toolbox!!.updateFillColorPicker(paintView!!.brush.fillColor)
        toolbox!!.updatePenColorPicker(paintView!!.brush.strokeColor)
    }

    override fun onColorPicked(color: Int) {
        paintView!!.brush.strokeColor = color
        toolbox!!.updatePenColorPicker(color)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val selected = paintView!!.shapeType
        if (!stickyMaximized && selected != null) {
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> if (selected != sfllhkhan95.doodle.core.models.tools.ColorPicker::class.java) {
                    setMaximized(false)
                } else {
                    findViewById<View>(R.id.pen).performClick()
                }

                MotionEvent.ACTION_DOWN -> {
                    if (selected != sfllhkhan95.doodle.core.models.tools.ColorPicker::class.java) {
                        setMaximized(true)
                    }

                    view.performClick()
                }
            }
        }
        return false
    }

    fun shareToFacebook() {
        // Get the drawn bitmap from paint canvas
        val canvas = paintView!!.canvas
        val bitmapToShare = canvas!!.bitmap

        val photo = SharePhoto.Builder()
                .setBitmap(bitmapToShare)
                .build()

        val content = SharePhotoContent.Builder()
                .addPhoto(photo)
                .build()

        ShareDialog.show(this, content)
    }

    fun deleteProject(view: View) {
        projectName?.let {
            ConfirmationDialog.Builder(view.context)
                    .setHeadline(view.context.getString(R.string.label_delete))
                    .setIcon(R.drawable.ic_action_delete)
                    .setTitle(view.context.resources.getString(R.string.confirm_delete_title))
                    .setMessage(view.context.resources.getString(R.string.confirm_delete_body))
                    .setPositiveButton(view.context.getString(android.R.string.ok),
                            View.OnClickListener {
                                projectName?.let {
                                    DoodleDatabase.removeDoodle(projectName!!)
                                }
                                this.finish()
                            }, true)
                    .setNegativeButton(view.context.getString(android.R.string.cancel),
                            View.OnClickListener { }, true)
                    .create()
                    .show()
        }
    }

    fun editMode(view: View) {
        onToggleReadMode(false)
    }

    private inner class CustomToolbar internal constructor() {
        private val primary: Toolbar = findViewById(R.id.primaryToolbar)
        val secondary: Toolbar

        init {
            primary.overflowIcon = resources.getDrawable(R.drawable.ic_action_layers)
            primary.title = ""

            secondary = findViewById(R.id.secondaryToolbar)
            secondary.title = ""
        }

        internal fun configure(isMaximized: Boolean) {
            primary.visibility = if (isMaximized) View.GONE else View.VISIBLE
            secondary.visibility = if (!isMaximized) View.GONE else View.VISIBLE
            setSupportActionBar(if (isMaximized) secondary else primary)
            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setHomeAsUpIndicator(
                        if (isMaximized)
                            R.drawable.ic_action_minimize
                        else
                            R.drawable.ic_action_maximize)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SHARE = 100
        private const val REQUEST_CODE_SHARE_TO_MESSENGER = 200
    }
}