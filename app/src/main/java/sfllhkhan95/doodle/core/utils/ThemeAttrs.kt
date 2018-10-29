package sfllhkhan95.doodle.core.utils

import android.content.Context
import android.support.annotation.AttrRes
import android.util.TypedValue
import sfllhkhan95.doodle.R

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 3.5.1 04/08/2018 2:58 PM
 */
object ThemeAttrs {

    const val THEME_DEFAULT = "DEFAULT"
    const val THEME_SUNLIGHT = "SUNLIGHT"
    const val THEME_OCEAN = "OCEAN"
    const val THEME_FOREST = "FOREST"
    const val THEME_CHOCOLATE = "CHOCOLATE"

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
