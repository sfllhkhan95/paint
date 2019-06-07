package sfllhkhan95.doodle.projects.utils

import android.content.Intent
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.core.MainActivity
import sfllhkhan95.doodle.projects.models.Project
import java.util.*

@UiThread
class ProjectInflater(private val activity: AppCompatActivity) : Runnable, AdapterView.OnItemClickListener {

    private val projects: ArrayList<Project>
        get() {
            val projects = ArrayList<Project>()
            DoodleDatabase.listDoodles()?.forEach { id ->
                var project = DoodleDatabase.loadMetadata(id)
                if (project == null) {
                    project = Project()
                    project.name = "Untitled"
                    project.timestamp = DoodleDatabase.name2Timestamp(id)

                    DoodleDatabase.saveMetadata(project, DoodleDatabase.name2Timestamp(id))
                }

                projects.add(project)
            }
            return projects
        }

    private fun inflate() {
        val projects = projects
        val adapter = ProjectAdapter(
                activity,
                R.layout.template_thumbnail,
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
        intent.putExtra("DOODLE", item.timestamp)
        intent.putExtra("READ_ONLY", true)

        if (!DoodleDatabase.contains(item.timestamp!!)) return

        activity.startActivity(intent)
    }

}