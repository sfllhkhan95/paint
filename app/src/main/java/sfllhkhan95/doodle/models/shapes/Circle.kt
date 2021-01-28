package sfllhkhan95.doodle.models.shapes

import android.graphics.PointF
import sfllhkhan95.doodle.models.PaintBrush
import sfllhkhan95.doodle.models.Tool
import kotlin.math.abs

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Circle internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    override fun draw(i: PointF, f: PointF) {
        // Find midpoint
        val x = (i.x + f.x) / 2
        val y = (i.y + f.y) / 2

        // Calculate radius
        val xr = abs((f.x - i.x) / 2)
        val yr = abs((f.y - i.y) / 2)
        val r = if (xr > yr) xr else yr

        // Add circle to path
        this.reset()
        this.moveTo(i.x, i.y)
        this.addCircle(x, y, r, Direction.CW)
    }
}
