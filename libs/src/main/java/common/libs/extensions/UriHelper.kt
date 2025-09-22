package common.libs.extensions

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri

fun Uri.toBitmap(activity: Activity): Bitmap? {
    return try {
        val source = activity.contentResolver.openInputStream(this)
        BitmapFactory.decodeStream(source)
    } catch (e: Exception) {
        null
    }
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun flipBitmap(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix()
    if (horizontal) matrix.postScale(-1f, 1f) // Lật ngang
    if (vertical) matrix.postScale(1f, -1f) // Lật dọc
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}