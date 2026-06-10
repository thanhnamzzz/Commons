/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup

object Slide {
	fun slideInDown(view: View): AnimatorSet = AnimatorSet().apply {
		val distance = (view.top + view.height).toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -distance, 0f)
		)
	}

	fun slideInLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.width - view.left).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -distance, 0f)
		)
	}

	fun slideInRight(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.width - view.left).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, distance, 0f)
		)
	}

	fun slideInUp(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.height - view.top).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, distance, 0f)
		)
	}

	fun slideOutDown(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.height - view.top).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, distance)
		)
	}

	fun slideOutLeft(view: View): AnimatorSet = AnimatorSet().apply {
		val right = view.right.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, -right)
		)
	}

	fun slideOutRight(view: View): AnimatorSet = AnimatorSet().apply {
		val parent = view.parent as? ViewGroup
		val distance = parent?.let { (it.width - view.left).toFloat() } ?: 0f
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, distance)
		)
	}

	fun slideOutUp(view: View): AnimatorSet = AnimatorSet().apply {
		val bottom = -view.bottom.toFloat()
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, bottom)
		)
	}
}