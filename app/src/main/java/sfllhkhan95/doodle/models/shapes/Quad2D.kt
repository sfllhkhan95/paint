package sfllhkhan95.doodle.models.shapes

import android.graphics.PointF

import sfllhkhan95.doodle.models.PaintBrush
import sfllhkhan95.doodle.models.Tool

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Quad2D internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    override fun draw(i: PointF, f: PointF) {
        this.reset()
        this.moveTo(i.x, i.y)
        this.lineTo(i.x, f.y)
        this.lineTo(f.x, f.y)
        this.lineTo(f.x, i.y)
        this.lineTo(i.x, i.y)
    }
}
