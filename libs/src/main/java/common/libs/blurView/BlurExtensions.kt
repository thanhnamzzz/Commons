package common.libs.blurView

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window

fun Window.setupBlurBackground(
	viewBlur: BlurView,
	viewTarget: BlurTarget,
	radius: Float = 10f,
	withBackground: Boolean = true
) {
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
}