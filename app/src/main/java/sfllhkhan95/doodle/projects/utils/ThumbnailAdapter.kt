package sfllhkhan95.doodle.projects.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.projects.models.Thumbnail
import sfllhkhan95.doodle.projects.views.ThumbnailView

class ThumbnailAdapter internal constructor(private val context: Activity, private val gridLayoutId: Int, private val thumbnails: List<Thumbnail>) : ArrayAdapter<Thumbnail>(context, gridLayoutId, thumbnails) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var gridItem = convertView
        val thumbnailView: ThumbnailView
        if (gridItem == null) {
            val inflater = context.layoutInflater
            gridItem = inflater.inflate(gridLayoutId, parent, false)

            thumbnailView = ThumbnailView(gridItem!!.findViewById<View>(R.id.projectIcon) as ImageView)

            gridItem.tag = thumbnailView
        } else {
            thumbnailView = gridItem.tag as ThumbnailView
        }

        thumbnailView.setThumbnail(thumbnails[position])
        return gridItem
    }

}