package sfllhkhan95.doodle.views.dialog

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ThemeUtils
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_DARK
import sfllhkhan95.doodle.utils.ThemeUtils.THEME_DEFAULT

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 01/11/2018 3:46 PM
 */
class ThemeSelector(context: AppCompatActivity) {

    private var selectedTheme: String? = null
    private val dialog: Dialog = AlertDialog.Builder(context, ThemeUtils.getDialogTheme())
            .setTitle(R.string.settings_item_theme)
            .setSingleChoiceItems(
                    arrayOf(context.getString(R.string.theme_light),
                            context.getString(R.string.theme_dark)),
                    if (ThemeUtils.currentTheme == THEME_DEFAULT) 0 else 1)
            { _, which ->
                selectedTheme = if (which == 0) THEME_DEFAULT else THEME_DARK
            }
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                selectedTheme?.let { ThemeUtils.changeTheme(context, it) }
                dialog.dismiss()
            }
            .create()

    fun show() {
        dialog.show()
    }

}