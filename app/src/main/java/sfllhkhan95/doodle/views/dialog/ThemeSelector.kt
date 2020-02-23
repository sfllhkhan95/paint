package sfllhkhan95.doodle.views.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
class ThemeSelector(private val ownerActivity: AppCompatActivity) : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var v: View
    private var actionbar: TextView? = null

    private var selectedTheme: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, ThemeUtils.getDialogTheme())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.dialog_theme_selector, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        actionbar = v.findViewById(R.id.actionbar)

        v.findViewById<View>(R.id.defaultTheme).setOnClickListener(this)
        v.findViewById<View>(R.id.oceanTheme).setOnClickListener(this)
        v.findViewById<View>(R.id.sunlightTheme).setOnClickListener(this)
        v.findViewById<View>(R.id.forestTheme).setOnClickListener(this)
        v.findViewById<View>(R.id.chocolateTheme).setOnClickListener(this)
        v.findViewById<View>(R.id.darkTheme).setOnClickListener(this)

        v.findViewById<View>(R.id.cancel_button).setOnClickListener { dialog?.cancel() }

        v.findViewById<View>(R.id.ok_button).setOnClickListener {
            dismiss()
            ThemeUtils.changeTheme(ownerActivity, selectedTheme!!)
        }

        selectedTheme = ThemeUtils.currentTheme
        updateUI(selectedTheme!!)

        // Display ads if they are enabled
        val mAdView = v.findViewById<AdView>(R.id.adView)
        AdManager.instance.showBannerAd(mAdView, object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                mAdView.visibility = View.VISIBLE
            }
        })

        return v
    }

    fun show() {
        super.show(ownerActivity.supportFragmentManager, "theme_selecter")
    }

    private fun updateUI(theme: String) {
        v.findViewById<View>(R.id.defaultThemeSelected).visibility = View.INVISIBLE
        v.findViewById<View>(R.id.oceanThemeSelected).visibility = View.INVISIBLE
        v.findViewById<View>(R.id.sunlightThemeSelected).visibility = View.INVISIBLE
        v.findViewById<View>(R.id.forestThemeSelected).visibility = View.INVISIBLE
        v.findViewById<View>(R.id.chocolateThemeSelected).visibility = View.INVISIBLE
        v.findViewById<View>(R.id.darkThemeSelected).visibility = View.INVISIBLE

        actionbar!!.text = theme
        when (theme) {
            THEME_DEFAULT -> {
                actionbar!!.setTextColor(ContextCompat.getColor(ownerActivity, R.color.purple_900))
                v.findViewById<View>(R.id.defaultThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_DEFAULT
            }

            THEME_OCEAN -> {
                actionbar!!.setTextColor(ContextCompat.getColor(ownerActivity, R.color.blue_900))
                v.findViewById<View>(R.id.oceanThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_OCEAN
            }

            THEME_SUNLIGHT -> {
                actionbar!!.setTextColor(ContextCompat.getColor(ownerActivity, R.color.yellow_900))
                v.findViewById<View>(R.id.sunlightThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_SUNLIGHT
            }

            THEME_FOREST -> {
                actionbar!!.setTextColor(ContextCompat.getColor(ownerActivity, R.color.green_900))
                v.findViewById<View>(R.id.forestThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_FOREST
            }

            THEME_CHOCOLATE -> {
                actionbar!!.setTextColor(ContextCompat.getColor(ownerActivity, R.color.brown_900))
                v.findViewById<View>(R.id.chocolateThemeSelected).visibility = View.VISIBLE
                selectedTheme = THEME_CHOCOLATE
            }

            THEME_DARK -> {
                actionbar!!.setTextColor(ContextCompat.getColor(ownerActivity, R.color.grey_900))
                v.findViewById<View>(R.id.darkThemeSelected).visibility = View.VISIBLE
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