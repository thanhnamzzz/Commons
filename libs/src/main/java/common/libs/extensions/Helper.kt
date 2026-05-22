package common.libs.extensions

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import common.libs.views.Duration
import common.libs.views.ToastStyle
import common.libs.views.ToastTheme
import common.libs.views.TypeToast
import common.libs.views.showError
import common.libs.views.showNone
import common.libs.views.showSuccess
import common.libs.views.showWarning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun reloadGallerySystem(context: Context, filePath: String) {
    MediaScannerConnection.scanFile(
        context, arrayOf(filePath), null
    ) { _, _ -> }
}

fun Activity.toastMess(
	mess: String,
	typeToast: TypeToast,
	duration: Long = Duration.SHORT,
    style: ToastStyle = ToastStyle.VERTICAL,
    theme: ToastTheme = ToastTheme.SOFT,
) {
    when (typeToast) {
        TypeToast.SUCCESS -> showSuccess(mess, duration, style, theme)
        TypeToast.ERROR -> showError(mess, duration, style, theme)
        TypeToast.WARNING -> showWarning(mess, duration, style, theme)
        TypeToast.NONE -> showNone(mess, duration, style, theme)
    }
}

@Deprecated(
    message = "Unsafe: this function does not respect lifecycle. " +
            "Use handlerFunction(timeWait, LifecycleOwner, callback) instead.",
    level = DeprecationLevel.WARNING
)
fun handlerFunction(timeWait: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ callback() }, timeWait)
}

fun LifecycleOwner.handlerFunction(
    timeWait: Long,
    callback: () -> Unit
) {
    lifecycleScope.launch {
        delay(timeWait)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            callback()
        }
    }
}