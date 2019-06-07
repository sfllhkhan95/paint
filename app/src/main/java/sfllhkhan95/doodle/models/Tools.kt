package sfllhkhan95.doodle.models

import java.util.*

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 23/10/2017 2:24 AM
 */
class Tools : ArrayList<Tool>() {

    var pointer = 0
        private set

    val current: Tool?
        get() = if (this.size > 0) this[this.size - 1] else null

    override fun add(element: Tool): Boolean {
        // Delete any undo-ed shapes
        while (this.size > pointer) {
            this.removeAt(this.size - 1)
        }

        // Add the new tool
        if (super.add(element)) {
            pointer++
            return true
        }

        return false
    }

    fun undo(): Boolean {
        if (pointer > 0) {
            pointer--
        }

        return pointer > 0
    }

    fun redo(): Boolean {
        if (pointer < this.size) {
            pointer++
        }

        return pointer < this.size
    }

    override fun clear() {
        pointer = 0
        super.clear()
    }

    internal fun paint(canvas: PaintCanvas) {
        for ((i, tool) in this.withIndex()) {
            if (i + 1 > pointer) break
            tool.paint(canvas)
        }
    }

}
