package sfllhkhan95.doodle.core.views

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AlertDialog
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import sfllhkhan95.doodle.core.utils.OnColorPickedListener
import java.util.*

class CanvasColorPicker(context: Context, color: Int, dialogTheme: Int) : ColorPickerClickListener {

    private val pickerModern: AlertDialog

    private var onColorPickedListener: OnColorPickedListener? = null

    init {
        this.pickerModern = ColorPickerDialogBuilder
                .with(context, dialogTheme)
                .setTitle("Canvas Color")
                .initialColor(color)
                .showAlphaSlider(false)
                .showColorPreview(false)
                .showColorEdit(false)
                .showLightnessSlider(true)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(15)
                .setPositiveButton("SET", this)
                .setNegativeButton("RANDOM") { dialogInterface, _ ->
                    val rand = Random()
                    val r = rand.nextInt(255)
                    val g = rand.nextInt(255)
                    val b = rand.nextInt(255)
                    val selectedColor = Color.rgb(r, g, b)
                    this@CanvasColorPicker.onClick(dialogInterface, selectedColor, null)
                }
                .build()
    }

    override fun onClick(dialogInterface: DialogInterface, selectedColor: Int, integers: Array<Int>?) {
        if (onColorPickedListener != null) {
            onColorPickedListener!!.onColorPicked(selectedColor)
        }
    }

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener
    }

    fun show() {
        this.pickerModern.show()
    }

}