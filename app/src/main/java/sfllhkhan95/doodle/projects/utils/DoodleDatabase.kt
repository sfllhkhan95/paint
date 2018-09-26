package sfllhkhan95.doodle.projects.utils

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * This is the project database. DoodleDatabase is responsible for managing all the project
 * files, including saving, opening and deleting them.
 *
 * @author saifkhichi96
 * @version 1.3.0
 * @since 1.0.0
 */
object DoodleDatabase {

    private const val EXT = ".jpg"

    private const val PREFIX = "DOODLEv2_"
    private const val DIR = "Android/data/sfllhkhan95.doodle/files/Projects/"
    private val rootDirPath: String
    private val rootDir: File

    private const val PREFIX_LEGACY = "DOODLE_"
    private const val DIR_LEGACY = "Pictures/Doodles/"
    private val legacyRootDirPath: String
    private val legacyRootDir: File

    init {
        legacyRootDirPath = Environment.getExternalStorageDirectory().toString() + "/" + DIR_LEGACY
        legacyRootDir = File(legacyRootDirPath)
        legacyRootDir.mkdirs()

        rootDirPath = Environment.getExternalStorageDirectory().toString() + "/" + DIR
        rootDir = File(rootDirPath)
        rootDir.mkdirs()
    }

    /**
     * Relocates a project to new root directory.
     *
     * @param projectName name of the project to be relocated
     * @since 3.4.3
     */
    private fun migrateDoodle(projectName: String) {
        val doodle = DoodleFactory.loadFromPath(legacyRootDirPath + projectName)
        saveDoodle(doodle, projectName)
        removeDoodle(legacyRootDir, projectName)
    }

    /**
     * Relocates all projects to the updated root directory.
     *
     * @since 3.4.3
     */
    private fun migrateLegacyDoodles() {
        val list = legacyRootDir.list { _, name ->
            (name.startsWith(PREFIX) || name.startsWith(PREFIX_LEGACY)) && name.endsWith(EXT)
        }
        if (list != null) {
            for (name in list) {
                migrateDoodle(name)
            }
        }
        legacyRootDir.delete()
    }

    /**
     * Renames projects to reflect updated naming syntax.
     *
     * @param oldName old name of the project
     * @return new name of the project
     * @since 2.1.0
     */
    private fun upgradeLegacyNames(oldName: String): String {
        var date = oldName.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val time = oldName.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2]

        val day = date.substring(0, 2)
        val month = date.substring(2, 4)
        val year = date.substring(4, 8)

        date = year + month + day
        val newName = PREFIX + date + "_" + time

        val doodle = DoodleFactory.loadFromPath(rootDirPath + oldName)
        saveDoodle(doodle, newName)
        removeDoodle(oldName)

        return newName
    }

    /**
     * Opens a project as a Bitmap image of specified size.
     *
     * @param projectName name of the project to open
     * @param wd          width of the opened image
     * @param ht          height of the opened image
     * @return the Bitmap image of the project, or null
     */
    fun loadDoodle(projectName: String, wd: Int, ht: Int): Bitmap? {
        val file = File(rootDir, projectName)
        return if (file.exists()) {
            try {
                DoodleFactory.loadFromPath(rootDirPath + projectName, wd, ht)
            } catch (ex: OutOfMemoryError) {
                null
            }

        } else {
            null
        }
    }

    /**
     * Lists all the projects in the database.
     *
     * @return a list containing names of all the projects, or null
     */
    fun listDoodles(): Array<String>? {
        migrateLegacyDoodles()
        var list: Array<String>? = rootDir.list { _, name ->
            (name.startsWith(PREFIX) || name.startsWith(PREFIX_LEGACY)) && name.endsWith(EXT)
        } ?: return null

        val newList = ArrayList<String>()

        for (name in list!!) {
            // If file is from an older version, upgrade filename
            var newName = name
            if (name.startsWith(PREFIX_LEGACY)) {
                newName = upgradeLegacyNames(name)
            }

            newList.add(newName)
        }
        list = newList.toTypedArray()

        Arrays.sort(list, Collections.reverseOrder())
        return list
    }

    /**
     * Saves a new project to the database.
     *
     * @param projectData content of the new project
     */
    fun saveDoodle(projectData: Bitmap) {
        // Create file for storage with CURRENT_TIMESTAMP as name
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val timestamp = simpleDateFormat.format(Date())
        val filename = PREFIX + timestamp + EXT

        saveDoodle(projectData, filename)
    }

    /**
     * Saves a new project to the database.
     *
     * @param projectData content of the new project
     * @param projectName name of the new project
     */
    fun saveDoodle(projectData: Bitmap, projectName: String) {
        val file = File(rootDir, projectName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            projectData.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Checks if a project file exists in the database.
     *
     * @param projectName name of the project to check
     * @return true if the project is found, false otherwise
     */
    operator fun contains(projectName: String): Boolean {
        val file = File(rootDir, projectName)
        return file.exists()
    }

    /**
     * Deletes a project.
     *
     * @param projectName name of the project to be deleted
     * @return true if the project is successfully deleted, false otherwise
     */
    fun removeDoodle(projectName: String): Boolean {
        return removeDoodle(rootDir, projectName)
    }

    /**
     * Deletes a project.
     *
     * @param rootDir     root directory where this project is located
     * @param projectName name of the project to be deleted
     * @return true if the project is successfully deleted, false otherwise
     */
    private fun removeDoodle(rootDir: File, projectName: String): Boolean {
        val file = File(rootDir, projectName)
        return file.exists() && file.delete()
    }

}