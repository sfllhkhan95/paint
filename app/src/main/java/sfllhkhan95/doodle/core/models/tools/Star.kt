package sfllhkhan95.doodle.core.models.tools

import android.graphics.PointF
import sfllhkhan95.doodle.core.models.PaintBrush
import kotlin.math.abs

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Star internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    override fun draw(i: PointF, f: PointF) {
        var ix = i.x
        if (i.x > f.x) {
            ix = f.x
        }

        var iy = i.y
        if (i.y > f.y) {
            iy = f.y
        }

        var w = abs(i.x - f.x)
        var h = abs(i.y - f.y)
        if (w > h) h = w else w = h

        val mx = ix + w / 2
        val my = iy + h / 2

        val fx = ix + w
        val fy = iy + h

        this.reset()
        this.moveTo(mx, iy)
        this.lineTo(mx - w / 6, my - h / 6)
        this.lineTo(ix, my - h / 6)
        this.lineTo(ix + w / 4, my + h / 7)
        this.lineTo(ix + w / 6, fy)
        this.lineTo(mx, my + h / 4)
        this.lineTo(fx - w / 6, fy)
        this.lineTo(fx - w / 4, my + h / 7)
        this.lineTo(fx, my - h / 6)
        this.lineTo(mx + w / 6, my - h / 6)
        this.lineTo(mx, iy)
    }
}
