package sfllhkhan95.doodle.projects.models

import android.graphics.Bitmap
import android.view.View
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater

class Thumbnail(
        private val inflater: ThumbnailInflater,
        var icon: Bitmap?,
        var name: String?) :
        View.OnClickListener {

    override fun onClick(view: View) {
        when (view.id) {
            R.id.shareButton -> name?.let { inflater.share(name!!) }
        }
    }

}