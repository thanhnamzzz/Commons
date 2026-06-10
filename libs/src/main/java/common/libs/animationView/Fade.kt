/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object Fade {
	fun fadeIn(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
		)
	}

	fun fadeInLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val width = -view.width.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, width / 4f, 0f)
		)
	}

	fun fadeInRight(view: View): AnimatorSet = AnimatorSet().apply {
		val width = view.width.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, width / 4f, 0f)
		)
	}

	fun fadeInUp(view: View): AnimatorSet = AnimatorSet().apply {
		val height = view.height.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, height / 4f, 0f)
		)
	}

	fun fadeInDown(view: View): AnimatorSet = AnimatorSet().apply {
		val height = -view.height.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, height / 4f, 0f)
		)
	}

	fun fadeOut(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)
		)
	}

	fun fadeOutLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val width = -view.width.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, width / 4f)
		)
	}

	fun fadeOutRight(view: View): AnimatorSet = AnimatorSet().apply {
		val width = view.width.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, width / 4f)
		)
	}

	fun fadeOutUp(view: View): AnimatorSet = AnimatorSet().apply {
		val height = view.height.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, height / 4f)
		)
	}

	fun fadeOutDown(view: View): AnimatorSet = AnimatorSet().apply {
		val height = -view.height.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, height / 4f)
		)
	}
}