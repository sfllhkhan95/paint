package sfllhkhan95.doodle.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import sfllhkhan95.doodle.R
import java.io.File

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-06-07 15:18
 */
object ShareUtils {

    fun createShareIntent(context: Context, file: File, contentType: String): Intent {
        // Get a shareable file URI
        val contentUri = FileProvider.getUriForFile(
                context,
                context.packageName + context.getString(R.string.provider),
                file)

        // Create share intent
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = contentType
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        return shareIntent
    }

}