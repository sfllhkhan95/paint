package sfllhkhan95.doodle.views.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.AdManager
import sfllhkhan95.doodle.utils.ThemeUtils
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_CHOCOLATE
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_DARK
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_DEFAULT
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_FOREST
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_OCEAN
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_SUNLIGHT

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 01/11/2018 3:46 PM
 */
class ThemeSelector(private val ownerActivity: AppCompatActivity) : Dialog(ownerActivity), View.OnClickListener {

    private var actionbar: TextView? = null
    private var window: View? = null
    private var body: View? = null
    private var positiveButton: Button? = null
    private var negativeButton: Button? = null

    private var selectedTheme: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_theme_selector)

        actionbar = findViewById(R.id.actionbar)
        window = findViewById(R.id.window)
        body = findViewById(R.id.body)
        positiveButton = findViewById(R.id.positiveButton)
        negativeButton = findViewById(R.id.negativeButton)

        findViewById<View>(R.id.defaultTheme).setOnClickListener(this)
        findViewById<View>(R.id.oceanTheme).setOnClickListener(this)
        findViewById<View>(R.id.sunlightTheme).setOnClickListener(this)
        findViewById<View>(R.id.forestTheme).setOnClickListener(this)
        findViewById<View>(R.id.chocolateTheme).setOnClickListener(this)
        findViewById<View>(R.id.darkTheme).setOnClickListener(this)

        findViewById<View>(R.id.cancel_button).setOnClickListener { cancel() }

        findViewById<View>(R.id.ok_button).setOnClickListener {
            dismiss()
            ThemeUtils.changeTheme(ownerActivity, selectedTheme!!)
        }

        selectedTheme = ThemeUtils.currentTheme
        updateUI(selectedTheme!!)

        // Display ads if they are enabled
        val mAdView = this.findViewById<AdView>(R.id.adView)
        AdManager.instance.showBannerAd(mAdView, object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                mAdView.visibility = View.VISIBLE
            }
        })
    }

    private fun updateUI(theme: String) {
        findViewById<View>(R.id.defaultThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.oceanThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.sunlightThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.forestThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.chocolateThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.darkThemeSelected).visibility = View.INVISIBLE
        negativeButton!!.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))

        actionbar!!.text = theme
        when (theme) {
            THEME_DEFAULT -> {
                window!!.setBackgroundColor(ContextCompat.getColor(context, R.color.deep_purple_50))
                body!!.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_100))
                positiveButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_900))
                negativeButton!!.setTextColor(ContextCompat.getColor(context, R.color.purple_900))
                findViewById<View>(R.id.defaultThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_DEFAULT
            }

            THEME_OCEAN -> {
                window!!.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_50))
                body!!.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_100))
                positiveButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_900))
                negativeButton!!.setTextColor(ContextCompat.getColor(context, R.color.blue_900))
                findViewById<View>(R.id.oceanThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_OCEAN
            }

            THEME_SUNLIGHT -> {
                window!!.setBackgroundColor(ContextCompat.getColor(context, R.color.orange_50))
                body!!.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_100))
                positiveButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_900))
                negativeButton!!.setTextColor(ContextCompat.getColor(context, R.color.yellow_900))
                findViewById<View>(R.id.sunlightThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_SUNLIGHT
            }

            THEME_FOREST -> {
                window!!.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green_50))
                body!!.setBackgroundColor(ContextCompat.getColor(context, R.color.green_100))
                positiveButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.green_900))
                negativeButton!!.setTextColor(ContextCompat.getColor(context, R.color.green_900))
                findViewById<View>(R.id.forestThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_FOREST
            }

            THEME_CHOCOLATE -> {
                window!!.setBackgroundColor(ContextCompat.getColor(context, R.color.deep_orange_50))
                body!!.setBackgroundColor(ContextCompat.getColor(context, R.color.brown_100))
                positiveButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.brown_900))
                negativeButton!!.setTextColor(ContextCompat.getColor(context, R.color.brown_900))
                findViewById<View>(R.id.chocolateThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_CHOCOLATE
            }

            THEME_DARK -> {
                window!!.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_600))
                body!!.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_400))
                positiveButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_900))
                negativeButton!!.setTextColor(ContextCompat.getColor(context, R.color.grey_900))
                findViewById<View>(R.id.darkThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_DARK
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.defaultTheme -> updateUI(THEME_DEFAULT)

            R.id.oceanTheme -> updateUI(THEME_OCEAN)

            R.id.sunlightTheme -> updateUI(THEME_SUNLIGHT)

            R.id.forestTheme -> updateUI(THEME_FOREST)

            R.id.chocolateTheme -> updateUI(THEME_CHOCOLATE)

            R.id.darkTheme -> updateUI(THEME_DARK)
        }
    }
}