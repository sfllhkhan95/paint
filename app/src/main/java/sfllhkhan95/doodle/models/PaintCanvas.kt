package sfllhkhan95.doodle.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ProjectUtils


/**
 * @author saifkhichi96
 */
class PaintCanvas(context: Context, metrics: DisplayMetrics) : Canvas() {

    private var baseBitmap: Bitmap? = null
    private var bitmapPath: String? = null
    val bitmap: Bitmap

    var color: Int = 0

    init {
        color = ContextCompat.getColor(context, DEFAULT_BG_COLOR)

        val height = metrics.heightPixels
        val width = metrics.widthPixels

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        setBitmap(bitmap)
    }

    fun drawBackground() {
        if (baseBitmap != null) {
            drawBitmap(baseBitmap!!, 0f, 0f, null)
        } else {
            drawColor(color)
        }
    }

    fun drawPaths(tools: Tools) {
        tools.paint(this)
    }

    fun getColor(touchAt: PointF): Int {
        return try {
            bitmap.getPixel(touchAt.x.toInt(), touchAt.y.toInt())
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
            DEFAULT_BG_COLOR
        }

    }

    fun saveProject() {
        bitmapPath?.let { ProjectUtils[ProjectUtils.timestamp2Name(it)] = bitmap }
                ?: ProjectUtils.create(bitmap)
    }

    fun saveProjectCopy() {
        ProjectUtils.create(bitmap)
    }

    companion object {
        private const val DEFAULT_BG_COLOR = R.color.blue_grey_500

        fun createWithBitmap(context: Context, metrics: DisplayMetrics, source: Bitmap): PaintCanvas {
            var bitmap = source
            val canvas = PaintCanvas(context, metrics)

            val deviceAspect = metrics.widthPixels / metrics.heightPixels.toFloat()
            val bmpAspect = bitmap.width / bitmap.height.toFloat()

            if (deviceAspect != bmpAspect) {
                if (deviceAspect > bmpAspect) { // Device is wider, fit width
                    val targetHeight = bitmap.width / deviceAspect
                    bitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            ((bitmap.height - targetHeight) / 2).toInt(),
                            bitmap.width,
                            targetHeight.toInt()
                    )
                } else {
                    val targetWidth = bitmap.height * deviceAspect
                    bitmap = Bitmap.createBitmap(
                            bitmap,
                            ((bitmap.width - targetWidth) / 2).toInt(),
                            0,
                            targetWidth.toInt(),
                            bitmap.height
                    )
                }

                if (metrics.widthPixels > metrics.heightPixels) {
                    val w = metrics.widthPixels
                    val h = (metrics.widthPixels * bitmap.height / bitmap.width.toFloat()).toInt()
                    canvas.baseBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false)
                } else {
                    val h = metrics.heightPixels
                    val w = (metrics.heightPixels * bitmap.width / bitmap.height.toFloat()).toInt()
                    canvas.baseBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false)
                }
            } else {
                canvas.baseBitmap = Bitmap.createScaledBitmap(bitmap, metrics.widthPixels, metrics.heightPixels, false)
            }

            return canvas
        }

        fun createWithBitmapPath(context: Context, metrics: DisplayMetrics, bitmapPath: String): PaintCanvas {
            val srcBmp = ProjectUtils.open(bitmapPath, metrics.widthPixels, metrics.heightPixels)
            val canvas = this.createWithBitmap(context, metrics, srcBmp!!)
            canvas.bitmapPath = bitmapPath

            return canvas
        }
    }

}