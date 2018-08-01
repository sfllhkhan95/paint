package sfllhkhan95.doodle.core.models.tools;

import android.graphics.PointF;

import sfllhkhan95.doodle.core.models.PaintBrush;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Quad2D extends Tool {

    Quad2D(PaintBrush paintBrush) {
        super(paintBrush);
    }

    @Override
    public void draw(PointF i, PointF f) {
        this.reset();
        this.moveTo(i.x, i.y);
        this.lineTo(i.x, f.y);
        this.lineTo(f.x, f.y);
        this.lineTo(f.x, i.y);
        this.lineTo(i.x, i.y);
    }
}
