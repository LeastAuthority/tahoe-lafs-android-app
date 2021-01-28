package org.tahoe.lafs.utils

import android.content.Context
import android.os.Environment
import okhttp3.ResponseBody
import org.tahoe.lafs.utils.Constants.EMPTY
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


object FileUtils {

    fun checkSDCardStatus(): Boolean {
        return when (Environment.getExternalStorageState()) {
            Environment.MEDIA_MOUNTED -> true
            Environment.MEDIA_MOUNTED_READ_ONLY -> {
                Timber.e("SD card is ready only.")
                false
            }
            else -> {
                Timber.e("SD card is not available.")
                false
            }
        }
    }

    fun createOrGetFile(fileName: String, folderName: String): File {
        val file = File(folderName, fileName)

        if (!file.parentFile.exists())
            file.parentFile.mkdirs()

        if (!file.exists())
            file.createNewFile()

        return file
    }

    fun saveFile(body: ResponseBody?, filePath: String): String {
        if (body == null)
            return EMPTY
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(filePath)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return filePath
        } catch (e: Exception) {
            Timber.e("Error while saving file ${e.message}")
        } finally {
            input?.close()
        }
        return EMPTY
    }

    fun getFolderName(context: Context): String {
        return Environment.getExternalStorageDirectory().absolutePath + Constants.TAHOE_LAFS_FOLDER
    }
}