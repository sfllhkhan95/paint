package sfllhkhan95.doodle.auth

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.Toolbar
import android.view.View
import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.FAQsActivity
import sfllhkhan95.doodle.PrivacyPolicy
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.core.utils.DialogFactory
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_DEFAULT
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_OCEAN
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_SUNLIGHT

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:28 AM
 */
class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var themeButton: AppCompatButton? = null
    private val themes = arrayOf<CharSequence>("Default", "Ocean", "Sunlight")

    override fun onCreate(savedInstanceState: Bundle?) {
        val which = (application as DoodleApplication).setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Configure action bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        themeButton = findViewById(R.id.theme_selected)
        themeButton!!.text = themes[which]
        themeButton!!.setOnClickListener(this)

        findViewById<View>(R.id.theme).setOnClickListener(this)
        findViewById<View>(R.id.support).setOnClickListener(this)
        findViewById<View>(R.id.privacy_policy).setOnClickListener(this)
        findViewById<View>(R.id.faqs).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.theme_selected, R.id.theme -> AlertDialog.Builder(ContextThemeWrapper(this, R.style.DialogTheme))
                    .setTitle("Choose Theme")
                    .setItems(themes) { dialogInterface, which ->
                        themeButton!!.text = themes[which]
                        var theme = THEME_DEFAULT
                        when (which) {
                            1 -> theme = THEME_OCEAN
                            2 -> theme = THEME_SUNLIGHT
                        }
                        (application as DoodleApplication)
                                .changeTheme(this@SettingsActivity, theme)
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()

            R.id.privacy_policy -> startActivity(Intent(this, PrivacyPolicy::class.java))

            R.id.faqs -> startActivity(Intent(this, FAQsActivity::class.java))

            R.id.support -> DialogFactory(this, null).supportDialog(this).show()
        }
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

}