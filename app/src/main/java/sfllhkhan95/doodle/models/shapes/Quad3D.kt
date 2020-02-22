package sfllhkhan95.doodle.models.shapes

import android.graphics.Paint
import android.graphics.PointF

import sfllhkhan95.doodle.models.PaintBrush
import sfllhkhan95.doodle.models.PaintCanvas
import sfllhkhan95.doodle.models.Tool

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Quad3D internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    override fun draw(i: PointF, f: PointF) {
        this.reset()
        this.moveTo(i.x, i.y)

        // Front
        this.lineTo(i.x, f.y)
        this.lineTo(f.x, f.y)
        this.lineTo(f.x, i.y)
        this.lineTo(i.x, i.y)

        // Back
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2)
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2)
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2)
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2)
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2)

        // Left
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2)
        this.lineTo(i.x, f.y)

        // Right
        this.lineTo(f.x, f.y)
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2)
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2)
        this.lineTo(f.x, i.y)
    }

    override fun paint(canvas: PaintCanvas) {
        paintBrush.style = Paint.Style.FILL
        paintBrush.color = paintBrush.fillColor
        canvas.drawPath(this, paintBrush)

        paintBrush.style = Paint.Style.STROKE
        paintBrush.color = paintBrush.strokeColor
        paintBrush.strokeWidth = paintBrush.size.toFloat()
        canvas.drawPath(this, paintBrush)
    }

}
