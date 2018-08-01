package sfllhkhan95.doodle.core.utils;

import android.view.Menu;
import android.view.MenuItem;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.views.PaintView;

/**
 * @author saifkhichi96
 * @see CanvasActionListener
 */
public class ActionBarManager implements CanvasActionListener {

    // Action bar buttons
    private final MenuItem undoAction;
    private final MenuItem redoAction;
    private final MenuItem revertAction;
    private final MenuItem saveAction;
    //private final MenuItem saveAsAction;

    public ActionBarManager(Menu menu) {
        undoAction = menu.findItem(R.id.undo);
        redoAction = menu.findItem(R.id.redo);
        revertAction = menu.findItem(R.id.revert);
        saveAction = menu.findItem(R.id.save);
        //saveAsAction = menu.findItem(R.id.save_as);
    }

    private void enable(MenuItem item) {
        item.setEnabled(true);
    }

    private void disable(MenuItem item) {
        item.setEnabled(false);
    }

    @Override
    public void onDrawPath() {
        enable(revertAction);
        enable(undoAction);
        disable(redoAction);
        enable(saveAction);
        //enable(saveAsAction);
    }

    @Override
    public void onUndo(boolean canUndo) {
        if (!canUndo) {
            disable(revertAction);
            disable(undoAction);
            disable(saveAction);
            //disable(saveAsAction);
        }

        enable(redoAction);
    }

    @Override
    public void onRedo(boolean canRedo) {
        if (!canRedo) {
            disable(redoAction);
        }

        enable(revertAction);
        enable(undoAction);
        enable(saveAction);
        //enable(saveAsAction);
    }

    @Override
    public void onRevert() {
        disable(revertAction);
        disable(undoAction);
        disable(redoAction);
        disable(saveAction);
        //disable(saveAsAction);
    }

    public void sync(PaintView paintView) {
        if (paintView.isModified()) {
            enable(revertAction);
            enable(undoAction);
            enable(saveAction);
            //enable(saveAsAction);
        } else {
            disable(revertAction);
            disable(undoAction);
            disable(saveAction);
            //disable(saveAsAction);
        }

        if (paintView.canRedo()) {
            enable(redoAction);
        } else {
            disable(redoAction);
        }
    }
}
