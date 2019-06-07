package sfllhkhan95.doodle.projects.views

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.projects.models.Project
import sfllhkhan95.doodle.projects.utils.DoodleDatabase
import java.io.ByteArrayOutputStream


/**
 * @author saifkhichi96
 * @version 1.0
 * created on 23/10/2017 2:27 AM
 */
class ProjectView(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val iconView: ImageView = itemView.findViewById(R.id.projectIcon)
    private val nameView: TextView = itemView.findViewById(R.id.projectName)

    fun show(project: Project) {
        nameView.text = project.name
        nameView.visibility = View.GONE

        project.timestamp?.let {
            val stream = ByteArrayOutputStream()
            DoodleDatabase.loadDoodle(it, 128, 128)?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            Glide.with(iconView.context)
                    .load(stream.toByteArray())
                    .into(iconView)
        }
    }

}