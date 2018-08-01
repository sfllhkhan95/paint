package sfllhkhan95.doodle.core.models.tools;

import android.graphics.Paint;
import android.graphics.PointF;

import sfllhkhan95.doodle.core.models.PaintBrush;
import sfllhkhan95.doodle.core.models.PaintCanvas;

/**
 * Created by saifkhichi96 on 23/10/2017.
 */
public class Pen extends Tool {

    Pen(PaintBrush paintBrush) {
        super(paintBrush);
    }

    @Override
    public void paint(PaintCanvas canvas) {
        paintBrush.setStyle(Paint.Style.STROKE);
        paintBrush.setColor(paintBrush.getStrokeColor());
        paintBrush.setStrokeWidth(paintBrush.getSize());
        canvas.drawPath(this, paintBrush);
    }

    @Override
    public void draw(PointF i, PointF f) {
        this.lineTo(f.x, f.y);
    }
}
