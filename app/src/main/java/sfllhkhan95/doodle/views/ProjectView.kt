package sfllhkhan95.doodle.views

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.models.Project
import sfllhkhan95.doodle.utils.ProjectUtils
import java.io.ByteArrayOutputStream


/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 23/10/2017 2:27 AM
 */
class ProjectView(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val iconView: ImageView = itemView.findViewById(R.id.projectIcon)
    private val nameView: TextView = itemView.findViewById(R.id.projectName)

    fun show(project: Project) {
        nameView.text = project.name
        nameView.visibility = View.GONE

        project.timestamp?.let {
            val stream = ByteArrayOutputStream()
            ProjectUtils.open(it, 128, 128)?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            Glide.with(iconView.context)
                    .load(stream.toByteArray())
                    .into(iconView)
        }
    }

}