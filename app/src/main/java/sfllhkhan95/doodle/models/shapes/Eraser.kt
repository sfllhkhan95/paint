package sfllhkhan95.doodle.models.shapes

import android.graphics.Paint

import sfllhkhan95.doodle.models.PaintBrush
import sfllhkhan95.doodle.models.PaintCanvas

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Eraser internal constructor(paintBrush: PaintBrush) : Pen(paintBrush) {

    fun initEraser(canvas: PaintCanvas) {
        paintBrush.strokeColor = canvas.color
    }

    override fun paint(canvas: PaintCanvas) {
        paintBrush.style = Paint.Style.STROKE
        paintBrush.color = canvas.color
        paintBrush.strokeWidth = paintBrush.size * 2.5f
        canvas.drawPath(this, paintBrush)
    }
}
