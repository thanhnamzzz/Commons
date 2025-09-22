package common.libs.functions

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    fun isFileInHiddenDirectory(file: File): Boolean {
        val pathParts = file.absolutePath.split(File.separator)
        return pathParts.any { it.startsWith(".") }
    }

    fun getMimeType(url: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase()).toString()
    }

    fun copyTo(path: File, toFolder: File, toNameFile: String) {
        if (!toFolder.exists()) toFolder.mkdir()
        val toFile = File(toFolder, toNameFile)

        try {
            FileInputStream(path).use { input ->
                FileOutputStream(toFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("Namzzz", "FileUtils: ---------- name $toNameFile | copyTo $toFolder")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Namzzz", "FileUtils: copyTo $toFolder fail ${e.message}")
        }
    }

    fun dateFile(file: File): String {
        val fileCreationTime: Long = file.lastModified()
        val creationDate = Date(fileCreationTime)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val creationDateString = dateFormat.format(creationDate)
        return creationDateString
    }

    fun sizeFile(file: File): String {
        val fileSize = file.length()
        val fileSizeInKB: Double = fileSize / 1024.0
        val fileSizeInMB = fileSizeInKB / 1024.0
        val decimalFormat = DecimalFormat("#.#")
        val size: String = if (fileSizeInMB <= 1) decimalFormat.format(fileSizeInKB) + " KB"
        else decimalFormat.format(fileSizeInMB) + " MB"
        return size
    }

    fun getFileInformation(file: File): String {
        return "${dateFile(file)} | ${sizeFile(file)}"
    }

    fun getVideoResolution(videoPath: String): Pair<Int, Int>? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoPath)
            val width =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()
            val height =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()
            Pair(width ?: 0, height ?: 0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    fun getImageResolution(imagePath: String): Pair<Int, Int>? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)
            val width = options.outWidth
            val height = options.outHeight
            Pair(width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(0, 0)
        }
    }
}