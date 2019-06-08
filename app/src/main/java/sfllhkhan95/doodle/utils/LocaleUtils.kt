package sfllhkhan95.doodle.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnSuccessListener
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

    private val locales = Array<CharSequence>(3) {
        when (it) {
            1 -> "Deutsche"
            2 -> "اردو"
            else -> "English"
        }
    }

    private val localeCodes = Array<CharSequence>(3) {
        when (it) {
            1 -> "de"
            2 -> "ur"
            else -> "en"
        }
    }

    private fun changeLocale(context: Context, locale: String) {
        val resources = context.resources

        val myLocale = Locale(locale)
        val config = resources.configuration
        config.locale = myLocale

        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun makeDialog(context: Context, onLocaleChanged: OnSuccessListener<Void>?): AlertDialog {
        return AlertDialog.Builder(context, ThemeUtils.getDialogTheme())
                .setTitle(context.getString(R.string.settings_item_locale))
                .setItems(locales) { _, which ->
                    changeLocale(context, localeCodes[which].toString())
                    onLocaleChanged?.onSuccess(null)
                }
                .create()
    }

}