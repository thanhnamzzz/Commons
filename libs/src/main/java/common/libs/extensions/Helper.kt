package common.libs.extensions

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

fun reloadGallerySystem(context: Context, filePath: String) {
    MediaScannerConnection.scanFile(
        context, arrayOf(filePath), null
    ) { _, _ -> }
}

fun postDelayedHandler(timeWait: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ callback() }, timeWait)
}

fun LifecycleOwner.postDelayed(
    timeWait: Long,
    callback: () -> Unit
) {
    lifecycleScope.launch {
        delay(timeWait.milliseconds)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            callback()
        }
    }
}