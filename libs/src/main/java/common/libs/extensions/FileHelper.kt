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
	} catch (_: Exception) {
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

fun File.toBitmap(): Bitmap? = BitmapFactory.decodeFile(absolutePath)

fun File.hasCacheVersion(context: Context): Boolean = logCacheFile(context).exists()

fun File.logCacheFile(context: Context): File = File(context.cacheDir, name)

fun File.isPdf(): Boolean = this.name.endsWith(".pdf", ignoreCase = true)

fun File.isWord(): Boolean =
	name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".dotx") || name.endsWith(".docb")

fun File.isExcel(): Boolean =
	name.endsWith(".xls") || name.endsWith(".xlsx") || name.endsWith(".csv")

fun File.isPowerPoint(): Boolean = (name.endsWith(".ppt") || name.endsWith(".pptx"))

fun File.isTxt(): Boolean = this.name.endsWith(".txt", ignoreCase = true)