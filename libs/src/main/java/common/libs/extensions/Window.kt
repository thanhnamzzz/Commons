package common.libs.extensions

import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Window.updateStatusBarColors(backgroundColor: Int) {
    statusBarColor = backgroundColor
    updateStatusBarForegroundColor(backgroundColor)
}

fun Window.updateStatusBarForegroundColor(backgroundColor: Int) {
    if (isR30Plus()) {
        if (backgroundColor.getContrastColor() == DARK_GREY) {
            //Background is Dark -> set light text
            insetsController?.apply {
                setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        } else {
            //Background is Light -> set dark text
            insetsController?.apply {
                setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
//	} else if (isM23Plus()) {
//		if (backgroundColor.getContrastColor() == DARK_GREY) {
//			decorView.systemUiVisibility =
//				decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
//		} else {
//			decorView.systemUiVisibility =
//				decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
//		}
    }
}

fun Window.updateNavigationBarColors(backgroundColor: Int) {
    navigationBarColor = backgroundColor
    updateNavigationBarForegroundColor(backgroundColor)
}

fun Window.updateNavigationBarForegroundColor(backgroundColor: Int) {
    if (isR30Plus()) {
        if (backgroundColor.getContrastColor() == DARK_GREY) {
            insetsController?.apply {
                setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }
        } else {
            insetsController?.apply {
                setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }
        }
    } else if (isO26Plus()) {
        if (backgroundColor.getContrastColor() == DARK_GREY) {
            decorView.systemUiVisibility =
                decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        } else {
            decorView.systemUiVisibility =
                decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
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