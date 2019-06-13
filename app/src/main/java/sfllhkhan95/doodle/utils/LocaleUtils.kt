package sfllhkhan95.doodle.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnSuccessListener
import com.orhanobut.hawk.Hawk
import sfllhkhan95.doodle.R
import java.util.*

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-06-09 03:08
 */
object LocaleUtils {

    private const val TAG = "APP_LANGUAGE"

    private val supportedLocales = Array<CharSequence>(5) {
        when (it) {
            1 -> "English"
            2 -> "Deutsch"
            3 -> "Français"
            4 -> "اردو"
            else -> "System Default"
        }
    }

    private fun getSelectedLocale(which: Int): Locale {
        return when (which) {
            1 -> Locale.ENGLISH
            2 -> Locale.GERMAN
            3 -> Locale.FRENCH
            4 -> Locale("ur")
            else -> Locale.getDefault()
        }
    }

    private fun selectLocale(context: Context, which: Int) {
        val locale = getSelectedLocale(which)
        changeLocale(context, locale)

        saveLocaleSettings(which)
    }

    private fun changeLocale(context: Context, locale: Locale) {
        val resources = context.resources

        val config = resources.configuration
        config.locale = locale

        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun saveLocaleSettings(which: Int) {
        Hawk.put(TAG, which)
    }

    fun restoreLocaleSettings(context: Context) {
        selectLocale(context, Hawk.get(TAG, 0))
    }

    fun makeDialog(context: Context, onLocaleChanged: OnSuccessListener<Void>?): AlertDialog {
        return AlertDialog.Builder(context, ThemeUtils.getDialogTheme())
                .setTitle(context.getString(R.string.settings_item_locale))
                .setItems(supportedLocales) { _, which ->
                    selectLocale(context, which)
                    onLocaleChanged?.onSuccess(null)
                }
                .create()
    }

}