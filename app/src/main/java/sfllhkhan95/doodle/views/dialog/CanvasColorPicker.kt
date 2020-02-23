package sfllhkhan95.doodle.views.dialog

class CanvasColorPicker : ColorPicker() {

    class Builder(color: Int) : ColorPicker.Builder(color) {
        init {
            picker.showPreview(true)
            picker.showAlpha(false)
        }
    }

}