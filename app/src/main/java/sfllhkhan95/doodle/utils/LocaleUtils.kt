package sfllhkhan95.doodle.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.franmontiel.localechanger.LocaleChanger
import com.google.android.gms.tasks.OnSuccessListener
import sfllhkhan95.doodle.R
import java.util.*

/**
 * @author saifkhichi96
 * @version 1.1.0
 * @since 1.0.0 2019-06-09 03:08
 */
object LocaleUtils {

    private val supportedLocales = Array<CharSequence>(4) {
        when (it) {
            1 -> "Deutsch"
            2 -> "Français"
            3 -> "اردو"
            else -> "English"
        }
    }

    fun init(context: Context) {
        LocaleChanger.initialize(context, listOf(
                Locale.ENGLISH,
                Locale.GERMAN,
                Locale.FRENCH,
                Locale("ur")
        ))
    }

    fun configureBaseContext(base: Context?): Context? {
        return LocaleChanger.configureBaseContext(base)
    }

    fun onConfigurationChanged() {
        LocaleChanger.onConfigurationChanged()
    }

    fun makeDialog(context: Context, onLocaleChanged: OnSuccessListener<Void>?): AlertDialog {
        return AlertDialog.Builder(context, ThemeUtils.getDialogTheme())
                .setTitle(context.getString(R.string.settings_item_locale))
                .setItems(supportedLocales) { _, which ->
                    selectLocale(which)
                    onLocaleChanged?.onSuccess(null)
                }
                .create()
    }

    private fun getSelectedLocale(which: Int): Locale {
        return when (which) {
            1 -> Locale.GERMAN
            2 -> Locale.FRENCH
            3 -> Locale("ur")
            else -> Locale.ENGLISH
        }
    }

    private fun selectLocale(which: Int) {
        LocaleChanger.setLocale(getSelectedLocale(which))
    }

}