package sfllhkhan95.doodle.projects

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.ads.AdManager
import sfllhkhan95.doodle.core.MainActivity
import sfllhkhan95.doodle.core.SettingsActivity
import sfllhkhan95.doodle.core.views.ConfirmationDialog
import sfllhkhan95.doodle.projects.utils.DoodleDatabase
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater
import java.io.File
import java.io.IOException

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 16/06/2018 12:10 AM
 */
class HomeActivity : AppCompatActivity(), SpeedDialView.OnActionSelectedListener {

    private var thumbnailInflater: ThumbnailInflater? = null

    private var mCameraPicturePath: String? = null

    private var mAdManager: AdManager? = null
    private var settingsButton: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as DoodleApplication).setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        thumbnailInflater = ThumbnailInflater(this)

        // Build floating actions
        val composeButton = findViewById<SpeedDialView>(R.id.compose_button)
        composeButton.setOnActionSelectedListener(this)

        composeButton.addActionItem(SpeedDialActionItem.Builder(R.id.link_camera, R.drawable.ic_open_camera)
                .setLabel(getString(R.string.label_camera))
                .setFabBackgroundColor(ContextCompat.getColor(this@HomeActivity, R.color.red_900))
                .setLabelColor(ContextCompat.getColor(this@HomeActivity, R.color.red_900))
                .create()
        )
        composeButton.addActionItem(SpeedDialActionItem.Builder(R.id.link_gallery, R.drawable.ic_open_gallery)
                .setLabel(getString(R.string.label_gallery))
                .setFabBackgroundColor(ContextCompat.getColor(this@HomeActivity, R.color.red_900))
                .setLabelColor(ContextCompat.getColor(this@HomeActivity, R.color.red_900))
                .create()
        )
        composeButton.addActionItem(SpeedDialActionItem.Builder(R.id.link_blank, R.drawable.ic_open_blank)
                .setLabel(getString(R.string.label_blank))
                .setFabBackgroundColor(ContextCompat.getColor(this@HomeActivity, R.color.blue_grey_500))
                .setLabelColor(ContextCompat.getColor(this@HomeActivity, R.color.blue_grey_500))
                .create()
        )

        settingsButton = findViewById(R.id.settingsButton)
        settingsButton?.setOnClickListener {
            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        mAdManager = AdManager.instance
        mAdManager?.loadVideoAd(this)
    }

    override fun onResume() {
        super.onResume()

        // Inflate thumbnails of saved projects
        thumbnailInflater?.let {
            it.setSavedProjects(DoodleDatabase.listDoodles())
            runOnUiThread(it)
        }
    }

    override fun onBackPressed() {
        val mBuilder = ConfirmationDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setHeadline(getString(R.string.label_exit))
                .setTitle(getString(R.string.confirm_quit))
                .setMessage(getString(R.string.description_quit))
                .setPositiveButton(getString(android.R.string.yes), View.OnClickListener { super@HomeActivity.onBackPressed() }, true)
                .setNegativeButton(getString(android.R.string.cancel), View.OnClickListener { /* no-op */ }, true)

        mAdManager?.let { ads ->
            if (ads.isVideoAdLoaded) {
                mBuilder.setMessage(getString(R.string.description_watch_ad))
                        .setNegativeButton(getString(R.string.label_watch_ad), View.OnClickListener { ads.showVideoAd(this@HomeActivity) }, false)
            }
        }

        mBuilder.create().show()
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            REQUEST_PICK_PHOTO -> if (data != null) {
                val openGalleryImage = Intent(applicationContext, MainActivity::class.java)
                openGalleryImage.putExtra("FROM_GALLERY", data)
                startActivity(openGalleryImage)
            }
            REQUEST_TAKE_PHOTO -> if (mCameraPicturePath != null && !mCameraPicturePath!!.isEmpty()) {
                val openCameraImage = Intent(applicationContext, MainActivity::class.java)
                openCameraImage.putExtra("FROM_CAMERA", mCameraPicturePath)
                startActivity(openCameraImage)
            }
        }
    }

    override fun onActionSelected(speedDialActionItem: SpeedDialActionItem): Boolean {
        when (speedDialActionItem.id) {
            R.id.link_blank -> {
                val blankProjectIntent = Intent(this, MainActivity::class.java)
                blankProjectIntent.putExtra("BG_COLOR", Color.BLACK)
                startActivity(blankProjectIntent)
                return true
            }
            R.id.link_gallery -> {
                val pickPictureIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                if (pickPictureIntent.resolveActivity(packageManager) != null) {
                    try {
                        startActivityForResult(pickPictureIntent, REQUEST_PICK_PHOTO)
                    } catch (ex: ActivityNotFoundException) {
                        Snackbar.make(settingsButton!!, "No Gallery application found", Snackbar.LENGTH_LONG).show()
                    }

                }
                return true
            }
            R.id.link_camera -> {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    // Create the File where the photo should go
                    val photoFile: File
                    try {
                        photoFile = createImageFile()
                        val photoURI = FileProvider.getUriForFile(this,
                                applicationContext.packageName + ".sfllhkhan95.doodle.provider",
                                photoFile)

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                        } catch (ex: ActivityNotFoundException) {
                            Snackbar.make(settingsButton!!, "No Camera application found",
                                    Snackbar.LENGTH_LONG).show()
                        }

                    } catch (ex: Exception) {
                        if (ContextCompat.checkSelfPermission(this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(settingsButton!!, "Permission to perform storage operations required",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Grant") {
                                        ActivityCompat.requestPermissions(this@HomeActivity,
                                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                                100)
                                    }.show()
                        }
                    }

                }
                return true
            }
        }

        return false
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val path = storageDir?.toString() + File.separator + "CAMERA_IMAGE.jpg"
        val image = File(path)
        val created = image.createNewFile()
        Log.i(DoodleApplication.TAG, if (created) "New temporary file created." else "Temporary file already exists. Overwriting!")

        // Save a file: path for use with ACTION_VIEW intents
        mCameraPicturePath = image.absolutePath
        return image
    }

    companion object {

        private const val REQUEST_TAKE_PHOTO = 100
        private const val REQUEST_PICK_PHOTO = 200
    }

}