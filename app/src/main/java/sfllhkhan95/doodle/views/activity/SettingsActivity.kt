package sfllhkhan95.doodle.views.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.bo.factory.DialogFactory
import sfllhkhan95.doodle.utils.LocaleUtils
import sfllhkhan95.doodle.utils.ThemeUtils
import sfllhkhan95.doodle.views.dialog.ThemeSelector


/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:28 AM
 */
class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var themeButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Configure action bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.label_settings)

        themeButton = findViewById(R.id.theme)
        themeButton?.setOnClickListener(this)

        findViewById<View>(R.id.support)?.setOnClickListener(this)
        findViewById<View>(R.id.privacy_policy)?.setOnClickListener(this)
        findViewById<View>(R.id.faqs)?.setOnClickListener(this)
        findViewById<View>(R.id.intro)?.setOnClickListener(this)
        findViewById<View>(R.id.locale)?.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        // Display ads if they are enabled
        val mAdView = this.findViewById<AdView>(R.id.adView)
        AdManager.instance.showBannerAd(mAdView, object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                mAdView.visibility = View.VISIBLE
            }
        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.theme -> ThemeSelector(this).show()

            R.id.privacy_policy -> startActivity(Intent(this, PrivacyPolicy::class.java))

            R.id.faqs -> startActivity(Intent(this, FAQsActivity::class.java))

            R.id.support -> DialogFactory.supportDialog(this).show(supportFragmentManager)

            R.id.intro -> startActivity(Intent(this@SettingsActivity, IntroActivity::class.java))

            R.id.locale -> {
                LocaleUtils.makeDialog(this, OnSuccessListener {
                    recreate()
                }).show()
            }
        }
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(try {
            LocaleUtils.configureBaseContext(base)
        } catch (ignored: Exception) {
            base
        })
    }

}