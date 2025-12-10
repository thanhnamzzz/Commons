/** Clone from https://github.com/dononcharles/NiceToast
 * from commit 91b458d
 */
package common.libs.compose.toast

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID


/**
 * A data class that holds all the configuration and content for a single toast message.
 * This object is designed to be immutable.
 *
 * @property id A unique identifier for this specific toast instance. This is crucial for
 * Jetpack Compose's LaunchedEffect to correctly trigger and re-trigger animations and
 * timers, even if the toast message content is identical to the previous one.
 * @property message The main content message to be displayed in the toast.
 * @property title An optional title displayed above the message.
 * @property type The visual style of the toast (e.g., SUCCESS, ERROR).
 * @property duration The time in milliseconds for which the toast will be visible before
 * it automatically dismisses.
 * @property isDarkMode Whether the toast should render in its dark theme variant. This should
 * typically be derived from the app's current theme (e.g., using `isSystemInDarkTheme()`).
 * @property isFullBackground If true, the toast background will be a solid color. If false,
 * it will have a side-bar style.
 * @property position The screen position where the toast will appear (TOP, CENTER, or BOTTOM).
 */
@Stable
internal class CToastData(
	val id: String = UUID.randomUUID().toString(),
	val message: String = "",
	val title: String? = null,
	val type: CToastType = CToastType.SUCCESS,
	val duration: Long = DURATION_SHORT,
	val isDarkMode: Boolean = false,
	val isFullBackground: Boolean = true,
//	val position: CToastPosition
)


/**
 * A state manager for the [CToastHost]. It controls the queue and visibility of toasts.
 *
 * This class is analogous to Jetpack Compose's `SnackbarHostState`. It should be created
 * and remembered within a Composable function and hoisted to control the toast display.
 * The `show` method is thread-safe, ensuring that multiple rapid calls do not corrupt the state.
 *
 * @see CToastHost
 */
@Stable
class CToastState {
	/**
	 * A Mutex to ensure that toast display requests are handled atomically. This prevents
	 * race conditions if `show` is called from multiple coroutines simultaneously.
	 */
	private val mutex = Mutex()

	/**
	 * The currently visible toast data. It is a mutable state object observed by the
	 * [CToastHost] Composable. When this value changes, the UI will react to either
	 * show a new toast or hide the current one. The setter is private to ensure all
	 * changes are channeled through the `show` and `dismiss` methods.
	 */
	internal var currentToastData by mutableStateOf<CToastData?>(null)
		private set

	/**
	 * Displays a toast with the given parameters. This function is a suspending function
	 * and must be called from a coroutine scope. It ensures that only one toast is shown
	 * at a time.
	 *
	 * @param message The main content of the toast.
	 * @param title An optional title.
	 * @param type The style of the toast (SUCCESS, ERROR, etc.).
	 * @param duration How long the toast should be visible.
	 * @param isDarkMode Set to true for a dark theme look.
	 * @param isFullBackground Set to true for a solid background color.
	 * @param position The gravity of the toast on the screen.
	 */
	suspend fun setAndShow(
		message: String="",
		title: String? = null,
		type: CToastType = CToastType.SUCCESS,
		duration: Long = DURATION_SHORT,
		isDarkMode: Boolean = false,
		isFullBackground: Boolean = true,
//		position: CToastPosition = CToastPosition.BOTTOM
	) {
		// Lock the mutex to ensure that we don't have multiple threads
		// trying to update the currentToastData at the same time.
		mutex.withLock {
			// Create a new data object and assign it to the mutable state.
			// This assignment will trigger a recomposition in the NiceToastHost.
			currentToastData = CToastData(
				message = message,
				title = title,
				type = type,
				duration = duration,
				isDarkMode = isDarkMode,
				isFullBackground = isFullBackground,
//				position = position
			)
		}
	}

	/**
	 * Immediately dismisses the currently visible toast, if there is one.
	 * This can be used for manual dismissal based on user action or other logic.
	 */
	fun dismiss() {
		currentToastData = null
	}
}