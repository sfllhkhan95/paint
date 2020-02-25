package sfllhkhan95.doodle.views.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rarepebble.colorpicker.ColorPickerView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.utils.ThemeUtils
import sfllhkhan95.doodle.utils.listener.OnColorPickedListener

open class ColorPicker : BottomSheetDialogFragment() {

    private var alpha = true
    private var hex = false
    private var preview = true
    private var options = true

    var color = 0xff
    var colorPickedListener: OnColorPickedListener? = null
    var fillColorPickedListener: OnColorPickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, ThemeUtils.getDialogTheme())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_color_picker, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        v.findViewById<ColorPickerView>(R.id.colorPicker)?.apply {
            this.addColorObserver { this@ColorPicker.color = it.color }
            this.color = this@ColorPicker.color
            this.showAlpha(this@ColorPicker.alpha)
            this.showHex(this@ColorPicker.hex)
            this.showPreview(this@ColorPicker.preview)
        }

        v.findViewById<View>(R.id.colorOptions).visibility = if (this@ColorPicker.options) View.VISIBLE else View.GONE
        v.findViewById<View>(R.id.confirm_button)?.setOnClickListener {
            if (v.findViewById<AppCompatCheckBox>(R.id.strokeColor).isChecked) {
                colorPickedListener?.onColorPicked(color)
            }

            if (v.findViewById<AppCompatCheckBox>(R.id.fillColor).isChecked) {
                fillColorPickedListener?.onColorPicked(color)
            }
            dismiss()
        }

        v.findViewById<View>(R.id.cancel_button)?.setOnClickListener {
            dismiss()
        }

        return v
    }

    fun showAlpha(alpha: Boolean) {
        this.alpha = alpha
    }

    fun showHex(hex: Boolean) {
        this.hex = hex
    }

    fun showPreview(preview: Boolean) {
        this.preview = preview
    }

    fun showOptions(options: Boolean) {
        this.options = options
    }

    fun show(manager: FragmentManager) {
        super.show(manager, "color_picker")
    }

    open class Builder(color: Int) {

        protected val picker: ColorPicker = ColorPicker()

        init {
            picker.color = color
            picker.showAlpha(true)
            picker.showHex(false)
            picker.showPreview(true)
        }

        fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener): Builder {
            picker.colorPickedListener = onColorPickedListener
            return this
        }

        fun setOnFillColorPickedListener(fillColorPickedListener: OnColorPickedListener): Builder {
            picker.fillColorPickedListener = fillColorPickedListener
            return this
        }

        fun create(): ColorPicker {
            return picker
        }

    }

}