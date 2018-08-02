package sfllhkhan95.doodle.core.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import sfllhkhan95.doodle.core.models.PaintBrush;
import sfllhkhan95.doodle.core.models.PaintCanvas;
import sfllhkhan95.doodle.core.models.Tools;
import sfllhkhan95.doodle.core.models.tools.ColorPicker;
import sfllhkhan95.doodle.core.models.tools.Eraser;
import sfllhkhan95.doodle.core.models.tools.Tool;
import sfllhkhan95.doodle.core.models.tools.ToolFactory;
import sfllhkhan95.doodle.core.utils.CanvasActionListener;
import sfllhkhan95.doodle.core.utils.OnColorPickedListener;


/**
 * @author alichishti
 */
public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 5;
    private final Tools tools = new Tools();
    private final PaintBrush mBrush;

    private PaintCanvas mCanvas;
    private PointF iTouch = new PointF();
    private PointF fTouch = new PointF();
    private Class<? extends Tool> shapeType = null;

    private CanvasActionListener canvasActionListener;
    private OnColorPickedListener onColorPickedListener;

    public PaintView(Context context) {
        super(context);
        mBrush = new PaintBrush(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBrush = new PaintBrush(context);
    }

    public PaintBrush getBrush() {
        return mBrush;
    }

    public PaintCanvas getCanvas() {
        return mCanvas;
    }

    public void setCanvas(PaintCanvas canvas) {
        mCanvas = canvas;
    }

    public void setShapeType(Class<? extends Tool> shapeType) {
        this.shapeType = shapeType;
    }

    /**
     * @return
     * @since 3.4.3
     */
    public Class<? extends Tool> getShapeType() {
        return shapeType;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        mCanvas.drawBackground();
        mCanvas.drawShapes(tools);

        canvas.drawBitmap(mCanvas.getBitmap(), 0, 0, null);
        canvas.restore();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (shapeType == null || !isEnabled()) return false;
        PointF touchAt = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(touchAt);
                performClick();
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                touchMove(touchAt);
                performClick();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                touchUp(touchAt);
                performClick();
                invalidate();
                return true;
        }

        return false;
    }

    private void touchStart(PointF touchAt) {
        if (this.shapeType == ColorPicker.class) {
            int color = mCanvas.getColor(touchAt);
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener.onColorPicked(color);
            }
            return;
        }

        iTouch.set(touchAt);
        fTouch = null;
    }

    private void touchMove(PointF touchAt) {
        if (this.shapeType == ColorPicker.class) {
            int color = mCanvas.getColor(touchAt);
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener.onColorPicked(color);
            }
            return;
        }


        if (fTouch == null) {
            fTouch = new PointF();

            Tool tool = ToolFactory.get(shapeType, mBrush);
            tool.moveTo(iTouch.x, iTouch.y);
            if (tool.getClass().equals(Eraser.class)) {
                ((Eraser) tool).initEraser(mCanvas);
            }
            tools.add(tool);
        }

        float dx = Math.abs(touchAt.x - fTouch.x);
        float dy = Math.abs(touchAt.y - fTouch.y);
        if (dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE) {
            return;
        }

        fTouch.set(touchAt);
        Tool currentTool = tools.getCurrent();
        if (currentTool != null) {
            currentTool.draw(iTouch, fTouch);
        }
    }

    private void touchUp(PointF touchAt) {
        if (this.shapeType == ColorPicker.class) {
            int color = mCanvas.getColor(touchAt);
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener.onColorPicked(color);
            }
            return;
        }

        if (iTouch == null) return;
        if (fTouch == null) {
            fTouch = touchAt;
            Tool tool = ToolFactory.get(shapeType, mBrush);
            tool.moveTo(touchAt.x, touchAt.y);
            if (tool.getClass().equals(Eraser.class)) {
                ((Eraser) tool).initEraser(mCanvas);
            }
            tools.add(tool);
        }

        Tool currentTool = tools.getCurrent();
        if (currentTool != null) {
            currentTool.draw(iTouch, fTouch);
            if (canvasActionListener != null) {
                canvasActionListener.onDrawPath();
            }
        }
    }

    public void clear() {
        tools.clear();
        invalidate();

        if (canvasActionListener != null) {
            canvasActionListener.onRevert();
        }
    }

    public void redo() {
        boolean canRedo = tools.redo();
        invalidate();

        if (canvasActionListener != null) {
            canvasActionListener.onRedo(canRedo);
        }
    }

    public void undo() {
        boolean canUndo = tools.undo();
        invalidate();

        if (canvasActionListener != null) {
            canvasActionListener.onUndo(canUndo);
        }
    }

    public void save() {
        mCanvas.saveProject();
    }

    public void saveAs() {
        mCanvas.clearProjectName();
        mCanvas.saveProject();
    }

    public void setCanvasActionListener(CanvasActionListener canvasActionListener) {
        this.canvasActionListener = canvasActionListener;
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener;
    }

    public boolean isModified() {
        return tools.getPointer() > 0;
    }

    public boolean canRedo() {
        return tools.getPointer() < tools.size();
    }

}