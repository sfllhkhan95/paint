package sfllhkhan95.doodle.views.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import sfllhkhan95.doodle.DoodleApplication.Companion.ACTION_SHARE
import sfllhkhan95.doodle.DoodleApplication.Companion.EVENT_PROJECT_CREATE
import sfllhkhan95.doodle.DoodleApplication.Companion.EVENT_PROJECT_CREATE_BLANK
import sfllhkhan95.doodle.DoodleApplication.Companion.EXT_IMAGE_MIME
import sfllhkhan95.doodle.DoodleApplication.Companion.FILE_SHAREABLE
import sfllhkhan95.doodle.DoodleApplication.Companion.FLAG_READ_ONLY
import sfllhkhan95.doodle.DoodleApplication.Companion.PROJECT_FROM_IMAGE
import sfllhkhan95.doodle.DoodleApplication.Companion.PROJECT_FROM_SAVED
import sfllhkhan95.doodle.DoodleApplication.Companion.PROPERTY_SUCCESS
import sfllhkhan95.doodle.DoodleApplication.Companion.REQUEST_SHARE_DOODLE
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.ActionBarManager
import sfllhkhan95.doodle.bo.factory.DialogFactory
import sfllhkhan95.doodle.models.EyedropTool
import sfllhkhan95.doodle.models.PaintCanvas
import sfllhkhan95.doodle.models.shapes.*
import sfllhkhan95.doodle.utils.*
import sfllhkhan95.doodle.utils.listener.OnColorPickedListener
import sfllhkhan95.doodle.utils.listener.OnToolSelectedListener
import sfllhkhan95.doodle.views.PaintView
import sfllhkhan95.doodle.views.ToolboxView
import sfllhkhan95.doodle.views.dialog.CanvasColorPicker
import sfllhkhan95.doodle.views.dialog.ColorPicker
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * @version 2.0.0
 * @since 1.0.0
 */
