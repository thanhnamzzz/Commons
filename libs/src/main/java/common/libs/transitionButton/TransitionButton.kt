package common.libs.transitionButton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import common.libs.R
import androidx.core.content.withStyledAttributes

class TransitionButton @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

	companion object {
		private const val WIDTH_ANIMATION_DURATION = 200L
		private const val SCALE_ANIMATION_DURATION = 300L
		private const val SHAKE_ANIMATION_DURATION = 500L
		private const val COLOR_ANIMATION_DURATION = 350L
	}

	private var messageAnimationDuration = COLOR_ANIMATION_DURATION * 10

	private var currentState: State = State.IDLE
	private var isMorphingInProgress = false

	private var initialWidth = 0
	private var initialHeight = 0
	private var initialText: String? = null

	private var defaultColor = 0
	private var errorColor = 0
	private var loaderColor = 0

	private var progressCircularAnimatedDrawable: CircularAnimatedDrawable? = null

	init {
		currentState = State.IDLE

		errorColor = ContextCompat.getColor(context, R.color.colorError)
		loaderColor = ContextCompat.getColor(context, R.color.colorLoader)

		val typedValue = TypedValue()
		context.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
		defaultColor = typedValue.data

		context.withStyledAttributes(attrs, R.styleable.TransitionButton) {
			getString(R.styleable.TransitionButton_defaultColor)?.let { dc ->
				defaultColor = ColorUtils.parse(dc)
			}

			getString(R.styleable.TransitionButton_loaderColor)?.let { lc ->
				loaderColor = ColorUtils.parse(lc)
			}

			getString(R.styleable.TransitionButton_errorColor)?.let { ec ->
				errorColor = ColorUtils.parse(ec)
			}
		}

		backgroundTintList = ColorStateList.valueOf(defaultColor)
		background = ContextCompat.getDrawable(context, R.drawable.transition_button_shape_idle)
	}

	fun startAnimation() {
		currentState = State.PROGRESS
		isMorphingInProgress = true

		initialWidth = width
		initialHeight = height
		initialText = text.toString()

		text = null
		isClickable = false

		startWidthAnimation(initialHeight, object : AnimatorListenerAdapter() {
			override fun onAnimationEnd(animation: Animator) {
				isMorphingInProgress = false
			}
		})
	}

	fun setMessageAnimationDuration(duration: Int) {
		this.messageAnimationDuration = duration.toLong()
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		if (currentState == State.PROGRESS && !isMorphingInProgress) {
			drawIndeterminateProgress(canvas)
		}
	}

	private fun drawIndeterminateProgress(canvas: Canvas) {
		val drawable = progressCircularAnimatedDrawable
		if (drawable == null || !drawable.isRunning) {
			val arcWidth = (height / 18).toFloat()

			val newDrawable = CircularAnimatedDrawable(loaderColor, arcWidth)

			val offset = (width - height) / 2

			val right = width - offset
			val bottom = height
			val top = 0

			newDrawable.setBounds(offset, top, right, bottom)
			newDrawable.callback = this
			newDrawable.start()
			progressCircularAnimatedDrawable = newDrawable
		} else {
			drawable.draw(canvas)
			invalidate()
		}
	}

	fun stopAnimation(
		stopAnimationStyle: StopAnimationStyle,
		onAnimationStopEndListener: OnAnimationStopEndListener? = null
	) {
		when (stopAnimationStyle) {
			StopAnimationStyle.SHAKE -> {
				currentState = State.ERROR

				startWidthAnimation(initialWidth, object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						text = initialText
						startShakeAnimation(object : AnimationListenerAdapter() {
							override fun onAnimationEnd(animation: Animation) {
								currentState = State.IDLE
								isClickable = true
								onAnimationStopEndListener?.onAnimationStopEnd()
							}
						})
					}
				})
			}

			StopAnimationStyle.NULL -> {
				currentState = State.ERROR

				startWidthAnimation(initialWidth, object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						currentState = State.IDLE
						text = initialText
						isClickable = true
						onAnimationStopEndListener?.onAnimationStopEnd()
					}
				})
			}

			StopAnimationStyle.EXPAND -> {
				currentState = State.TRANSITION

				startScaleAnimation(object : AnimationListenerAdapter() {
					override fun onAnimationEnd(animation: Animation) {
						super.onAnimationEnd(animation)
						onAnimationStopEndListener?.onAnimationStopEnd()
					}
				})
			}
		}
	}

	/**
	 * Reset button to its initial state.
	 * Useful after EXPAND animation or when needing to retry an action.
	 */
	fun reset() {
		currentState = State.IDLE
		isClickable = true
		isMorphingInProgress = false

		initialText?.let { text = it }

		if (initialWidth != 0) {
			val params = layoutParams
			params.width = initialWidth
			layoutParams = params
		}

		clearAnimation()

		backgroundTintList = ColorStateList.valueOf(defaultColor)
		progressCircularAnimatedDrawable?.stop()
	}

	private fun startWidthAnimation(to: Int, onAnimationEnd: AnimatorListenerAdapter?) {
		startWidthAnimation(width, to, onAnimationEnd)
	}

	private fun startWidthAnimation(from: Int, to: Int, onAnimationEnd: AnimatorListenerAdapter?) {
		val widthAnimation = ValueAnimator.ofInt(from, to).apply {
			addUpdateListener { valueAnimator ->
				val valAnim = valueAnimator.animatedValue as Int
				val params = layoutParams
				params.width = valAnim
				layoutParams = params
			}
		}

		AnimatorSet().apply {
			duration = WIDTH_ANIMATION_DURATION
			playTogether(widthAnimation)
			onAnimationEnd?.let { addListener(it) }
			start()
		}
	}

	private fun startShakeAnimation(animationListener: Animation.AnimationListener) {
		TranslateAnimation(0f, 15f, 0f, 0f).apply {
			duration = SHAKE_ANIMATION_DURATION
			interpolator = CycleInterpolator(4f)
			setAnimationListener(animationListener)
			startAnimation(this)
		}
	}

	private fun startScaleAnimation(animationListener: Animation.AnimationListener) {
		val ts = (WindowUtils.getHeight(context).toFloat() / height * 2.1).toFloat()
		ScaleAnimation(
			1f, ts,
			1f, ts,
			Animation.RELATIVE_TO_SELF, 0.5f,
			Animation.RELATIVE_TO_SELF, 0.5f
		).apply {
			duration = SCALE_ANIMATION_DURATION
			fillAfter = true
			setAnimationListener(animationListener)
			startAnimation(this)
		}
	}

	fun showErrorMessage(message: String) {
		text = message
		isClickable = false
		startColorAnimation(defaultColor, errorColor)

		Handler(Looper.getMainLooper()).postDelayed({
			text = initialText
			isClickable = true
			startColorAnimation(errorColor, defaultColor)
		}, messageAnimationDuration)
	}

	private fun startColorAnimation(from: Int, to: Int) {
		ValueAnimator.ofArgb(from, to).apply {
			addUpdateListener { valueAnimator ->
				backgroundTintList = ColorStateList.valueOf(valueAnimator.animatedValue as Int)
				refreshDrawableState()
			}
			duration = COLOR_ANIMATION_DURATION
			start()
		}
	}

	open class AnimationListenerAdapter : Animation.AnimationListener {
		override fun onAnimationStart(animation: Animation) {}
		override fun onAnimationEnd(animation: Animation) {}
		override fun onAnimationRepeat(animation: Animation) {}
	}

	fun interface OnAnimationStopEndListener {
		fun onAnimationStopEnd()
	}

	private enum class State {
		PROGRESS, IDLE, ERROR, TRANSITION
	}

	enum class StopAnimationStyle {
		EXPAND, SHAKE, NULL
	}
}
