package sfllhkhan95.doodle.models.shapes

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import sfllhkhan95.doodle.models.PaintBrush
import sfllhkhan95.doodle.models.PaintCanvas
import sfllhkhan95.doodle.models.Tool
import java.util.*

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class FloodFill internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    var bitmap: Bitmap? = null

    override fun paint(canvas: PaintCanvas) {
        val start = System.currentTimeMillis()
        paintBrush.style = Paint.Style.STROKE
        paintBrush.color = paintBrush.strokeColor
        paintBrush.strokeWidth = 1.0f
        canvas.drawPath(this@FloodFill, paintBrush)
        Log.i("Doodle", String.format("FloodFill::paint() took %fs", (System.currentTimeMillis() - start) / 1000.0f))
    }

    override fun draw(i: PointF, f: PointF) {

    }

    fun execute(f: PointF) {
        bitmap?.let {
            flood(
                    f,
                    it.getPixel(f.x.toInt(), f.y.toInt()),
                    paintBrush.color
            )
        }
    }

    private val queue: Queue<PointF> = LinkedList()

    private fun isBoundary(node: PointF, targetColor: Int): Boolean {
        return ((bitmap?.getPixel(node.x.toInt() - 1, node.y.toInt()) != targetColor)
                or (bitmap?.getPixel(node.x.toInt() + 1, node.y.toInt()) != targetColor)
                or (bitmap?.getPixel(node.x.toInt(), node.y.toInt() - 1) != targetColor)
                or (bitmap?.getPixel(node.x.toInt(), node.y.toInt() + 1) != targetColor))
    }

    private fun flood(node: PointF, targetColor: Int, replacementColor: Int) {
        bitmap?.let {
            try {
                when {
                    targetColor == replacementColor -> return
                    targetColor != it.getPixel(node.x.toInt(), node.y.toInt()) -> return
                    else -> {
                        queue.clear()
                        queue.add(node)

                        while (queue.isNotEmpty()) {
                            val current = queue.poll()

                            // Process current node
                            try {
                                if (it.getPixel(current.x.toInt(), current.y.toInt()) == targetColor) {
                                    this.lineTo(current.x, current.y)

                                    it.setPixel(current.x.toInt(), current.y.toInt(), replacementColor)

                                    // Check in all four directions
                                    queue.add(PointF(current.x, current.y - 1))
                                    queue.add(PointF(current.x - 1, current.y))
                                    queue.add(PointF(current.x, current.y + 1))
                                    queue.add(PointF(current.x + 1, current.y))
                                }
                            } catch (ignored: Exception) {

                            }
                        }
                    }
                }
            } catch (ignored: Exception) {

            }
        }
    }

}