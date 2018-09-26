package sfllhkhan95.doodle.core.views

import android.content.Context

import sfllhkhan95.doodle.core.utils.OnColorPickedListener
import yuku.ambilwarna.AmbilWarnaDialog

class ColorPicker(context: Context, color: Int) : AmbilWarnaDialog.OnAmbilWarnaListener {

    private val dialog: AmbilWarnaDialog = AmbilWarnaDialog(context, color, true, this)

    private var onColorPickedListener: OnColorPickedListener? = null

    override fun onCancel(dialog: AmbilWarnaDialog) {

    }

    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
        if (onColorPickedListener != null) {
            onColorPickedListener!!.onColorPicked(color)
        }
    }

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener
    }

    fun show() {
        this.dialog.show()
    }
}