package sfllhkhan95.doodle.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Created by saifkhichi96 on 30/10/2017.
 */

object BitmapUtils {

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

    /**
     * Load a bitmap from a file path. If the specified file path cannot be decoded
     * into a bitmap the function returns null.
     *
     * @param [path] complete path name for the file to be decoded.
     *
     * @return the resulting decoded bitmap, or null if it could not be decoded.
     */
    fun openFromPath(path: String): Bitmap? {
        return BitmapFactory.decodeFile(path)
    }

    /**
     * Load a bitmap of specified size from a file path. If the specified file path
     * cannot be decoded into a bitmap the function returns null.
     *
     * @param [path] complete path name for the file to be decoded.
     * @param [width] width of decoded bitmap in pixels.
     * @param [height] height of decoded bitmap in pixels.
     *
     * @return the resulting decoded bitmap, or null if it could not be decoded.
     */
    fun openFromPath(path: String, width: Int, height: Int): Bitmap? {
        return try {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, width, height)

            // Decode bitmap with inSampleSize create
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565
            BitmapFactory.decodeFile(path, options)
        } catch (ex: Exception) {
            null
        }
    }

}