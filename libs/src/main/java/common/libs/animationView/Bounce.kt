/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object Bounce {
	fun bounceIn(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 0.3f, 1.05f, 0.9f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.3f, 1.05f, 0.9f, 1f)
		)
	}

	fun bounceInLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val width = -view.width.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, width, 30f, -10f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f, 1f)
		)
	}

	fun bounceInRight(view: View): AnimatorSet = AnimatorSet().apply {
		val measuredWidth = -view.measuredWidth.toFloat()
		val width = -view.width.toFloat()
		// Giữ nguyên logic cũ: measured_width + width
		playTogether(
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, measuredWidth + width, -30f, 10f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f, 1f)
		)
	}

	fun bounceInUp(view: View): AnimatorSet = AnimatorSet().apply {
		val measuredHeight = view.measuredHeight.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, measuredHeight, -30f, 10f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f, 1f)
		)
	}

	fun bounceInDown(view: View): AnimatorSet = AnimatorSet().apply {
		val height = -view.height.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, height, 30f, -10f, 0f)
		)
	}
}