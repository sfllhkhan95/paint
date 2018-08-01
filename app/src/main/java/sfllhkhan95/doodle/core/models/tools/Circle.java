package sfllhkhan95.doodle.core.models.tools;

import android.graphics.Path;
import android.graphics.PointF;

import sfllhkhan95.doodle.core.models.PaintBrush;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Circle extends Tool {

    Circle(PaintBrush paintBrush) {
        super(paintBrush);
    }

    @Override
    public void draw(PointF i, PointF f) {
        // Find midpoint
        float x = (i.x + f.x) / 2;
        float y = (i.y + f.y) / 2;

        // Calculate radius
        float xr = Math.abs((f.x - i.x) / 2);
        float yr = Math.abs((f.y - i.y) / 2);
        float r = (xr > yr) ? xr : yr;

        // Add circle to path
        this.reset();
        this.moveTo(i.x, i.y);
        this.addCircle(x, y, r, Path.Direction.CW);
    }
}
