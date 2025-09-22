package common.libs.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.graphics.createBitmap

fun Bitmap?.saveBitmapToJpgCache(context: Context, fileName: String): String? {
    val cacheDir: File = context.cacheDir
    val file = File(cacheDir, fileName)
    if (this != null) {
        if (file.exists()) if (!file.delete()) return null
        return try {
            FileOutputStream(file).use { outputStream ->
                this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            file.absolutePath
        } catch (_: IOException) {
            null
        }
    } else return null
}

fun Bitmap.saveBitmapToPng(folder: String, fileName: String): String? {
    Log.d("Namzzz", ": saveBitmapToPng")
    val directory = File(folder)
    if (!directory.exists())
        directory.mkdirs()
    val file = File(directory, "$fileName.png")
    try {
        FileOutputStream(file).use {
            this.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return if (file.exists()) file.absolutePath else null
}

fun Bitmap.bitmapResize(newWidth: Int, newHeight: Int): Bitmap {
    val width = this.width
    val height = this.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)

    // "RECREATE" THE NEW BITMAP
    val newBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
    val bitmap = createBitmap(newWidth, newHeight)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.TRANSPARENT)
    canvas.drawBitmap(newBitmap, 0f, 0f, null)
    return bitmap
}

fun Bitmap.cropWithRect(cropRect: Rect): Bitmap {
    return Bitmap.createBitmap(
        this,
        cropRect.left,
        cropRect.top,
        cropRect.width(),
        cropRect.height()
    )
}