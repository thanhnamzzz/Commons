package common.libs.compose.functions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun HideKeyboard() {
	val keyboardController = LocalSoftwareKeyboardController.current
	val focusManager = LocalFocusManager.current
	LaunchedEffect(Unit) {
		focusManager.clearFocus()
		keyboardController?.hide()
	}
}

@Composable
fun ShowKeyboard(focusRequester: FocusRequester) {
	val keyboardController = LocalSoftwareKeyboardController.current

	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
		keyboardController?.show()
	}
}