package sfllhkhan95.doodle.core.models

import android.content.Context
import android.graphics.Paint

import sfllhkhan95.doodle.R

/**
 * @author saifkhichi96
 * @since 23/10/2017
 */
class PaintBrush(val context: Context) : Paint(), Cloneable {

    var strokeColor: Int = 0
    var fillColor: Int = 0
    var size = DEFAULT_SIZE
        set(size) {
            field = DEFAULT_SIZE + size
        }

    init {
        strokeColor = context.resources.getColor(DEFAULT_STROKE_COLOR)
        fillColor = context.resources.getColor(DEFAULT_FILL_COLOR)

        this.color = strokeColor
        this.style = Paint.Style.STROKE
        this.strokeJoin = Paint.Join.ROUND
        this.strokeCap = Paint.Cap.ROUND
        this.xfermode = null
        this.alpha = 0xff
    }

    public override fun clone(): PaintBrush {
        return try {
            super.clone() as PaintBrush
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
            this
        }
    }

    companion object {

        private const val DEFAULT_STROKE_COLOR = R.color.red_900
        private const val DEFAULT_FILL_COLOR = R.color.red_900a
        private const val DEFAULT_SIZE = 5
    }

}
