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

    private var selectedLocale = 0
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

    private fun getIndexFromLanguage(language: String): Int {
        return when (language) {
            Locale.GERMAN.language -> 1
            Locale.FRENCH.language -> 2
            Locale("ur").language -> 3
            else -> 0
        }
    }

    fun configureBaseContext(base: Context?): Context? {
        return LocaleChanger.configureBaseContext(base)
    }

    fun onConfigurationChanged() {
        LocaleChanger.onConfigurationChanged()
    }

    fun makeDialog(context: Context, onLocaleChanged: OnSuccessListener<Void>?): AlertDialog {
        selectedLocale = getIndexFromLanguage(LocaleChanger.getLocale().language)
        return AlertDialog.Builder(context, ThemeUtils.getDialogTheme())
                .setTitle(context.getString(R.string.settings_item_locale))
                .setSingleChoiceItems(supportedLocales, selectedLocale) { _, which ->
                    selectedLocale = which
                }
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    selectLocale(selectedLocale)
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