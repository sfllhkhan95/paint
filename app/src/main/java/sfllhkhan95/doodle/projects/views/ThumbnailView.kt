package sfllhkhan95.doodle.projects.views

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import sfllhkhan95.doodle.projects.models.Thumbnail
import java.io.ByteArrayOutputStream

/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
class ThumbnailView(private val iconView: ImageView) {

    fun setThumbnail(thumbnail: Thumbnail) {
        val mBitmap = thumbnail.icon
        val stream = ByteArrayOutputStream()
        mBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
        Glide.with(iconView.context)
                .load(stream.toByteArray())
                .into(iconView)
    }

}