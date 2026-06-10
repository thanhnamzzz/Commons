/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object Flip {
	fun flipInX(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0.25f, 0.5f, 0.75f, 1f),
			ObjectAnimator.ofFloat(view, View.ROTATION_X, 90f, -15f, 15f, 0f)
		)
	}

	fun flipInY(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 0.25f, 0.5f, 0.75f, 1f),
			ObjectAnimator.ofFloat(view, View.ROTATION_Y, 90f, -15f, 15f, 0f)
		)
	}

	fun flipOutX(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.ROTATION_X, 0f, 90f)
		)
	}

	fun flipOutY(view: View): AnimatorSet = AnimatorSet().apply {
		playTogether(
			ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f),
			ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0f, 90f)
		)
	}
}