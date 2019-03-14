package sfllhkhan95.doodle.core.views

import android.content.Context

class CanvasColorPicker(context: Context, color: Int, dialogTheme: Int) : ColorPicker(context, color, dialogTheme) {

    init {
        picker.showPreview(false)
        picker.showAlpha(false)
    }
}