package sfllhkhan95.doodle.utils

import android.content.Context
import android.os.Environment
import sfllhkhan95.doodle.DoodleApplication.Companion.EXT_IMAGE
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-06-07 12:46
 */
object FileUtils {

    /**
     * Creates an empty JPG file in the default Pictures directory, using the given name.
     *
     * @param [context] this is used to get the default Pictures directory.
     * @param [name] name of the empty file to create.
     * @return a file object corresponding to a newly-created file.
     *
     * @throws IOException in case of input/output error.
     */
    @Throws(IOException::class)
    fun createImageFile(context: Context, name: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filename = dir?.toString() + File.separator + name + EXT_IMAGE

        val tempFile = File(filename)
        tempFile.createNewFile()

        return tempFile
    }

    @Throws(IOException::class)
    fun writeToFile(bytes: ByteArray, file: File) {
        val out = FileOutputStream(file)
        out.write(bytes)
        out.flush()
        out.close()
    }

}