package sfllhkhan95.doodle.core.views

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.R

import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_CHOCOLATE
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_DEFAULT
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_FOREST
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_OCEAN
import sfllhkhan95.doodle.core.utils.ThemeAttrs.THEME_SUNLIGHT

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 01/11/2018 3:46 PM
 */
class ThemeSelector(context: Activity) : Dialog(context), View.OnClickListener {

    private var actionbar: TextView? = null
    private var window: View? = null
    private var body: View? = null
    private var positiveButton: Button? = null
    private var negativeButton: Button? = null

    private var selectedTheme: String? = null

    init {
        ownerActivity = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        findViewById<View>(R.id.cancel_button).setOnClickListener { cancel() }

        findViewById<View>(R.id.ok_button).setOnClickListener {
            dismiss()
            (ownerActivity!!.application as DoodleApplication)
                    .changeTheme(ownerActivity!!, selectedTheme!!)
        }

        selectedTheme = (ownerActivity!!.application as DoodleApplication).currentTheme
        updateUI((ownerActivity!!.application as DoodleApplication).currentTheme)
    }

    private fun updateUI(theme: String) {
        findViewById<View>(R.id.defaultThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.oceanThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.sunlightThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.forestThemeSelected).visibility = View.INVISIBLE
        findViewById<View>(R.id.chocolateThemeSelected).visibility = View.INVISIBLE
        negativeButton!!.setBackgroundColor(context.resources.getColor(android.R.color.transparent))

        actionbar!!.text = theme
        when (theme) {
            THEME_DEFAULT -> {
                actionbar!!.setBackgroundColor(context.resources.getColor(R.color.purple_900))
                window!!.setBackgroundColor(context.resources.getColor(R.color.deep_purple_50))
                body!!.setBackgroundColor(context.resources.getColor(R.color.purple_100))
                positiveButton!!.setBackgroundColor(context.resources.getColor(R.color.purple_900))
                negativeButton!!.setTextColor(context.resources.getColor(R.color.purple_900))
                findViewById<View>(R.id.defaultThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_DEFAULT
            }

            THEME_OCEAN -> {
                actionbar!!.setBackgroundColor(context.resources.getColor(R.color.blue_900))
                window!!.setBackgroundColor(context.resources.getColor(R.color.blue_grey_50))
                body!!.setBackgroundColor(context.resources.getColor(R.color.blue_100))
                positiveButton!!.setBackgroundColor(context.resources.getColor(R.color.blue_900))
                negativeButton!!.setTextColor(context.resources.getColor(R.color.blue_900))
                findViewById<View>(R.id.oceanThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_OCEAN
            }

            THEME_SUNLIGHT -> {
                actionbar!!.setBackgroundColor(context.resources.getColor(R.color.yellow_900))
                window!!.setBackgroundColor(context.resources.getColor(R.color.orange_50))
                body!!.setBackgroundColor(context.resources.getColor(R.color.yellow_100))
                positiveButton!!.setBackgroundColor(context.resources.getColor(R.color.yellow_900))
                negativeButton!!.setTextColor(context.resources.getColor(R.color.yellow_900))
                findViewById<View>(R.id.sunlightThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_SUNLIGHT
            }

            THEME_FOREST -> {
                actionbar!!.setBackgroundColor(context.resources.getColor(R.color.green_900))
                window!!.setBackgroundColor(context.resources.getColor(R.color.light_green_50))
                body!!.setBackgroundColor(context.resources.getColor(R.color.green_100))
                positiveButton!!.setBackgroundColor(context.resources.getColor(R.color.green_900))
                negativeButton!!.setTextColor(context.resources.getColor(R.color.green_900))
                findViewById<View>(R.id.forestThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_FOREST
            }

            THEME_CHOCOLATE -> {
                actionbar!!.setBackgroundColor(context.resources.getColor(R.color.brown_900))
                window!!.setBackgroundColor(context.resources.getColor(R.color.deep_orange_50))
                body!!.setBackgroundColor(context.resources.getColor(R.color.brown_100))
                positiveButton!!.setBackgroundColor(context.resources.getColor(R.color.brown_900))
                negativeButton!!.setTextColor(context.resources.getColor(R.color.brown_900))
                findViewById<View>(R.id.chocolateThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_CHOCOLATE
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
        }
    }
}