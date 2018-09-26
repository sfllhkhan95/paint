package sfllhkhan95.doodle.projects.models

import android.graphics.Bitmap
import android.view.View

import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.core.views.ConfirmationDialog
import sfllhkhan95.doodle.projects.utils.DoodleDatabase
import sfllhkhan95.doodle.projects.utils.ThumbnailInflater

class Thumbnail(
        private val inflater: ThumbnailInflater,
        var icon: Bitmap?,
        var name: String?) :
        View.OnClickListener {

    override fun onClick(view: View) {
        when (view.id) {
            R.id.shareButton -> inflater.share(name)

            R.id.deleteButton -> ConfirmationDialog.Builder(view.context)
                    .setHeadline(view.context.getString(R.string.label_delete))
                    .setIcon(R.drawable.ic_action_delete)
                    .setTitle(view.context.resources.getString(R.string.confirm_delete_title))
                    .setMessage(view.context.resources.getString(R.string.confirm_delete_body))
                    .setPositiveButton(view.context.getString(android.R.string.ok),
                            {
                                DoodleDatabase.removeDoodle(this@Thumbnail.name)
                                inflater.run()
                            }, true)
                    .setNegativeButton(view.context.getString(android.R.string.cancel),
                            null, true)
                    .create()
                    .show()
        }
    }

}