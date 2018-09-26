package sfllhkhan95.doodle.core.models.tools

import android.graphics.PointF

import sfllhkhan95.doodle.core.models.PaintBrush

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
class Line internal constructor(paintBrush: PaintBrush) : Tool(paintBrush) {

    override fun draw(i: PointF, f: PointF) {
        this.reset()
        this.moveTo(i.x, i.y)
        this.lineTo(f.x, f.y)
    }

}
