package sfllhkhan95.doodle.core.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Random;

import sfllhkhan95.doodle.R;
import sfllhkhan95.doodle.core.utils.OnColorPickedListener;

public class CanvasColorPicker implements ColorPickerClickListener {

    private final AlertDialog pickerModern;

    private OnColorPickedListener onColorPickedListener;

    public CanvasColorPicker(Context context, int color) {
        this.pickerModern = ColorPickerDialogBuilder
                .with(context, R.style.DialogTheme)
                .setTitle("Canvas Color")
                .initialColor(color)
                .showAlphaSlider(false)
                .showColorPreview(false)
                .showColorEdit(true)
                .showLightnessSlider(true)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(15)
                .setPositiveButton("SET", this)
                .setNegativeButton("RANDOM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Random rand = new Random();
                        int r = rand.nextInt(255);
                        int g = rand.nextInt(255);
                        int b = rand.nextInt(255);
                        int selectedColor = Color.rgb(r, g, b);
                        CanvasColorPicker.this.onClick(dialogInterface, selectedColor, null);
                    }
                })
                .build();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
        if (onColorPickedListener != null) {
            onColorPickedListener.onColorPicked(selectedColor);
        }
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener;
    }

    public void show() {
        this.pickerModern.show();
    }

}