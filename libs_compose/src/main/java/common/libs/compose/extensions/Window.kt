package common.libs.compose.extensions

import android.os.Build
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun Window.SetStatusBarContentColor(backgroundColor: Color) {
	val insetsController = WindowCompat.getInsetsController(this, decorView)

	val isLightBackground = backgroundColor.luminance() > 0.5f

	SideEffect {
		if (isLightBackground) {
			// Background sáng → icon dark
			insetsController.isAppearanceLightStatusBars = true
		} else {
			// Background tối → icon light
			insetsController.isAppearanceLightStatusBars = false
		}
	}
}

@Composable
fun Window.SetNavigationBarContentColor(
	backgroundColor: Color,
	enforceContrast: Boolean = false,
) {
	val controllers = WindowCompat.getInsetsController(this, decorView)

	val isLightBackground = backgroundColor.luminance() > 0.5f

	SideEffect {
		// (3) Đổi màu icon/navigation bar
		controllers.isAppearanceLightNavigationBars = isLightBackground
		// (1) Set navigation bar background
		if (enforceContrast) {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
				if (isLightBackground) {
					insetsController?.apply {
						setSystemBarsAppearance(
							0,
							WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
						)
					}
				} else {
					insetsController?.apply {
						setSystemBarsAppearance(
							WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
							WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
						)
					}
				}
			} else {
				navigationBarColor = backgroundColor.toArgb()
			}
		}
		// (2) Bật / tắt contrast enforcement
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			isNavigationBarContrastEnforced = enforceContrast
		}
	}
}

fun Window.keepScreenOn() {
	this.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Window.clearKeepScreenOn() {
	this.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Window.hideSystemNavigationBar() {
	WindowCompat.setDecorFitsSystemWindows(this, false)

	WindowInsetsControllerCompat(this, decorView).let { controller ->
		controller.hide(WindowInsetsCompat.Type.navigationBars())
		controller.show(WindowInsetsCompat.Type.statusBars())
		controller.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
	}
}

fun Window.hideSystemStatusBar() {
	WindowCompat.setDecorFitsSystemWindows(this, false)

	WindowInsetsControllerCompat(this, decorView).let { controller ->
		controller.show(WindowInsetsCompat.Type.navigationBars())
		controller.hide(WindowInsetsCompat.Type.statusBars())
		controller.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
	}
}

fun Window.hideSystemUiBar() {
	WindowCompat.setDecorFitsSystemWindows(this, false)

	WindowInsetsControllerCompat(this, decorView).let { controller ->
		controller.hide(WindowInsetsCompat.Type.navigationBars())
		controller.hide(WindowInsetsCompat.Type.statusBars())
		controller.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
	}
}

fun Window.showSystemUiBar() {
	WindowCompat.setDecorFitsSystemWindows(this, false)

	WindowInsetsControllerCompat(this, decorView).let { controller ->
		controller.show(WindowInsetsCompat.Type.navigationBars())
		controller.show(WindowInsetsCompat.Type.statusBars())
		controller.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
	}
}

fun Window.showSystemStatusBar() {
	WindowCompat.setDecorFitsSystemWindows(this, false)

	WindowInsetsControllerCompat(this, decorView).let { controller ->
		controller.show(WindowInsetsCompat.Type.statusBars())
		controller.hide(WindowInsetsCompat.Type.navigationBars())
		controller.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
	}
}

fun Window.showSystemNavigationBar() {
	WindowCompat.setDecorFitsSystemWindows(this, false)

	WindowInsetsControllerCompat(this, decorView).let { controller ->
		controller.hide(WindowInsetsCompat.Type.statusBars())
		controller.show(WindowInsetsCompat.Type.navigationBars())
		controller.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
	}
}