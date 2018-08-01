package sfllhkhan95.doodle.core.utils;

import sfllhkhan95.doodle.core.views.PaintView;

/**
 * CanvasActionListener provides a callback interface for certain actions performed on the {@link PaintView}.
 * Pass an instance of this class to {@link PaintView#setCanvasActionListener(CanvasActionListener)} to use
 * these callback methods.
 *
 * @author saifkhichi96
 * @version 1.0
 * @see PaintView
 */
public interface CanvasActionListener {

    /**
     * This method is called after a new object is drawn on the canvas.
     */
    void onDrawPath();

    /**
     * This method is called after an action performed on canvas is undone by calling the
     * {@link PaintView#undo()} method.
     *
     * @param allowed flag indicating whether more undo actions are allowed or not
     */
    void onUndo(boolean allowed);

    /**
     * This method is called after a previously undone action on canvas is restored by calling the
     * {@link PaintView#redo()} method.
     *
     * @param allowed flag indicating whether more redo actions are allowed or not
     */
    void onRedo(boolean allowed);

    /**
     * This method is called after reverting the canvas to its initial state using the
     * {@link PaintView#clear()} method.
     */
    void onRevert();

}
