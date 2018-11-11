package sfllhkhan95.doodle.projects.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import sfllhkhan95.doodle.DoodleApplication
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.auth.utils.LoginController
import sfllhkhan95.doodle.auth.views.UserView
import sfllhkhan95.doodle.projects.models.Thumbnail
import sfllhkhan95.doodle.projects.views.ThumbnailView

class ThumbnailAdapter internal constructor(private val context: Activity, private val gridLayoutId: Int, private val thumbnails: List<Thumbnail>) : ArrayAdapter<Thumbnail>(context, gridLayoutId, thumbnails) {

    private val mLoginController: LoginController = LoginController(context, (context.application as DoodleApplication).getDialogTheme());

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
        try {
            gridItem.findViewById<View>(R.id.deleteButton).setOnClickListener(thumbnails[position])
            gridItem.findViewById<View>(R.id.shareButton).setOnClickListener(thumbnails[position])
            (gridItem.findViewById<View>(R.id.email) as TextView).text = mLoginController.currentUser!!.email
            UserView(context)
                    .setEmailView(gridItem.findViewById<View>(R.id.email) as TextView)
                    .setAvatarView(gridItem.findViewById<View>(R.id.userAvatar) as ImageView)
                    .showUser(mLoginController.currentUser!!)
        } catch (ignored: Exception) {

        }

        thumbnailView.setThumbnail(thumbnails[position])
        return gridItem
    }

}