package sfllhkhan95.doodle.core.models.tools

import android.graphics.Paint
import android.graphics.PointF

import sfllhkhan95.doodle.core.models.PaintBrush
import sfllhkhan95.doodle.core.models.PaintCanvas

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
open class Pen internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    override fun paint(canvas: PaintCanvas) {
        paintBrush.style = Paint.Style.STROKE
        paintBrush.color = paintBrush.strokeColor
        paintBrush.strokeWidth = paintBrush.size.toFloat()
        canvas.drawPath(this, paintBrush)
    }

    override fun draw(i: PointF, f: PointF) {
        this.lineTo(f.x, f.y)
    }
}
