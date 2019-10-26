package sfllhkhan95.doodle.bo

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import sfllhkhan95.doodle.DoodleApplication.Companion.DEFAULT_PROJECT_NAME
import sfllhkhan95.doodle.DoodleApplication.Companion.FLAG_READ_ONLY
import sfllhkhan95.doodle.DoodleApplication.Companion.PROJECT_FROM_SAVED
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.bo.adapter.ProjectAdapter
import sfllhkhan95.doodle.models.Project
import sfllhkhan95.doodle.utils.ProjectUtils
import sfllhkhan95.doodle.views.activity.MainActivity
import java.util.*

@UiThread
class ProjectInflater(private val activity: AppCompatActivity) : Runnable, AdapterView.OnItemClickListener {

    private val projects: ArrayList<Project>
        get() {
            val projects = ArrayList<Project>()
            ProjectUtils.listAll()?.forEach { projectName ->
                var project = ProjectUtils[projectName]
                if (project == null) {
                    project = Project()
                    project.name = DEFAULT_PROJECT_NAME
                    project.timestamp = ProjectUtils.name2Timestamp(projectName)

                    ProjectUtils[ProjectUtils.name2Timestamp(projectName)] = project
                }

                projects.add(project)
            }
            return projects
        }

    private fun inflate() {
        val projects = projects
        val adapter = ProjectAdapter(
                activity,
                R.layout.view_project,
                projects)

        val projectGrid = activity.findViewById<GridView>(R.id.savedProjectsGrid)
        projectGrid.adapter = adapter
        projectGrid.onItemClickListener = this
    }

    override fun run() {
        inflate()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val intent = Intent(activity, MainActivity::class.java)
        val item = parent.getItemAtPosition(position) as Project
        intent.putExtra(PROJECT_FROM_SAVED, item.timestamp)
        intent.putExtra(FLAG_READ_ONLY, true)

        if (item.timestamp!! !in ProjectUtils) return

        activity.startActivity(intent)
    }

}