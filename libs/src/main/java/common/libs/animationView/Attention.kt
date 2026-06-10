/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object Attention {
	fun bounce(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, 0f, -30f, 0f, -15f, 0f, 0f)
		)
	}

	fun flash(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f, 1f, 0f, 1f)
		)
	}

	fun pulse(view: View, toScale: Float = 1.1f): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, toScale, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, toScale, 1f)
		)
	}

	fun rubberBand(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.25f, 0.75f, 1.15f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.75f, 1.25f, 0.85f, 1f)
		)
	}

	fun shake(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(
				view,
				View.TRANSLATION_X,
				0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
			)
		)
	}

	fun standup(view: View): AnimatorSet = AnimatorSet().apply {
		val x =
			((view.width - view.paddingLeft - view.paddingRight) / 2 + view.paddingLeft).toFloat()
		val y = (view.height - view.paddingBottom).toFloat()

		playTogether(
			ObjectAnimator.ofFloat(view, "pivotX", x, x, x, x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y, y, y, y),
			ObjectAnimator.ofFloat(view, View.ROTATION_X, 55f, -30f, 15f, -15f, 0f)
		)
	}

	fun swing(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 10f, -10f, 6f, -6f, 3f, -3f, 0f)
		)
	}

	fun tada(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(
				view, View.SCALE_X, 1f, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1f
			),
			ObjectAnimator.ofFloat(
				view, View.SCALE_Y, 1f, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1f
			),
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, -3f, -3f, 3f, -3f, 3f, -3f, 3f, -3f, 0f)
		)
	}

	fun wave(view: View): AnimatorSet = AnimatorSet().apply {
		val x =
			((view.width - view.paddingLeft - view.paddingRight) / 2 + view.paddingLeft).toFloat()
		val y = (view.height - view.paddingBottom).toFloat()

		playTogether(
			ObjectAnimator.ofFloat(view, View.ROTATION, 12f, -12f, 3f, -3f, 0f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x, x, x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y, y, y, y)
		)
	}

	fun wobble(view: View): AnimatorSet = AnimatorSet().apply {
		val width = (view.width).toFloat()
		val one = (width / 100.0).toFloat()

		playTogether(
			ObjectAnimator.ofFloat(
				view,
				View.TRANSLATION_X,
				0f * one,
				-25f * one,
				20f * one,
				-15f * one,
				10f * one,
				-5f * one,
				0f * one,
				0f
			),
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, -5f, 3f, -3f, 2f, -1f, 0f)
		)
	}
}
