package sfllhkhan95.doodle.core.utils

import android.view.Menu
import android.view.MenuItem

import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.core.views.PaintView

/**
 * @author saifkhichi96
 * @see CanvasActionListener
 */
class ActionBarManager
(menu: Menu) : CanvasActionListener {

    // Action bar buttons
    private val undoAction: MenuItem = menu.findItem(R.id.undo)
    private val redoAction: MenuItem = menu.findItem(R.id.redo)
    private val revertAction: MenuItem = menu.findItem(R.id.revert)
    private val saveAction: MenuItem = menu.findItem(R.id.save)
    // private val saveAsAction: MenuItem = menu.findItem(R.id.save_as)

    init {

    }

    private fun enable(item: MenuItem) {
        item.isEnabled = true
    }

    private fun disable(item: MenuItem) {
        item.isEnabled = false
    }

    override fun onDrawPath() {
        enable(revertAction)
        enable(undoAction)
        disable(redoAction)
        enable(saveAction)
        //enable(saveAsAction);
    }

    override fun onUndo(allowed: Boolean) {
        if (!allowed) {
            disable(revertAction)
            disable(undoAction)
            disable(saveAction)
            //disable(saveAsAction);
        }

        enable(redoAction)
    }

    override fun onRedo(allowed: Boolean) {
        if (!allowed) {
            disable(redoAction)
        }

        enable(revertAction)
        enable(undoAction)
        enable(saveAction)
        //enable(saveAsAction);
    }

    override fun onRevert() {
        disable(revertAction)
        disable(undoAction)
        disable(redoAction)
        disable(saveAction)
        //disable(saveAsAction);
    }

    fun sync(paintView: PaintView) {
        if (paintView.isModified) {
            enable(revertAction)
            enable(undoAction)
            enable(saveAction)
            //enable(saveAsAction);
        } else {
            disable(revertAction)
            disable(undoAction)
            disable(saveAction)
            //disable(saveAsAction);
        }

        if (paintView.canRedo()) {
            enable(redoAction)
        } else {
            disable(redoAction)
        }
    }
}
