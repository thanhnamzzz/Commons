package common.libs.customView.shimmer

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View

class Shimmer {

	companion object {
		const val ANIMATION_DIRECTION_LTR = 0
		const val ANIMATION_DIRECTION_RTL = 1

		private const val DEFAULT_REPEAT_COUNT = ValueAnimator.INFINITE
		private const val DEFAULT_DURATION = 1000L
		private const val DEFAULT_START_DELAY = 0L
		private const val DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR
	}

	private var repeatCount: Int = DEFAULT_REPEAT_COUNT
	private var duration: Long = DEFAULT_DURATION
	private var startDelay: Long = DEFAULT_START_DELAY
	private var direction: Int = DEFAULT_DIRECTION
	private var animatorListener: Animator.AnimatorListener? = null

	private var animator: ObjectAnimator? = null

	fun getRepeatCount(): Int = repeatCount

	fun setRepeatCount(repeatCount: Int): Shimmer {
		this.repeatCount = repeatCount
		return this
	}

	fun getDuration(): Long = duration

	fun setDuration(duration: Long): Shimmer {
		this.duration = duration
		return this
	}

	fun getStartDelay(): Long = startDelay

	fun setStartDelay(startDelay: Long): Shimmer {
		this.startDelay = startDelay
		return this
	}

	fun getDirection(): Int = direction

	fun setDirection(direction: Int): Shimmer {
		if (direction != ANIMATION_DIRECTION_LTR && direction != ANIMATION_DIRECTION_RTL) {
			throw IllegalArgumentException("The animation direction must be either ANIMATION_DIRECTION_LTR or ANIMATION_DIRECTION_RTL")
		}
		this.direction = direction
		return this
	}

	fun getAnimatorListener(): Animator.AnimatorListener? = animatorListener

	fun setAnimatorListener(animatorListener: Animator.AnimatorListener?): Shimmer {
		this.animatorListener = animatorListener
		return this
	}

	fun <V> start(shimmerView: V) where V : View, V : ShimmerViewBase {
		if (isAnimating()) return

		val animate = {
			shimmerView.isShimmering = true

			var fromX = 0f
			var toX = shimmerView.width.toFloat()
			if (direction == ANIMATION_DIRECTION_RTL) {
				fromX = shimmerView.width.toFloat()
				toX = 0f
			}

			animator = ObjectAnimator.ofFloat(shimmerView, "gradientX", fromX, toX).apply {
				repeatCount = this@Shimmer.repeatCount
				duration = this@Shimmer.duration
				startDelay = this@Shimmer.startDelay

				addListener(object : Animator.AnimatorListener {
					override fun onAnimationStart(animation: Animator) {}

					override fun onAnimationEnd(animation: Animator) {
						shimmerView.isShimmering = false
						shimmerView.postInvalidateOnAnimation()
						animator = null
					}

					override fun onAnimationCancel(animation: Animator) {}
					override fun onAnimationRepeat(animation: Animator) {}
				})

				animatorListener?.let { addListener(it) }
				start()
			}
		}

		if (!shimmerView.isSetUp) {
			shimmerView.setAnimationSetupCallback(object : ShimmerViewHelper.AnimationSetupCallback{
				override fun onSetupAnimation(target: View?) {
					animate()
				}
			})
		} else {
			animate()
		}
	}

	fun cancel() {
		animator?.cancel()
	}

	fun isAnimating(): Boolean = animator?.isRunning == true
}