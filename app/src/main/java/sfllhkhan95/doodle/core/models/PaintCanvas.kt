package sfllhkhan95.doodle.core.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.util.DisplayMetrics

import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.projects.utils.DoodleDatabase


/**
 * @author saifkhichi96
 */
class PaintCanvas(context: Context, metrics: DisplayMetrics) : Canvas() {
    var color: Int = 0

    private var bgImage: Bitmap? = null
    val bitmap: Bitmap
    private var projectName: String? = null

    init {
        color = context.resources.getColor(DEFAULT_BG_COLOR)

        val height = metrics.heightPixels
        val width = metrics.widthPixels

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        setBitmap(bitmap)
    }

    fun drawShapes(tools: Tools) {
        tools.paint(this)
    }

    fun drawBackground() {
        if (bgImage == null) {
            drawColor(color)
        } else {
            drawBitmap(bgImage!!, 0f, 0f, null)
        }
    }

    private fun setProjectName(title: String) {
        this.projectName = title
    }

    fun clearProjectName() {
        this.projectName = null
    }

    fun saveProject() {
        if (projectName == null) {
            DoodleDatabase.saveDoodle(bitmap)
        } else {
            DoodleDatabase.saveDoodle(bitmap, projectName!!)
        }
    }

    fun getColor(touchAt: PointF): Int {
        try {
            return bitmap.getPixel(touchAt.x.toInt(), touchAt.y.toInt())
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
            return DEFAULT_BG_COLOR
        }

    }

    companion object {

        private val DEFAULT_BG_COLOR = R.color.blue_grey_500

        fun loadFromBitmap(context: Context, metrics: DisplayMetrics, srcBmp: Bitmap): PaintCanvas {
            var srcBmp = srcBmp
            val canvas = PaintCanvas(context, metrics)

            val deviceAspect = metrics.widthPixels / metrics.heightPixels.toFloat()
            val bmpAspect = srcBmp.width / srcBmp.height.toFloat()

            if (deviceAspect != bmpAspect) {
                if (deviceAspect > bmpAspect) { // Device is wider, fit width
                    val targetHeight = srcBmp.width / deviceAspect
                    srcBmp = Bitmap.createBitmap(
                            srcBmp,
                            0,
                            ((srcBmp.height - targetHeight) / 2).toInt(),
                            srcBmp.width,
                            targetHeight.toInt()
                    )
                } else {
                    val targetWidth = srcBmp.height * deviceAspect
                    srcBmp = Bitmap.createBitmap(
                            srcBmp,
                            ((srcBmp.width - targetWidth) / 2).toInt(),
                            0,
                            targetWidth.toInt(),
                            srcBmp.height
                    )
                }

                if (metrics.widthPixels > metrics.heightPixels) {
                    val w = metrics.widthPixels
                    val h = (metrics.widthPixels * srcBmp.height / srcBmp.width.toFloat()).toInt()
                    canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, w, h, false)
                } else {
                    val h = metrics.heightPixels
                    val w = (metrics.heightPixels * srcBmp.width / srcBmp.height.toFloat()).toInt()
                    canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, w, h, false)
                }
            } else {
                canvas.bgImage = Bitmap.createScaledBitmap(srcBmp, metrics.widthPixels, metrics.heightPixels, false)
            }

            return canvas
        }

        fun loadFromPath(context: Context, metrics: DisplayMetrics, bmpPath: String): PaintCanvas {
            val srcBmp = DoodleDatabase.loadDoodle(bmpPath, metrics.widthPixels, metrics.heightPixels)
            val canvas = loadFromBitmap(context, metrics, srcBmp!!)
            canvas.setProjectName(bmpPath)

            return canvas
        }
    }
}