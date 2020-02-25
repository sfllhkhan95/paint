package sfllhkhan95.doodle.views.dialog

class CanvasColorPicker : ColorPicker() {

    class Builder(color: Int) : ColorPicker.Builder(color) {
        init {
            picker.showOptions(false)
            picker.showPreview(true)
            picker.showAlpha(false)
        }
    }

}