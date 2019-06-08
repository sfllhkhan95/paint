package sfllhkhan95.doodle.utils

import android.content.Context
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

    const val THEME_DEFAULT = "DEFAULT"
    const val THEME_SUNLIGHT = "SUNLIGHT"
    const val THEME_OCEAN = "OCEAN"
    const val THEME_FOREST = "FOREST"
    const val THEME_CHOCOLATE = "CHOCOLATE"
    const val THEME_DARK = "DARK"

    fun getDialogTheme(): Int {
        return when (currentTheme) {
            THEME_OCEAN -> R.style.DialogTheme_Ocean
            THEME_SUNLIGHT -> R.style.DialogTheme_Sunlight
            THEME_FOREST -> R.style.DialogTheme_Forest
            THEME_CHOCOLATE -> R.style.DialogTheme_Chocolate
            THEME_DARK -> R.style.DialogTheme_Dark
            THEME_DEFAULT -> R.style.DialogTheme
            else -> R.style.DialogTheme
        }
    }

    fun setActivityTheme(activity: AppCompatActivity): Int {
        when (currentTheme) {
            THEME_OCEAN -> {
                activity.setTheme(R.style.AppTheme_Ocean)
                return 1
            }
            THEME_SUNLIGHT -> {
                activity.setTheme(R.style.AppTheme_Sunlight)
                return 2
            }
            THEME_FOREST -> {
                activity.setTheme(R.style.AppTheme_Forest)
                return 3
            }
            THEME_CHOCOLATE -> {
                activity.setTheme(R.style.AppTheme_Chocolate)
                return 4
            }
            THEME_DARK -> {
                activity.setTheme(R.style.AppTheme_Dark)
                return 5
            }
            THEME_DEFAULT -> {
                activity.setTheme(R.style.AppTheme)
                return 0
            }
            else -> {
                activity.setTheme(R.style.AppTheme)
                return 0
            }
        }
    }

    fun changeTheme(activity: AppCompatActivity, currentTheme: String) {
        when (currentTheme) {
            THEME_OCEAN -> Hawk.put(DoodleApplication.TAG_THEME, THEME_OCEAN)
            THEME_SUNLIGHT -> Hawk.put(DoodleApplication.TAG_THEME, THEME_SUNLIGHT)
            THEME_FOREST -> Hawk.put(DoodleApplication.TAG_THEME, THEME_FOREST)
            THEME_CHOCOLATE -> Hawk.put(DoodleApplication.TAG_THEME, THEME_CHOCOLATE)
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

    private fun getColor(context: Context, @AttrRes attrId: Int): Int {
        val typedValue = TypedValue()

        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(attrId))
        val color = a.getColor(0, 0)

        a.recycle()

        return color
    }

}