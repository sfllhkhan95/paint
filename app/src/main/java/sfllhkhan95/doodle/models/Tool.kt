package sfllhkhan95.doodle.models

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import sfllhkhan95.doodle.views.PaintView

/**
 * Tool represents a single path drawn on the canvas. Characteristics of the path, including
 * its shape, color, etc. are stored in this class.
 *
 * @author alichishti
 * @version 1.0
 * @see PaintView
 */
abstract class Tool internal constructor(paintBrush: PaintBrush) : Path() {

    protected val paintBrush: PaintBrush = paintBrush.clone()

    init {
        this.reset()
    }

    abstract fun draw(i: PointF, f: PointF)

    open fun paint(canvas: PaintCanvas) {
        paintBrush.style = Paint.Style.STROKE
        paintBrush.color = paintBrush.strokeColor
        paintBrush.strokeWidth = paintBrush.size.toFloat()
        canvas.drawPath(this, paintBrush)

        paintBrush.style = Paint.Style.FILL
        paintBrush.color = paintBrush.fillColor
        canvas.drawPath(this, paintBrush)
    }

}
