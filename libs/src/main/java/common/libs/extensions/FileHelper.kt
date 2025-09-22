package common.libs.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

fun File.isCacheScan(context: Context): Boolean {
    val path = this.absolutePath
    return path.contains("${context.packageName}/cache/")
}

fun File.copyToCache(context: Context): File? {
    val fileName = this.name
    val cacheDir: File = context.cacheDir
    val newFile = File(cacheDir, fileName)
    return try {
        this.inputStream().use { input ->
            newFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        newFile
    } catch (e: Exception) {
        null
    }
}

fun File.clearFileInFolder() {
    this.listFiles()?.forEach { fi ->
        if (fi.isFile) {
            if (fi.delete()) {
                Log.d("Namzzz", "delete: clearFileInFolder")
            }
        }
    }
}

fun File.toBitmap(): Bitmap {
    return BitmapFactory.decodeFile(this.absolutePath)
}

fun File.hasCacheVersion(context: Context): Boolean {
    val fileCacheVersion = logCacheFile(context)
    return fileCacheVersion.exists()
}

fun File.logCacheFile(context: Context): File {
    val folderCache = context.cacheDir
    return File(folderCache, this.name)
}

fun File.isPdf(): Boolean {
    val name = this.name
    return name.endsWith(".pdf")
}

fun File.isWord(): Boolean {
    val name = this.name
    return name.endsWith(".doc") || name.endsWith(".docx")
            || name.endsWith(".dotx") || name.endsWith(".docb")
}

fun File.isExcel(): Boolean {
    val name = this.name
    return name.endsWith(".xls") || name.endsWith(".xlsx")
            || name.endsWith(".csv")
}

fun File.isPowerPoint(): Boolean {
    val name = this.name
    return name.endsWith(".ppt") || name.endsWith(".pptx")
}

fun File.isTxt(): Boolean {
    val name = this.name
    return name.endsWith(".txt")
}