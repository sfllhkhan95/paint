package sfllhkhan95.doodle.core.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import sfllhkhan95.doodle.core.models.tools.Tool;

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:24 AM
 */
public class Tools extends ArrayList<Tool> {

    private int pointer = 0;

    @Override
    public boolean add(Tool tool) {
        // Delete any undo-ed shapes
        while (this.size() > pointer) {
            this.remove(this.size() - 1);
        }

        // Add the new tool
        if (super.add(tool)) {
            pointer++;
            return true;
        }

        return false;
    }

    public boolean undo() {
        if (pointer > 0) {
            pointer--;
        }

        return pointer > 0;
    }

    public boolean redo() {
        if (pointer < this.size()) {
            pointer++;
        }

        return pointer < this.size();
    }

    @Override
    public void clear() {
        pointer = 0;
        super.clear();
    }

    void paint(PaintCanvas canvas) {
        int i = 0;
        for (Tool tool : this) {
            if (++i > pointer) break;
            tool.paint(canvas);
        }
    }

    @Nullable
    public Tool getCurrent() {
        return this.size() > 0 ? this.get(this.size() - 1) : null;
    }

    public int getPointer() {
        return pointer;
    }

}
