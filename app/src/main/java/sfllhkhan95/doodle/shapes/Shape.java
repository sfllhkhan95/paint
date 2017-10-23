package sfllhkhan95.doodle.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import sfllhkhan95.doodle.core.PaintBrush;
import sfllhkhan95.doodle.view.PaintView;

/**
 * Shape represents a single path drawn on the canvas. Characteristics of the path, including
 * its shape, color, etc. are stored in this class.
 *
 * @author alichishti
 * @version 1.0
 * @see PaintView
 */
public abstract class Shape extends Path {

    final PaintBrush paintBrush;

    Shape(PaintBrush paintBrush) {
        this.paintBrush = paintBrush.clone();
        this.reset();
    }

    public abstract void draw(PointF a, PointF b);

    public void paint(Canvas canvas) {
        paintBrush.setStyle(Paint.Style.STROKE);
        paintBrush.setColor(paintBrush.getStrokeColor());
        paintBrush.setStrokeWidth(paintBrush.getSize());
        canvas.drawPath(this, paintBrush);

        if (paintBrush.isFilled()) {
            paintBrush.setStyle(Paint.Style.FILL);
            paintBrush.setColor(paintBrush.getFillColor());
            canvas.drawPath(this, paintBrush);
        }
    }

}
