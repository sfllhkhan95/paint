package sfllhkhan95.doodle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.Toast

import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage

import pk.aspirasoft.core.db.PersistentStorage
import sfllhkhan95.doodle.core.utils.ThemeAttrs
import sfllhkhan95.doodle.projects.HomeActivity

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 06/08/2018 2:38 PM
 */
class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(SlideFragmentBuilder()
                .image(R.drawable.paintbrush)
                .backgroundColor(ThemeAttrs.colorPrimaryDark(this))
                .title(getString(R.string.slide1_title))
                .description(getString(R.string.slide1_description))
                .build())

        addSlide(SlideFragmentBuilder()
                .image(R.drawable.palette)
                .backgroundColor(resources.getColor(R.color.grey_800))
                .title(getString(R.string.slide2_title))
                .description(getString(R.string.slide2_description))
                .build())

        addSlide(SlideFragmentBuilder()
                .image(R.drawable.social)
                .backgroundColor(resources.getColor(R.color.com_facebook_messenger_blue))
                .title(getString(R.string.slide3_title))
                .description(getString(R.string.slide3_description))
                .build())

        val storagePermissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        val cameraPermissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        addSlide(SlideFragmentBuilder()
                .image(R.drawable.permissions)
                .backgroundColor(resources.getColor(R.color.red_900))
                .title(getString(R.string.slide4_title))
                .description(if (!storagePermissionGranted || !cameraPermissionGranted)
                    getString(R.string.slide4_description_permissions)
                else
                    getString(R.string.slide3_description_normal))
                .build())

        askForPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 4)

        showPagerIndicator(false)
        showStatusBar(false)
        showSkipButton(false)
        backButtonVisibilityWithDone = false
        setDoneText(if (!storagePermissionGranted || !cameraPermissionGranted)
            getString(R.string.label_intro_done_permission)
        else
            getString(R.string.label_intro_end_normal))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                var allGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false
                        break
                    }
                }

                if (allGranted) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                } else {
                    Toast.makeText(this, "Grant all permissions before continuing!", Toast.LENGTH_SHORT).show()
                    recreate()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        PersistentStorage.put("INTRO_SEEN", true)

        startActivity(Intent(applicationContext, HomeActivity::class.java))
        overridePendingTransition(0, 0)
        finish()
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private inner class SlideFragmentBuilder {
        private val fragment: SliderPage = SliderPage()

        fun image(@DrawableRes imageId: Int): SlideFragmentBuilder {
            fragment.imageDrawable = imageId
            return this
        }

        fun title(title: String): SlideFragmentBuilder {
            fragment.title = title
            return this
        }

        fun description(description: String): SlideFragmentBuilder {
            fragment.description = description
            return this
        }

        fun backgroundColor(color: Int): SlideFragmentBuilder {
            fragment.bgColor = color
            return this
        }

        fun build(): AppIntroFragment {
            return AppIntroFragment.newInstance(fragment)
        }
    }

}