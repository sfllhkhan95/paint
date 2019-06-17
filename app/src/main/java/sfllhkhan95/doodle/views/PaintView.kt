package sfllhkhan95.doodle.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import sfllhkhan95.doodle.bo.factory.ToolFactory
import sfllhkhan95.doodle.models.*
import sfllhkhan95.doodle.models.shapes.Eraser
import sfllhkhan95.doodle.models.shapes.FloodFill
import sfllhkhan95.doodle.utils.listener.CanvasActionListener
import sfllhkhan95.doodle.utils.listener.OnColorPickedListener


/**
 * @author saifkhichi96
 */
class PaintView : View {

    val brush: PaintBrush
    var canvas: PaintCanvas? = null

    var selectedTool: Class<out Tool>? = null
    private val tools = Tools()

    private val iTouch = PointF()
    private var fTouch: PointF? = PointF()

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
        this.canvas?.let {
            it.drawBackground()
            it.drawPaths(tools)

            canvas.drawBitmap(it.bitmap, 0f, 0f, null)
        }
        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (selectedTool == null || !isEnabled || ev.pointerCount > 1) return false
        val touchAt = PointF(ev.x, ev.y)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(touchAt)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(touchAt)
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

    fun canRedo(): Boolean {
        return tools.pointer < tools.size
    }

    fun clear() {
        tools.clear()
        invalidate()

        canvasActionListener?.onRevert()
    }

    fun redo() {
        val canRedo = tools.redo()
        invalidate()

        canvasActionListener?.onRedo(canRedo)
    }

    fun setCanvasActionListener(canvasActionListener: CanvasActionListener) {
        this.canvasActionListener = canvasActionListener
    }

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener
    }

    fun save() {
        canvas?.saveProject()
    }

    fun saveAs() {
        canvas?.saveProjectCopy()
    }

    fun undo() {
        val canUndo = tools.undo()
        invalidate()

        canvasActionListener?.onUndo(canUndo)
    }

    private fun touchStart(touchAt: PointF) {
        if (this.selectedTool == EyedropTool::class.java) {
            canvas?.let {
                val color = it.getColor(touchAt)
                this.onColorPickedListener?.onColorPicked(color)
                return
            }
        }

        iTouch.set(touchAt)
        fTouch = null
    }

    private fun touchMove(touchAt: PointF) {
        if (this.selectedTool == EyedropTool::class.java) {
            canvas?.let {
                val color = it.getColor(touchAt)
                this.onColorPickedListener?.onColorPicked(color)
                return
            }
        }

        if (fTouch == null) {
            fTouch = PointF()

            val tool = ToolFactory[selectedTool!!, brush]
            tool!!.moveTo(iTouch.x, iTouch.y)
            canvas?.let {
                when (tool.javaClass) {
                    Eraser::class.java -> (tool as Eraser).initEraser(it)
                    FloodFill::class.java -> {
                        (tool as FloodFill).apply {
                            this.bitmap = it.bitmap
                            this.execute(iTouch)
                        }
                    }
                }
                tools.add(tool)
            }
        }

        fTouch?.let {
            val dx = Math.abs(touchAt.x - it.x)
            val dy = Math.abs(touchAt.y - it.y)
            if (dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE) {
                return
            }

            it.set(touchAt)
            tools.current?.draw(iTouch, it)
        }
    }

    private fun touchUp(touchAt: PointF) {
        canvas?.let { c ->
            if (this.selectedTool == EyedropTool::class.java) {
                val color = c.getColor(touchAt)
                this.onColorPickedListener?.onColorPicked(color)
                return
            }

            if (fTouch == null) {
                fTouch = touchAt
                val tool = ToolFactory[selectedTool!!, brush]
                tool!!.moveTo(touchAt.x, touchAt.y)
                if (tool.javaClass == Eraser::class.java) {
                    (tool as Eraser).initEraser(c)
                }
                tools.add(tool)
            }

            fTouch?.let {
                val currentTool = tools.current
                currentTool?.draw(iTouch, it)
                canvasActionListener?.onDrawPath()
            }
        }
    }

    companion object {
        private const val TOUCH_TOLERANCE = 5f
    }

}