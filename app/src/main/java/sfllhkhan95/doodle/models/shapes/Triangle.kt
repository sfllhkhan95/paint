package sfllhkhan95.doodle.models.shapes

import android.graphics.PointF
import sfllhkhan95.doodle.models.PaintBrush
import sfllhkhan95.doodle.models.Tool

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Triangle internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    override fun draw(i: PointF, f: PointF) {
        var ix = i.x
        var fx = f.x
        if (i.x > f.x) {
            ix = f.x
            fx = i.x
        }

        var iy = i.y
        var fy = f.y
        if (i.y > f.y) {
            iy = f.y
            fy = i.y
        }

        this.reset()
        this.moveTo(ix + (fx - ix) / 2, iy)
        this.lineTo(ix, fy)
        this.lineTo(fx, fy)
        this.lineTo(ix + (fx - ix) / 2, iy)
    }
}
