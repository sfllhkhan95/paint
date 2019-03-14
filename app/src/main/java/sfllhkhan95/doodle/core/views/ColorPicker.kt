package sfllhkhan95.doodle.core.views

import android.content.Context
import android.support.v7.app.AlertDialog
import com.rarepebble.colorpicker.ColorObserver
import com.rarepebble.colorpicker.ColorPickerView
import com.rarepebble.colorpicker.ObservableColor
import sfllhkhan95.doodle.core.utils.OnColorPickedListener

open class ColorPicker(val context: Context, color: Int, val dialogTheme: Int) : ColorObserver {

    protected val picker: ColorPickerView = ColorPickerView(context)

    init {
        picker.color = color
        picker.showAlpha(true)
        picker.showHex(false)
        picker.showPreview(true)
        picker.addColorObserver(this)
    }

    private var onColorPickedListener: OnColorPickedListener? = null

    override fun updateColor(observableColor: ObservableColor?) {
        observableColor?.let {
            if (onColorPickedListener != null) {
                onColorPickedListener!!.onColorPicked(it.color)
            }
        }
    }

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener
    }

    fun show() {
        AlertDialog.Builder(context, dialogTheme).setView(picker).show()
    }
}