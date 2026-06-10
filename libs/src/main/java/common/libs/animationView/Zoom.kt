/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup

object Zoom {
	fun zoomIn(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.SCALE_X, 0.45f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.45f, 1f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
		)
	}

	fun zoomInDown(view: View): AnimatorSet = AnimatorSet().apply {
		val bottom = -view.bottom.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.SCALE_X, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, bottom, 60f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f)
		)
	}

	fun zoomInLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val right = -view.right.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.SCALE_X, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, right, 48f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f)
		)
	}

	fun zoomInRight(view: View): AnimatorSet = AnimatorSet().apply {
		val width = -view.width.toFloat()
		val right = -view.paddingRight.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.SCALE_X, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, width + right, -48f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f)
		)
	}

	fun zoomInUp(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.height - view.top).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 0.475f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, distance, -60f, 0f)
		)
	}

	fun zoomOut(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f, 0f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.3f, 0f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.3f, 0f)
		)
	}

	fun zoomOutDown(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.height - view.top).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -60f, distance)
		)
	}

	fun zoomOutLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val right = -view.right.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, 42f, right)
		)
	}

	fun zoomOutRight(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.width - it.left).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, -42f, distance)
		)
	}

	fun zoomOutUp(view: View): AnimatorSet = AnimatorSet().apply {
		val bottom = -view.bottom.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.475f, 0.1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, 60f, bottom)
		)
	}
}