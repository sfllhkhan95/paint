package sfllhkhan95.doodle.core.models.tools

import android.graphics.PointF
import sfllhkhan95.doodle.core.models.PaintBrush
import kotlin.math.abs

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Diamond internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

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

        val w = abs(i.x - f.x)
        val h = abs(i.y - f.y)

        this.reset()
        this.moveTo(ix + w / 2, iy)
        this.lineTo(ix, iy + h / 2)
        this.lineTo(ix + w / 2, fy)
        this.lineTo(fx, iy + h / 2)
        this.lineTo(ix + w / 2, iy)
    }
}
