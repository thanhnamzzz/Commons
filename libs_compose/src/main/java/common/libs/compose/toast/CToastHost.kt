/** Clone from https://github.com/dononcharles/NiceToast
 * from commit 91b458d
 */
package common.libs.compose.toast

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * HD:
 * Khai báo 1 val cToast = CToastState() và 1 val scope = rememberCoroutineScope()
 *
 * gọi scope.launch {cToast.show(...)} để khai báo nội dung cho toast
 *
 * Quan trọng: phải gọi tới CToastHost(hostState = cToast, modifier = Modifier.systemBarsPadding()) ở cuối function Composable
 * để có thể hiển thị của toast view
 *
 * A composable that observes a [CToastState] and displays toasts when they are requested.
 *
 * This host should be placed at a high level in your UI hierarchy, typically within a `Scaffold`
 * or a top-level `Box`. It acts as an overlay that can display content (toasts) above your
 * main screen content. Its `contentAlignment` is dynamically updated based on the toast's
 * requested position.
 *
 * The appearance and animations of the toast are handled declaratively using [AnimatedVisibility].
 *
 * @param hostState The [CToastState] that holds the current toast data. The host will
 *   observe this state and display or dismiss toasts accordingly.
 * @param modifier The [Modifier] to be applied to the host's container [Box]. Use this for
 *   sizing and positioning the host itself if needed, although it defaults to filling the max size.
 * @param enter The [EnterTransition] used to animate the toast in. Defaults to a fade-in and slide-in.
 *   This can be customized for different animation effects.
 * @param exit The [ExitTransition] used to animate the toast out. Defaults to a fade-out and slide-out.
 *   This can be customized for different animation effects.
 */
@Composable
fun CToastHost(
    hostState: CToastState,
    modifier: Modifier = Modifier,
    // Customize the animations from the outside for maximum flexibility.
    enter: EnterTransition = fadeIn() + slideInVertically(),
    exit: ExitTransition = fadeOut() + slideOutVertically()
) {
    val currentData = hostState.currentToastData

    // This LaunchedEffect is the core of the auto-dismiss logic.
    // By keying the effect on `currentData?.id`, we ensure this block re-runs *only* when a
    // new toast appears (because the ID will be different). If the key were `currentData`,
    // it wouldn't re-run for a new toast with identical content.
    LaunchedEffect(currentData?.id) {
        currentData?.let { cD ->
            delay(cD.duration)
            hostState.dismiss()
        }
    }

//    val alignment = when (currentData?.position) {
//        CToastPosition.TOP -> Alignment.TopCenter
//        CToastPosition.CENTER -> Alignment.Center
//        else -> Alignment.BottomCenter
//    }
    // theo libs gốc thì có 3 vị trí nhưng top vs center bị lỗi hiệu ứng thu nhỏ biến mất
    // hiệu ứng này chỉ chạy ở bottom
    // nên là cho position mặc định là Bottom
    val alignment = Alignment.BottomCenter

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 16.dp),
        contentAlignment = alignment
    ) {
        AnimatedContent(
            targetState = currentData,
            transitionSpec = {
                if (targetState != null) {
                    enter togetherWith exit
                } else {
                    enter togetherWith exit
                }
            },
            label = "NiceToastAnimation"
        ) { toastData ->
            toastData?.let { data ->
                CToastView(
                    data = data,
                    // Retrieve the globally provided configuration via CompositionLocal.
                    config = LocalCToastConfig.current
                )
            }
        }
    }
}