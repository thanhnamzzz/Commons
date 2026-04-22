package common.libs.blurView

import android.graphics.Outline
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window

fun Window.setupBlurBackground(
	viewBlur: BlurView,
	viewTarget: BlurTarget,
	radius: Float = 10f,
	withBackground: Boolean = true,
	setupBlurFail: () -> Unit = {}
) {
	viewBlur.post {
		try {
			viewBlur.apply {
				if (withBackground) {
					clipToOutline = true
					outlineProvider = object : ViewOutlineProvider() {
						override fun getOutline(p0: View?, p1: Outline?) {
							p1?.let {
								viewBlur.background.getOutline(it)
								it.alpha = 1f
							}
						}
					}
				}
				setupWith(viewTarget)
					.setFrameClearDrawable(decorView.background)
					.setBlurRadius(radius)
			}
		} catch (e: Exception) {
			Log.e("Namzzz", "BlurView: setupBlurBackground ERROR:", e)
			setupBlurFail()
		}
	}
}