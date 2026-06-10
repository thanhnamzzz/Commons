/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object Rotate {
	fun rotateIn(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.ROTATION, -200f, 0f)
		)
	}

	fun rotateInDownLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val x = view.paddingLeft.toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ROTATION, -90f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}

	fun rotateInDownRight(view: View): AnimatorSet = AnimatorSet().apply {
		val x = (view.width - view.paddingRight).toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ROTATION, 90f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}

	fun rotateInUpLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val x = view.paddingLeft.toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ROTATION, 90f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}

	fun rotateInUpRight(view: View): AnimatorSet = AnimatorSet().apply {
		val x = (view.width - view.paddingRight).toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ROTATION, -90f, 0f),
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}

	fun rotateOut(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 200f)
		)
	}

	fun rotateOutDownLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val x = view.paddingLeft.toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 90f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}

	fun rotateOutDownRight(view: View): AnimatorSet = AnimatorSet().apply {
		val x = (view.width - view.paddingRight).toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, -90f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}

	fun rotateOutUpLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val x = view.paddingLeft.toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, -90f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}

	fun rotateOutUpRight(view: View): AnimatorSet = AnimatorSet().apply {
		val x = (view.width - view.paddingRight).toFloat()
		val y = (view.height - view.paddingBottom).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 90f),
			ObjectAnimator.ofFloat(view, "pivotX", x, x),
			ObjectAnimator.ofFloat(view, "pivotY", y, y)
		)
	}
}