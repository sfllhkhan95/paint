package sfllhkhan95.doodle.views.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.button.MaterialButton
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.bo.factory.DialogFactory
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
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        themeButton = findViewById(R.id.theme)
        themeButton?.setOnClickListener(this)

        findViewById<View>(R.id.support)?.setOnClickListener(this)
        findViewById<View>(R.id.privacy_policy)?.setOnClickListener(this)
        findViewById<View>(R.id.faqs)?.setOnClickListener(this)
        findViewById<View>(R.id.intro)?.setOnClickListener(this)
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

            R.id.support -> DialogFactory.supportDialog(this).show()

            R.id.intro -> startActivity(Intent(this@SettingsActivity, IntroActivity::class.java))
        }
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

}