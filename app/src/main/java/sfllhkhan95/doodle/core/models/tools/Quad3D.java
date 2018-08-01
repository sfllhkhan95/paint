package sfllhkhan95.doodle.core.models.tools;

import android.graphics.PointF;

import sfllhkhan95.doodle.core.models.PaintBrush;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Quad3D extends Tool {

    Quad3D(PaintBrush paintBrush) {
        super(paintBrush);
    }

    @Override
    public void draw(PointF i, PointF f) {
        this.reset();
        this.moveTo(i.x, i.y);

        // Front
        this.lineTo(i.x, f.y);
        this.lineTo(f.x, f.y);
        this.lineTo(f.x, i.y);
        this.lineTo(i.x, i.y);

        // Back
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2);
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2);
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2);
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2);
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2);

        // Left
        this.lineTo(i.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2);
        this.lineTo(i.x, f.y);

        // Right
        this.lineTo(f.x, f.y);
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, f.y + Math.abs(f.y - i.y) / 2);
        this.lineTo(f.x + Math.abs(f.x - i.x) / 2, i.y + Math.abs(f.y - i.y) / 2);
        this.lineTo(f.x, i.y);
    }
}
