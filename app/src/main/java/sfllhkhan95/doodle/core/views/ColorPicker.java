package sfllhkhan95.doodle.core.views;

import android.content.Context;

import sfllhkhan95.doodle.core.utils.OnColorPickedListener;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ColorPicker implements AmbilWarnaDialog.OnAmbilWarnaListener {

    private final AmbilWarnaDialog dialog;

    private OnColorPickedListener onColorPickedListener;

    public ColorPicker(Context context, int color) {
        this.dialog = new AmbilWarnaDialog(context, color, true, this);
    }

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {

    }

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        if (onColorPickedListener != null) {
            onColorPickedListener.onColorPicked(color);
        }
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener;
    }

    public void show() {
        this.dialog.show();
    }
}