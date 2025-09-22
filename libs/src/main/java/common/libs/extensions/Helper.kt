package common.libs.extensions

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Handler
import android.os.Looper
import common.libs.views.MyToast

fun reloadGallerySystem(context: Context, filePath: String) {
	MediaScannerConnection.scanFile(
		context, arrayOf(filePath), null
	) { _, _ -> }
}

fun toastMess(activity: Activity, mess: String, duration: Int, typeToast: MyToast.TypeToast) {
	when (typeToast) {
		MyToast.TypeToast.TOAST_SUCCESS -> MyToast.showToastSuccess(activity, mess, duration)
		MyToast.TypeToast.TOAST_ERROR -> MyToast.showToastError(activity, mess, duration)
		MyToast.TypeToast.TOAST_WARNING -> MyToast.showToastWarning(activity, mess, duration)
		MyToast.TypeToast.TOAST_NONE -> MyToast.showToastNone(activity, mess, duration)
	}
}

fun handlerFunction(timeWait: Long, callback: () -> Unit) {
	Handler(Looper.getMainLooper()).postDelayed({ callback() }, timeWait)
}