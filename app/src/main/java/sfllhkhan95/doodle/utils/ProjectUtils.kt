package sfllhkhan95.doodle.utils

import android.graphics.Bitmap
import android.os.Environment
import com.google.gson.Gson
import sfllhkhan95.doodle.DoodleApplication.Companion.EXT_IMAGE
import sfllhkhan95.doodle.DoodleApplication.Companion.EXT_METADATA
import sfllhkhan95.doodle.models.Project
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * This is the project database. ProjectUtils is responsible for managing all the project
 * files, including saving, opening and deleting them.
 *
 * @author saifkhichi96
 * @version 1.3.0
 * @since 1.0.0
 */
object ProjectUtils {

    private const val EXT = EXT_IMAGE

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
     * Checks if a project file exists in the database.
     *
     * @param name name of the project to check
     * @return true if the project is found, false otherwise
     */
    operator fun contains(name: String): Boolean {
        return File(rootDir, timestamp2Name(name)).exists()
    }

    /**
     * Reads metadata of a project.
     *
     * @param name name of the project to open
     * @return Project contents, or null
     */
    operator fun get(name: String): Project? {
        val file = File(rootDir, timestamp2Name(name).split(EXT)[0] + EXT_METADATA)
        if (!file.exists()) return null

        val stream = FileInputStream(file)
        val reader = BufferedReader(InputStreamReader(stream))
        val sb = StringBuilder()
        var line: String? = readLine()
        while (line != null) {
            sb.append(line).append("\n")
            line = reader.readLine()
        }
        reader.close()
        val json = sb.toString()
        stream.close()
        return Gson().fromJson(json, Project::class.java)
    }

    /**
     * Writes the metadata of a project to the database.
     *
     * @param project content of the project
     * @param name name of the project
     */
    operator fun set(name: String, project: Project) {
        val file = File(rootDir, timestamp2Name(name).split(EXT)[0] + EXT_METADATA)
        if (file.exists()) file.delete()
        try {
            FileUtils.writeToFile(Gson().toJson(project).toByteArray(), file)
        } catch (ignored: Exception) {
            // TODO:
        }
    }

    /**
     * Saves a new project to the database.
     *
     * @param bitmap content of the new project
     * @param name name of the new project
     */
    operator fun set(name: String, bitmap: Bitmap) {
        val file = File(rootDir, name)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Saves a new project to the database.
     *
     * @param bitmap content of the new project
     */
    fun create(bitmap: Bitmap) {
        // Create file for storage with CURRENT_TIMESTAMP as name
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
        val timestamp = simpleDateFormat.format(Date())
        val filename = timestamp2Name(timestamp)

        this[filename] = bitmap
    }

    /**
     * Deletes a project.
     *
     * @param name name of the project to be deleted
     * @return true if the project is successfully deleted, false otherwise
     */
    fun delete(name: String): Boolean {
        return delete(rootDir, timestamp2Name(name))
    }

    /**
     * Lists all the projects in the database.
     *
     * @return a list containing names of all the projects, or null
     */
    fun listAll(): Array<String>? {
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
     * Opens a project as a Bitmap image of specified size.
     *
     * @param name name of the project to open
     * @param width          width of the opened image
     * @param height          height of the opened image
     * @return the Bitmap image of the project, or null
     */
    fun open(name: String, width: Int, height: Int): Bitmap? {
        val file = File(rootDir, timestamp2Name(name))
        return if (file.exists()) {
            try {
                BitmapUtils.openFromPath(rootDirPath + timestamp2Name(name), width, height)
            } catch (ex: OutOfMemoryError) {
                null
            }

        } else {
            null
        }
    }

    fun name2Timestamp(name: String): String {
        return name.split(PREFIX)[1].split(EXT)[0]
    }

    fun timestamp2Name(timestamp: String): String {
        return PREFIX + timestamp + EXT
    }

    /**
     * Relocates a project to new root directory.
     *
     * @param name name of the project to be relocated
     * @since 3.4.3
     */
    private fun migrateDoodle(name: String) {
        BitmapUtils.openFromPath(legacyRootDirPath + name)?.let { doodle ->
            this[name] = doodle
            delete(legacyRootDir, name)
        }
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

        BitmapUtils.openFromPath(rootDirPath + oldName)?.let { doodle ->
            this[newName] = doodle
            delete(oldName)
        }

        return newName
    }

    /**
     * Deletes a project.
     *
     * @param rootDir     root directory where this project is located
     * @param name name of the project to be deleted
     * @return true if the project is successfully deleted, false otherwise
     */
    private fun delete(rootDir: File, name: String): Boolean {
        val file = File(rootDir, name)
        File(rootDir, name.split(EXT)[0] + EXT_METADATA).delete()
        return file.exists() && file.delete()
    }

}