package common.libs.extensions

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat


fun View.isKeyboardVisible(): Boolean {
	val insets = ViewCompat.getRootWindowInsets(this)
	return insets?.isVisible(WindowInsetsCompat.Type.ime()) == true
}

fun Activity.hideKeyboard() {
	val controller = WindowCompat.getInsetsController(window, window.decorView)
	controller.hide(WindowInsetsCompat.Type.ime())
}