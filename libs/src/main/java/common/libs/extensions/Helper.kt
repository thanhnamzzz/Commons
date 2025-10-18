package common.libs.extensions

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import common.libs.views.TypeToast
import common.libs.views.showToastError
import common.libs.views.showToastNone
import common.libs.views.showToastSuccess
import common.libs.views.showToastWarning

fun reloadGallerySystem(context: Context, filePath: String) {
    MediaScannerConnection.scanFile(
        context, arrayOf(filePath), null
    ) { _, _ -> }
}

fun Activity.toastMess(
    mess: String,
    typeToast: TypeToast,
    @IntRange(from = 0, to = 1) duration: Int = 0,
) {
    when (typeToast) {
        TypeToast.TOAST_SUCCESS -> showToastSuccess(mess, duration)
        TypeToast.TOAST_ERROR -> showToastError(mess, duration)
        TypeToast.TOAST_WARNING -> showToastWarning(mess, duration)
        TypeToast.TOAST_NONE -> showToastNone(mess, duration)
    }
}

fun handlerFunction(timeWait: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ callback() }, timeWait)
}