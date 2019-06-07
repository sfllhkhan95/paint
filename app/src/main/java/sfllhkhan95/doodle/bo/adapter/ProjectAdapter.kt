package sfllhkhan95.doodle.bo.adapter

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import sfllhkhan95.doodle.models.Project
import sfllhkhan95.doodle.views.ProjectView

class ProjectAdapter internal constructor(private val context: AppCompatActivity, private val gridLayoutId: Int, private val projects: List<Project>) : ArrayAdapter<Project>(context, gridLayoutId, projects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var gridItem = convertView
        val projectView: ProjectView
        if (gridItem == null) {
            val inflater = context.layoutInflater
            gridItem = inflater.inflate(gridLayoutId, parent, false)

            projectView = ProjectView(gridItem)
            gridItem.tag = projectView
        } else {
            projectView = gridItem.tag as ProjectView
        }

        projectView.show(projects[position])
        return gridItem!!
    }

}