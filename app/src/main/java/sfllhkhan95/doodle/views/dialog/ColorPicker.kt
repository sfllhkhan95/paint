package sfllhkhan95.doodle.views.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.rarepebble.colorpicker.ColorObserver
import com.rarepebble.colorpicker.ColorPickerView
import com.rarepebble.colorpicker.ObservableColor
import sfllhkhan95.doodle.utils.listener.OnColorPickedListener

open class ColorPicker(private val context: Context, color: Int, private val dialogTheme: Int) : ColorObserver {

    protected val picker: ColorPickerView = ColorPickerView(context)

    init {
        picker.color = color
        picker.showAlpha(true)
        picker.showHex(false)
        picker.showPreview(true)
    }

    private var onColorPickedListener: OnColorPickedListener? = null

    override fun updateColor(observableColor: ObservableColor?) {
        observableColor?.let {
            onColorPickedListener?.onColorPicked(it.color)
        }
    }

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener): ColorPicker {
        picker.addColorObserver(this)
        this.onColorPickedListener = onColorPickedListener
        return this
    }

    fun show() {
        AlertDialog.Builder(context, dialogTheme).setView(picker).show()
    }

}