class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, OnToolSelectedListener,
        OnColorPickedListener, View.OnTouchListener {

    // Brush controller
    private var brushController: SeekBar? = null

    // A custom OpenGL ES canvas to draw on
    private lateinit var paintView: PaintView

    // Toolbox contains the drawing tools
    private lateinit var toolbox: ToolboxView

    // Action bars
    private lateinit var toolbar: CustomToolbar
    private var isMaximized: Boolean = false
        set(value) {
            field = value
            onToggleFullscreen(value)
        }

    private var stickyMaximized: Boolean = false

    // Firebase Analytics (For event logging)
    private var mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    // Is this a new or existing project?
    private var isExisting = false

    // Is the project opened in read-only mode?
    private var isViewing = false

    private var projectName: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.setActivityTheme(this, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.toolbar = CustomToolbar()

        this.brushController = findViewById(R.id.brushController)
        this.brushController!!.setOnSeekBarChangeListener(this)

        // Obtain device display metrics (used to setup project resolution)
        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.defaultDisplay.getRealMetrics(metrics)
        } else {
            windowManager.defaultDisplay.getMetrics(metrics)
        }

        isViewing = intent.getBooleanExtra(FLAG_READ_ONLY, false)

        // Initialize canvas where everything is drawn
        paintView = findViewById(R.id.canvas)
        paintView.setOnTouchListener(this)
        initPaintView(createCanvas(metrics))

        // Add click event listeners to toolbox buttons
        toolbox = findViewById(R.id.toolbox)
        toolbox.updatePenColorPicker(paintView.brush.strokeColor)
    }

    override fun onStart() {
        super.onStart()
        onToggleReadMode(isViewing)
        if (intent.getBooleanExtra(ACTION_SHARE, false)) {
            shareProject()
        }
    }

    override fun onResume() {
        super.onResume()
        paintView.invalidate()
    }

    override fun onBackPressed() {
        when {
            isViewing -> super.onBackPressed()
            paintView.isModified -> DialogFactory.confirmExitDialog(this,
                    OnSuccessListener { paintView.clear(); onToggleReadMode(true) },
                    OnSuccessListener { paintView.save(); onToggleReadMode(true) })
                    .show(supportFragmentManager)
            else -> onToggleReadMode(true)
        }
    }

    override fun onColorPicked(color: Int) {
        paintView.brush.strokeColor = color
        toolbox.updatePenColorPicker(color)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!isMaximized) {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_main, menu)
            menu.findItem(R.id.canvas).isVisible = !isExisting

            val mActionBarManager = ActionBarManager(menu)
            paintView.setCanvasActionListener(mActionBarManager)
            mActionBarManager.sync(paintView)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (isViewing) return false
        when (item.itemId) {
            android.R.id.home -> {
                isMaximized = !isMaximized
                stickyMaximized = !stickyMaximized
            }

            R.id.canvas -> {
                CanvasColorPicker.Builder(paintView.canvas?.color ?: Color.BLACK)
                        .setOnColorPickedListener(object : OnColorPickedListener {
                            override fun onColorPicked(color: Int) {
                                paintView.canvas?.color = color
                                this@MainActivity.onColorPicked(color.inv() or -0x1000000)
                                paintView.invalidate()
                            }
                        })
                        .create()
                        .show(supportFragmentManager)
                return true
            }

            R.id.undo -> {
                paintView.undo()
                return true
            }

            R.id.redo -> {
                paintView.redo()
                return true
            }

            R.id.revert -> {
                DialogFactory.confirmRevertDialog(
                        this,
                        OnSuccessListener { paintView.clear() }
                ).show(supportFragmentManager)
                return true
            }

            R.id.save -> {
                if (paintView.isModified) {
                    if (isExisting) {
                        DialogFactory.confirmSaveAsDialog(
                                this,
                                OnSuccessListener { paintView.save(); this@MainActivity.finish() },
                                OnSuccessListener { paintView.saveAs();this@MainActivity.finish() }
                        ).show(supportFragmentManager)
                    } else {
                        DialogFactory.confirmSaveDialog(
                                this,
                                OnSuccessListener { paintView.save(); this@MainActivity.finish() }
                        ).show(supportFragmentManager)
                    }
                }
                return true
            }
        }

        return false
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar == brushController) {
            paintView.brush.size = progress
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    override fun onToolSelected(reset: Boolean, id: Int) {
        if (isViewing) {
            return
        }

        if (reset) {
            paintView.selectedTool = null
        }
        when (id) {
            R.id.pen -> paintView.selectedTool = Pen::class.java

            R.id.colorPicker -> {
                paintView.selectedTool = EyedropTool::class.java
                paintView.setOnColorPickedListener(this)
            }

            R.id.line -> paintView.selectedTool = Line::class.java
            R.id.rect -> paintView.selectedTool = Quad2D::class.java
            R.id.box -> paintView.selectedTool = Quad3D::class.java
            R.id.circle -> paintView.selectedTool = Circle::class.java
            R.id.triangle -> paintView.selectedTool = Triangle::class.java
            R.id.diamond -> paintView.selectedTool = Diamond::class.java
            R.id.star -> paintView.selectedTool = Star::class.java

            R.id.penColorPicker -> {
                val strokePicker = ColorPicker.Builder(paintView.brush.strokeColor)
                strokePicker.setOnColorPickedListener(this)
                strokePicker.create().show(supportFragmentManager)
            }
            R.id.fillColorPicker -> {
                val fillPicker = ColorPicker.Builder(paintView.brush.fillColor)
                fillPicker.setOnColorPickedListener(object : OnColorPickedListener {
                    override fun onColorPicked(color: Int) {
                        paintView.brush.fillColor = color
                        toolbox.updateFillColorPicker(color)
                    }
                })
                fillPicker.create().show(supportFragmentManager)
            }

            R.id.eraser -> paintView.selectedTool = Eraser::class.java
        }

        toolbox.updateFillColorPicker(paintView.brush.fillColor)
        toolbox.updatePenColorPicker(paintView.brush.strokeColor)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val selected = paintView.selectedTool
        if (!stickyMaximized && selected != null) {
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> if (selected != EyedropTool::class.java) {
                    isMaximized = false
                } else {
                    findViewById<View>(R.id.pen).performClick()
                }

                MotionEvent.ACTION_DOWN -> {
                    if (selected != EyedropTool::class.java) {
                        isMaximized = true
                    }

                    view.performClick()
                }
            }
        }
        return false
    }

    fun deleteProject(view: View) {
        projectName?.let { project ->
            DialogFactory.confirmDeleteDialog(view.context, OnSuccessListener {
                ProjectUtils.delete(project)
                this@MainActivity.finish()
            }).show(supportFragmentManager)
        }
    }

    fun editMode(view: View) {
        onToggleReadMode(false)
    }

    fun shareProject(view: View? = null) {
        onShareClicked()
    }

    private fun createCanvas(metrics: DisplayMetrics): PaintCanvas {
        val savedDoodle = intent.getStringExtra(PROJECT_FROM_SAVED)
        val selectedImage = intent.getParcelableExtra<Uri>(PROJECT_FROM_IMAGE)

        return if (savedDoodle != null && savedDoodle.isNotEmpty()) {
            projectName = savedDoodle
            resumeProject(metrics, savedDoodle)
        } else if (selectedImage != null) {
            startFromDevice(metrics, selectedImage)
        } else {
            startFromScratch(metrics)
        }
    }

    private fun createShareableFile(): File? {
        return try {
            // Get the drawn bitmap from paint canvas
            val bytes = ByteArrayOutputStream()
            paintView.canvas!!.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

            // Write to a temporary file
            val tempFile = FileUtils.createImageFile(this, FILE_SHAREABLE)
            FileUtils.writeToFile(bytes.toByteArray(), tempFile)

            tempFile
        } catch (ex: Exception) {
            Crashlytics.logException(ex)
            Snackbar.make(
                    paintView,
                    getString(R.string.error_unknown),
                    Snackbar.LENGTH_INDEFINITE
            ).show()

            null
        }
    }

    private fun initPaintView(canvas: PaintCanvas) {
        paintView.canvas = canvas
        paintView.selectedTool = Pen::class.java  // Select Pen by default
        paintView.brush.size = brushController!!.progress
    }

    private fun onShareClicked() {
        if (isExisting || paintView.isModified) {
            createShareableFile()?.let { tempFile -> share(tempFile) }
        }
    }

    private fun onToggleFullscreen(isMaximized: Boolean) {
        findViewById<View>(R.id.toolbox).visibility = if (isMaximized) View.GONE else View.VISIBLE
        findViewById<View>(R.id.brushSizeBar).visibility = if (isMaximized) View.GONE else View.VISIBLE
        toolbar.configure(isMaximized)

        window.decorView.apply {
            systemUiVisibility = 0
            systemUiVisibility = if (!isMaximized) {
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                } else {
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }
            }
        }
    }

    private fun onToggleReadMode(isViewing: Boolean) {
        this.isViewing = isViewing
        this.isMaximized = isViewing
        if (isViewing) {
            findViewById<View>(R.id.editButton).visibility = View.VISIBLE
            findViewById<View>(R.id.shareButton).visibility = View.VISIBLE
            findViewById<View>(R.id.deleteButton).visibility = View.VISIBLE
            paintView.isEnabled = false
            toolbar.secondary.visibility = View.GONE
        } else {
            findViewById<View>(R.id.editButton).visibility = View.GONE
            findViewById<View>(R.id.shareButton).visibility = View.GONE
            findViewById<View>(R.id.deleteButton).visibility = View.GONE
            paintView.isEnabled = true

            // Select pen
            findViewById<View>(R.id.pen).performClick()
        }
        paintView.invalidate()
    }

    private fun resumeProject(metrics: DisplayMetrics, savedDoodle: String): PaintCanvas {
        isExisting = true
        return PaintCanvas.createWithBitmapPath(this, metrics, savedDoodle)
    }

    private fun startFromDevice(metrics: DisplayMetrics, galleryImage: Uri?): PaintCanvas {
        var canvas: PaintCanvas
        var success = false
        try {
            val path = galleryImage?.path
            val bitmap = BitmapUtils.openFromPath(path!!, metrics.widthPixels, metrics.heightPixels)
            canvas = PaintCanvas.createWithBitmap(this, metrics, bitmap!!)

            projectName = path
            success = true
        } catch (ex: Exception) {
            canvas = startFromScratch(metrics)
        } finally {
            val logParams = Bundle()
            logParams.putBoolean(PROPERTY_SUCCESS, success)
            mFirebaseAnalytics.logEvent(EVENT_PROJECT_CREATE, logParams)
        }
        return canvas
    }

    private fun startFromScratch(metrics: DisplayMetrics): PaintCanvas {
        val canvas = PaintCanvas(this, metrics)

        // Log event
        val logParams = Bundle()
        logParams.putBoolean(PROPERTY_SUCCESS, true)
        mFirebaseAnalytics.logEvent(EVENT_PROJECT_CREATE_BLANK, logParams)

        return canvas
    }

    private fun share(imageFile: File, targetPackage: String? = null) {
        val intent = ShareUtils.createShareIntent(applicationContext, imageFile, EXT_IMAGE_MIME)
        intent.setPackage(targetPackage)
        startActivityForResult(
                Intent.createChooser(intent, resources.getString(R.string.menu_action_share)),
                REQUEST_SHARE_DOODLE
        )
    }

    private inner class CustomToolbar internal constructor() {
        private val primary: Toolbar = findViewById(R.id.primaryToolbar)
        val secondary: Toolbar

        init {
            primary.overflowIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_action_layers)
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

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(try {
            LocaleUtils.configureBaseContext(base)
        } catch (ignored: Exception) {
            base
        })
    }

}