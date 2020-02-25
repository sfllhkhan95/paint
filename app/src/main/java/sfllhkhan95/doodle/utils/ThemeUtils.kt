package sfllhkhan95.doodle.utils

import android.content.Context
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.hawk.Hawk
import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.R

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 3.5.1 04/08/2018 2:58 PM
 */
object ThemeUtils {

    val currentTheme: String
        get() {
            var currentTheme: String? = Hawk.get(DoodleApplication.TAG_THEME, null)
            if (currentTheme == null) currentTheme = THEME_DEFAULT
            return currentTheme
        }

    const val THEME_DEFAULT = "LIGHT"
    const val THEME_DARK = "DARK"

    fun getDialogTheme(): Int {
        return when (currentTheme) {
            THEME_DARK -> R.style.DialogTheme_Dark
            THEME_DEFAULT -> R.style.DialogTheme
            else -> R.style.DialogTheme
        }
    }

    fun setActivityTheme(activity: AppCompatActivity, immersive: Boolean = false): Int {
        when (currentTheme) {
            THEME_DARK -> {
                if (immersive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    activity.setTheme(R.style.AppTheme_Dark_Immersive)
                } else {
                    activity.setTheme(R.style.AppTheme_Dark)
                }
                return 5
            }
            THEME_DEFAULT -> {
                if (immersive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    activity.setTheme(R.style.AppTheme_Immersive)
                } else {
                    activity.setTheme(R.style.AppTheme)
                }
                return 0
            }
            else -> {
                if (immersive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    activity.setTheme(R.style.AppTheme_Immersive)
                } else {
                    activity.setTheme(R.style.AppTheme)
                }
                return 0
            }
        }
    }

    fun changeTheme(activity: AppCompatActivity, currentTheme: String) {
        when (currentTheme) {
            THEME_DARK -> Hawk.put(DoodleApplication.TAG_THEME, THEME_DARK)
            THEME_DEFAULT -> Hawk.put(DoodleApplication.TAG_THEME, THEME_DEFAULT)
            else -> Hawk.put(DoodleApplication.TAG_THEME, THEME_DEFAULT)
        }
        activity.recreate()
    }

    fun colorPrimary(context: Context): Int {
        return getColor(context, R.attr.colorPrimary)
    }

    fun colorPrimaryDark(context: Context): Int {
        return getColor(context, R.attr.colorPrimaryDark)
    }

    fun colorAccent(context: Context): Int {
        return getColor(context, R.attr.colorAccent)
    }

    fun colorTextPrimary(context: Context): Int {
        return getColor(context, android.R.attr.textColorPrimary)
    }

    fun colorTextSecondary(context: Context): Int {
        return getColor(context, android.R.attr.textColorSecondary)
    }

    fun colorBackground(context: Context): Int {
        return getColor(context, android.R.attr.colorBackground)
    }

    private fun getColor(context: Context, @AttrRes attrId: Int): Int {
        val typedValue = TypedValue()

        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(attrId))
        val color = a.getColor(0, 0)

        a.recycle()

        return color
    }

}