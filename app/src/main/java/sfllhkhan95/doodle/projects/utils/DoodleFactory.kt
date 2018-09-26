package sfllhkhan95.doodle.projects.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Created by saifkhichi96 on 30/10/2017.
 */

object DoodleFactory {

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    internal fun loadFromPath(path: String): Bitmap {
        return BitmapFactory.decodeFile(path)
    }

    fun loadFromPath(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inDither = true
        return BitmapFactory.decodeFile(path, options)
    }

}
