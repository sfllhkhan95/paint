package sfllhkhan95.doodle.models

import android.content.Context
import android.graphics.Paint
import sfllhkhan95.doodle.utils.ThemeUtils

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
        strokeColor = ThemeUtils.colorAccent(context)
        fillColor = ThemeUtils.colorBackground(context)

        this.color = strokeColor
        this.style = Style.STROKE
        this.strokeJoin = Join.ROUND
        this.strokeCap = Cap.ROUND
        this.xfermode = null
        this.alpha = 0xff
    }

    public override fun clone(): PaintBrush {
        return try {
            super.clone() as PaintBrush
        } catch (e: CloneNotSupportedException) {
            this
        }
    }

    companion object {
        private const val DEFAULT_SIZE = 5
    }

}