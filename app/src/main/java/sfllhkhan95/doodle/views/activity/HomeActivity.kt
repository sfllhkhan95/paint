package sfllhkhan95.doodle.views.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.yalantis.ucrop.UCrop
import sfllhkhan95.doodle.DoodleApplication.Companion.FILE_CAMERA
import sfllhkhan95.doodle.DoodleApplication.Companion.FILE_CROPPED
import sfllhkhan95.doodle.DoodleApplication.Companion.PROJECT_FROM_IMAGE
import sfllhkhan95.doodle.DoodleApplication.Companion.REQUEST_ALL_PERMISSIONS
import sfllhkhan95.doodle.DoodleApplication.Companion.REQUEST_PHOTO_CAPTURE
import sfllhkhan95.doodle.DoodleApplication.Companion.REQUEST_PHOTO_PICK
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.bo.ProjectInflater
import sfllhkhan95.doodle.utils.FileUtils
import sfllhkhan95.doodle.utils.LocaleUtils
import sfllhkhan95.doodle.utils.ThemeUtils
import sfllhkhan95.doodle.views.dialog.ConfirmationDialog
import java.io.File

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 16/06/2018 12:10 AM
 */
class HomeActivity : AppCompatActivity(), SpeedDialView.OnActionSelectedListener {

    private var projectInflater: ProjectInflater? = null

    private var mCameraPicturePath: String? = null

    private var mAdManager: AdManager? = null
    private var settingsButton: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        projectInflater = ProjectInflater(this)

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

        // Display ads if they are enabled
        mAdManager = AdManager.instance
        mAdManager?.loadVideoAd(this)

        val mAdView = this.findViewById<AdView>(R.id.adView)
        AdManager.instance.showBannerAd(mAdView, object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                mAdView.visibility = View.VISIBLE
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // Inflate thumbnails of saved projects
        projectInflater?.let {
            runOnUiThread(it)
        }
    }

    override fun onBackPressed() {
        val mBuilder = ConfirmationDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setHeadline(getString(R.string.label_exit))
                .setTitle(getString(R.string.confirm_quit))
                .setMessage(getString(R.string.desc_prompt_quit))
                .setPositiveButton(getString(android.R.string.yes), View.OnClickListener { super@HomeActivity.onBackPressed() }, true)
                .setNegativeButton(getString(android.R.string.cancel), View.OnClickListener { /* no-op */ }, true)

        mAdManager?.let { ads ->
            if (ads.isVideoAdLoaded) {
                mBuilder.setMessage(getString(R.string.desc_prompt_watch_ad))
                        .setNegativeButton(getString(R.string.label_watch_ad), View.OnClickListener { ads.showVideoAd() }, false)
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
        if (resultCode != RESULT_OK) return
        when (requestCode) {
            REQUEST_PHOTO_PICK -> data?.data?.let { source ->
                val photoFile = FileUtils.createImageFile(this, FILE_CROPPED)
                val destination = Uri.fromFile(photoFile)

                val size = Point()
                window.windowManager.defaultDisplay.getSize(size)

                UCrop.of(source, destination)
                        .withAspectRatio(9.0F, 16.0F)
                        .withMaxResultSize(size.x, size.y)
                        .start(this)
            }
            REQUEST_PHOTO_CAPTURE -> Uri.fromFile(File(mCameraPicturePath))?.let { source ->
                val photoFile = FileUtils.createImageFile(this, FILE_CROPPED)
                val destination = Uri.fromFile(photoFile)

                val size = Point()
                window.windowManager.defaultDisplay.getSize(size)

                UCrop.of(source, destination)
                        .withAspectRatio(9.0F, 16.0F)
                        .withMaxResultSize(size.x, size.y)
                        .start(this)
            }
            UCrop.REQUEST_CROP -> {
                if (data != null) {
                    val openGalleryImage = Intent(applicationContext, MainActivity::class.java)
                    openGalleryImage.putExtra(PROJECT_FROM_IMAGE, UCrop.getOutput(data))
                    startActivity(openGalleryImage)
                }
            }
        }
    }

    override fun onActionSelected(speedDialActionItem: SpeedDialActionItem): Boolean {
        when (speedDialActionItem.id) {
            R.id.link_blank -> {
                val blankProjectIntent = Intent(this, MainActivity::class.java)
                startActivity(blankProjectIntent)
                return true
            }
            R.id.link_gallery -> {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                if (intent.resolveActivity(packageManager) != null) {
                    try {
                        startActivityForResult(intent, REQUEST_PHOTO_PICK)
                    } catch (ex: ActivityNotFoundException) {
                        Snackbar.make(settingsButton!!, getString(R.string.error_no_gallery), Snackbar.LENGTH_LONG).show()
                    }

                }
                return true
            }
            R.id.link_camera -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null) {
                    try {
                        val photoFile: File = FileUtils.createImageFile(this, FILE_CAMERA)
                        mCameraPicturePath = photoFile.absolutePath

                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(this,
                                        applicationContext.packageName + getString(R.string.provider),
                                        photoFile))

                        startActivityForResult(intent, REQUEST_PHOTO_CAPTURE)

                    } catch (ex: ActivityNotFoundException) {
                        Snackbar.make(settingsButton!!, getString(R.string.error_no_camera),
                                Snackbar.LENGTH_LONG).show()

                    } catch (ex: Exception) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(settingsButton!!, getString(R.string.prompt_permissions),
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getString(R.string.label_intro_done_permission)) {
                                        ActivityCompat.requestPermissions(this@HomeActivity,
                                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                                REQUEST_ALL_PERMISSIONS)
                                    }.show()
                        }
                    }
                }
                return true
            }
        }

        return false
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(try {
            LocaleUtils.configureBaseContext(base)
        } catch (ignored: Exception) {
            base
        })
    }

}