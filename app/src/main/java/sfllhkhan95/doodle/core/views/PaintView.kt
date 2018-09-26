package sfllhkhan95.doodle.core.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import sfllhkhan95.doodle.core.models.PaintBrush
import sfllhkhan95.doodle.core.models.PaintCanvas
import sfllhkhan95.doodle.core.models.Tools
import sfllhkhan95.doodle.core.models.tools.ColorPicker
import sfllhkhan95.doodle.core.models.tools.Eraser
import sfllhkhan95.doodle.core.models.tools.Tool
import sfllhkhan95.doodle.core.models.tools.ToolFactory
import sfllhkhan95.doodle.core.utils.CanvasActionListener
import sfllhkhan95.doodle.core.utils.OnColorPickedListener


/**
 * @author alichishti
 */
class PaintView : View {
    private val tools = Tools()
    val brush: PaintBrush

    var canvas: PaintCanvas? = null
    private val iTouch = PointF()
    private var fTouch: PointF? = PointF()
    /**
     * @return
     * @since 3.4.3
     */
    var shapeType: Class<out Tool>? = null

    private var canvasActionListener: CanvasActionListener? = null
    private var onColorPickedListener: OnColorPickedListener? = null

    val isModified: Boolean
        get() = tools.pointer > 0

    constructor(context: Context) : super(context) {
        brush = PaintBrush(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        brush = PaintBrush(context)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        this.canvas!!.drawBackground()
        this.canvas!!.drawShapes(tools)

        canvas.drawBitmap(this.canvas!!.bitmap, 0f, 0f, null)
        canvas.restore()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (shapeType == null || !isEnabled) return false
        val touchAt = PointF(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(touchAt)
                performClick()
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(touchAt)
                performClick()
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchUp(touchAt)
                performClick()
                invalidate()
                return true
            }
        }

        return false
    }

    private fun touchStart(touchAt: PointF) {
        if (this.shapeType == ColorPicker::class.java) {
            val color = canvas!!.getColor(touchAt)
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener!!.onColorPicked(color)
            }
            return
        }

        iTouch.set(touchAt)
        fTouch = null
    }

    private fun touchMove(touchAt: PointF) {
        if (this.shapeType == ColorPicker::class.java) {
            val color = canvas!!.getColor(touchAt)
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener!!.onColorPicked(color)
            }
            return
        }


        if (fTouch == null) {
            fTouch = PointF()

            val tool = ToolFactory[shapeType!!, brush]
            tool!!.moveTo(iTouch.x, iTouch.y)
            if (tool.javaClass == Eraser::class.java) {
                (tool as Eraser).initEraser(canvas!!)
            }
            tools.add(tool)
        }

        val dx = Math.abs(touchAt.x - fTouch!!.x)
        val dy = Math.abs(touchAt.y - fTouch!!.y)
        if (dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE) {
            return
        }

        fTouch!!.set(touchAt)
        val currentTool = tools.current
        currentTool?.draw(iTouch, fTouch!!)
    }

    private fun touchUp(touchAt: PointF) {
        if (this.shapeType == ColorPicker::class.java) {
            val color = canvas!!.getColor(touchAt)
            if (this.onColorPickedListener != null) {
                this.onColorPickedListener!!.onColorPicked(color)
            }
            return
        }

        if (fTouch == null) {
            fTouch = touchAt
            val tool = ToolFactory[shapeType!!, brush]
            tool!!.moveTo(touchAt.x, touchAt.y)
            if (tool.javaClass == Eraser::class.java) {
                (tool as Eraser).initEraser(canvas!!)
            }
            tools.add(tool)
        }

        val currentTool = tools.current
        if (currentTool != null) {
            currentTool.draw(iTouch, fTouch!!)
            if (canvasActionListener != null) {
                canvasActionListener!!.onDrawPath()
            }
        }
    }

    fun clear() {
        tools.clear()
        invalidate()

        if (canvasActionListener != null) {
            canvasActionListener!!.onRevert()
        }
    }

    fun redo() {
        val canRedo = tools.redo()
        invalidate()

        if (canvasActionListener != null) {
            canvasActionListener!!.onRedo(canRedo)
        }
    }

    fun undo() {
        val canUndo = tools.undo()
        invalidate()

        if (canvasActionListener != null) {
            canvasActionListener!!.onUndo(canUndo)
        }
    }

    fun save() {
        canvas!!.saveProject()
    }

    fun saveAs() {
        canvas!!.clearProjectName()
        canvas!!.saveProject()
    }

    fun setCanvasActionListener(canvasActionListener: CanvasActionListener) {
        this.canvasActionListener = canvasActionListener
    }

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener
    }

    fun canRedo(): Boolean {
        return tools.pointer < tools.size
    }

    companion object {
        private const val TOUCH_TOLERANCE = 5f
    }

}