package sfllhkhan95.doodle.projects.utils

import android.app.Activity
import android.content.Intent
import android.support.annotation.UiThread
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import sfllhkhan95.doodle.R
import sfllhkhan95.doodle.core.MainActivity
import sfllhkhan95.doodle.projects.models.Thumbnail
import java.util.*

@UiThread
class ThumbnailInflater(private val activity: Activity) : Runnable, AdapterView.OnItemClickListener {
    private var savedProjects: Array<String>? = null

    private val thumbnails: List<Thumbnail>
        get() {
            val thumbnails = ArrayList<Thumbnail>()
            if (savedProjects != null) {
                for (projectName in savedProjects!!) {
                    val thumbnailBitmap = DoodleDatabase.loadDoodle(projectName, 200, 200)
                    if (thumbnailBitmap != null) {
                        val thumbnail = Thumbnail(thumbnailBitmap, projectName)
                        thumbnails.add(thumbnail)
                    }
                }
            }
            return thumbnails
        }

    private// Obtain device display metrics (used to setup project resolution)
    val thumbnailsFull: List<Thumbnail>
        get() {
            val thumbnails = ArrayList<Thumbnail>()
            if (savedProjects != null) {
                for (projectName in savedProjects!!) {
                    val metrics = DisplayMetrics()
                    activity.windowManager.defaultDisplay.getMetrics(metrics)

                    val thumbnailBitmap = DoodleDatabase.loadDoodle(projectName, metrics.widthPixels / 2, metrics.heightPixels / 2)
                    if (thumbnailBitmap != null) {
                        val thumbnail = Thumbnail(thumbnailBitmap, projectName)
                        thumbnails.add(thumbnail)
                    }
                }
            }
            return thumbnails
        }

    fun setSavedProjects(savedProjects: Array<String>?) {
        this.savedProjects = savedProjects
    }

    private fun inflateGrid() {
        val thumbnails = thumbnails
        val adapter = ThumbnailAdapter(
                activity,
                R.layout.template_thumbnail,
                thumbnails)

        val projectGrid = activity.findViewById<GridView>(R.id.savedProjectsGrid)
        projectGrid.adapter = adapter

        projectGrid.onItemClickListener = this
    }

    override fun run() {
        inflateGrid()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val intent = Intent(activity, MainActivity::class.java)
        val item = parent.getItemAtPosition(position) as Thumbnail
        intent.putExtra("DOODLE", item.name)
        intent.putExtra("READ_ONLY", true)

        if (!DoodleDatabase.contains(item.name!!)) return

        activity.startActivity(intent)
    }

    fun share(name: String) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra("DOODLE", name)
        intent.putExtra("SHARE", true)
        intent.putExtra("READ_ONLY", true)

        if (!DoodleDatabase.contains(name)) return

        activity.startActivity(intent)
    }
}