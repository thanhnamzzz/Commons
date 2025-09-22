package common.libs.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.os.SystemClock
import android.provider.Settings
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import common.libs.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import androidx.core.content.withStyledAttributes

class ProgressWheel : View {
	private val barLength = 16
	private val barMaxLength = 270
	private val pauseGrowingTime: Long = 200

	/**
	 * *********
	 * DEFAULTS *
	 * **********
	 */
	//Sizes (with defaults in DP)
	private var circleRadius = 28
	private var barWidth = 4
	private var rimWidth = 4
	private var fillRadius = false
	private var timeStartGrowing = 0.0
	private var barSpinCycleTime = 460.0
	private var barExtraLength = 0f
	private var barGrowingFromFront = true
	private var pausedTimeWithoutGrowing: Long = 0
	private var borderProgress = false

	//Colors (with defaults)
	private var barColor = -0x56000000
	private var rimColor = 0x00FFFFFF

	//Paints
	private val barPaint = Paint()
	private val rimPaint = Paint()

	//Rectangles
	private var circleBounds = RectF()

	//Animation
	//The amount of degrees per second
	private var spinSpeed = 230.0f

	//private float spinSpeed = 120.0f;
	// The last time the spinner was animated
	private var lastTimeAnimated: Long = 0

	private var linearProgress = false

	private var mProgress = 0.0f
	private var mTargetProgress = 0.0f

	/**
	 * Check if the wheel is currently spinning
	 */
	var isSpinning: Boolean = false
		private set

	private var callback: ProgressCallback? = null

	private var shouldAnimate = false

	/**
	 * The constructor for the ProgressWheel
	 */
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		context.withStyledAttributes(attrs, R.styleable.ProgressWheel) {
			val metrics = context.resources.displayMetrics
			barWidth =
				TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, barWidth.toFloat(), metrics)
					.toInt()
			rimWidth =
				TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rimWidth.toFloat(), metrics)
					.toInt()
			circleRadius =
				TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					circleRadius.toFloat(),
					metrics
				)
					.toInt()
			circleRadius =
				getDimension(R.styleable.ProgressWheel_pw_circleRadius, circleRadius.toFloat())
					.toInt()
			fillRadius = getBoolean(R.styleable.ProgressWheel_pw_fillRadius, false)
			barWidth =
				getDimension(R.styleable.ProgressWheel_pw_barWidth, barWidth.toFloat()).toInt()
			rimWidth =
				getDimension(R.styleable.ProgressWheel_pw_rimWidth, rimWidth.toFloat()).toInt()
			val baseSpinSpeed = getFloat(R.styleable.ProgressWheel_pw_spinSpeed, spinSpeed / 360.0f)
			spinSpeed = baseSpinSpeed * 360

			barSpinCycleTime =
				getInt(R.styleable.ProgressWheel_pw_barSpinCycleTime, barSpinCycleTime.toInt())
					.toDouble()
			barColor = getColor(R.styleable.ProgressWheel_pw_barColor, barColor)
			rimColor = getColor(R.styleable.ProgressWheel_pw_rimColor, rimColor)

			linearProgress = getBoolean(R.styleable.ProgressWheel_pw_linearProgress, false)
			borderProgress = getBoolean(R.styleable.ProgressWheel_pw_borderProgress, false)

			if (getBoolean(R.styleable.ProgressWheel_pw_progressIndeterminate, false)) spin()
		}

		setAnimationEnabled()
	}

	/**
	 * The constructor for the ProgressWheel
	 */
	constructor(context: Context?) : super(context) {
		setAnimationEnabled()
	}

	private fun setAnimationEnabled() {
		val animationValue = Settings.Global.getFloat(
			context.contentResolver,
			Settings.Global.ANIMATOR_DURATION_SCALE, 1f
		)

		shouldAnimate = animationValue != 0f
	}

	//----------------------------------
	//Setting up stuff
	//----------------------------------
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)

		val viewWidth = circleRadius + this.paddingLeft + this.paddingRight
		val viewHeight = circleRadius + this.paddingTop + this.paddingBottom

		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
		val widthSize = MeasureSpec.getSize(widthMeasureSpec)
		val heightMode = MeasureSpec.getMode(heightMeasureSpec)
		val heightSize = MeasureSpec.getSize(heightMeasureSpec)

		//Measure Width
		val width: Int = when (widthMode) {
			MeasureSpec.EXACTLY -> {
				//Must be this size
				widthSize
			}

			MeasureSpec.AT_MOST -> {
				//Can't be bigger than...
				min(viewWidth.toDouble(), widthSize.toDouble()).toInt()
			}

			else -> {
				//Be whatever you want
				viewWidth
			}
		}

		//Measure Height
		val height: Int =
			if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
				//Must be this size
				heightSize
			} else if (heightMode == MeasureSpec.AT_MOST) {
				//Can't be bigger than...
				min(viewHeight.toDouble(), heightSize.toDouble()).toInt()
			} else {
				//Be whatever you want
				viewHeight
			}

		setMeasuredDimension(width, height)
	}

	/**
	 * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
	 * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
	 * Use this dimensions to setup the bounds and paints.
	 */
	override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
		super.onSizeChanged(w, h, oldW, oldH)

		setupBounds()
		setupPaints()
		invalidate()
	}

	/**
	 * Set the properties of the paints we're using to
	 * draw the progress wheel
	 */
	private fun setupPaints() {
		barPaint.color = barColor
		barPaint.isAntiAlias = true
		barPaint.style = Paint.Style.STROKE
		barPaint.strokeWidth = barWidth.toFloat()
		if (borderProgress)
			barPaint.strokeCap = Paint.Cap.ROUND
		else barPaint.strokeCap = Paint.Cap.SQUARE

		rimPaint.color = rimColor
		rimPaint.isAntiAlias = true
		rimPaint.style = Paint.Style.STROKE
		rimPaint.strokeWidth = rimWidth.toFloat()
	}

	/**
	 * Set the bounds of the component
	 */
	private fun setupBounds() {
		val paddingTop = paddingTop
		val paddingBottom = paddingBottom
		val paddingLeft = paddingLeft
		val paddingRight = paddingRight

		if (!fillRadius) {
			// Width should equal to Height, find the min value to setup the circle
			val minValue = min(
				(width - paddingLeft - paddingRight).toDouble(),
				(height - paddingBottom - paddingTop).toDouble()
			).toInt()

			val circleDiameter =
				min(minValue.toDouble(), (circleRadius * 2 - barWidth * 2).toDouble()).toInt()

			// Calc the Offset if needed for centering the wheel in the available space
			val xOffset = (width - paddingLeft - paddingRight - circleDiameter) / 2 + paddingLeft
			val yOffset = (height - paddingTop - paddingBottom - circleDiameter) / 2 + paddingTop

			circleBounds =
				RectF(
					(xOffset + barWidth).toFloat(),
					(yOffset + barWidth).toFloat(),
					(xOffset + circleDiameter - barWidth).toFloat(),
					(yOffset + circleDiameter - barWidth).toFloat()
				)
		} else {
			circleBounds = RectF(
				(paddingLeft + barWidth).toFloat(),
				(paddingTop + barWidth).toFloat(),
				(width - paddingRight - barWidth).toFloat(),
				(height - paddingBottom - barWidth).toFloat()
			)
		}
	}

	fun setCallback(progressCallback: ProgressCallback?) {
		callback = progressCallback

		if (!isSpinning) runCallback()
	}

	//----------------------------------
	//Animation stuff
	//----------------------------------
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		canvas.drawArc(circleBounds, 360f, 360f, false, rimPaint)

		var mustInvalidate = false

		if (!shouldAnimate) return

		if (isSpinning) {
			//Draw the spinning bar
			mustInvalidate = true

			val deltaTime = (SystemClock.uptimeMillis() - lastTimeAnimated)
			val deltaNormalized = deltaTime * spinSpeed / 1000.0f

			updateBarLength(deltaTime)

			mProgress += deltaNormalized
			if (mProgress > 360) {
				mProgress -= 360f

				// A full turn has been completed
				// we run the callback with -1 in case we want to
				// do something, like changing the color
				runCallbackA()
			}
			lastTimeAnimated = SystemClock.uptimeMillis()

			var from = mProgress - 90
			var length = barLength + barExtraLength

			if (isInEditMode) {
				from = 0f
				length = 135f
			}

			canvas.drawArc(circleBounds, from, length, false, barPaint)
		} else {
			val oldProgress = mProgress

			if (mProgress != mTargetProgress) {
				//We smoothly increase the progress bar
				mustInvalidate = true

				val deltaTime = (SystemClock.uptimeMillis() - lastTimeAnimated).toFloat() / 1000
				val deltaNormalized = deltaTime * spinSpeed

				mProgress =
					min(
						(mProgress + deltaNormalized).toDouble(),
						mTargetProgress.toDouble()
					).toFloat()
				lastTimeAnimated = SystemClock.uptimeMillis()
			}

			if (oldProgress != mProgress) {
				runCallback()
			}

			var offset = 0.0f
			var progress = mProgress
			if (!linearProgress) {
				val factor = 2.0f
				offset = (1.0f - (1.0f - mProgress / 360.0f).pow((2.0f * factor))) * 360.0f
				progress = (1.0f - (1.0f - mProgress / 360.0f).pow(factor)) * 360.0f
			}

			if (isInEditMode) {
				progress = 360f
			}

			canvas.drawArc(circleBounds, offset - 90, progress, false, barPaint)
		}

		if (mustInvalidate) {
			invalidate()
		}
	}

	override fun onVisibilityChanged(changedView: View, visibility: Int) {
		super.onVisibilityChanged(changedView, visibility)

		if (visibility == VISIBLE) {
			lastTimeAnimated = SystemClock.uptimeMillis()
		}
	}

	private fun updateBarLength(deltaTimeInMilliSeconds: Long) {
		if (pausedTimeWithoutGrowing >= pauseGrowingTime) {
			timeStartGrowing += deltaTimeInMilliSeconds.toDouble()

			if (timeStartGrowing > barSpinCycleTime) {
				// We completed a size change cycle
				// (growing or shrinking)
				timeStartGrowing -= barSpinCycleTime
				//if(barGrowingFromFront) {
				pausedTimeWithoutGrowing = 0
				//}
				barGrowingFromFront = !barGrowingFromFront
			}

			val distance =
				cos((timeStartGrowing / barSpinCycleTime + 1) * Math.PI).toFloat() / 2 + 0.5f
			val destLength = (barMaxLength - barLength).toFloat()

			if (barGrowingFromFront) {
				barExtraLength = distance * destLength
			} else {
				val newLength = destLength * (1 - distance)
				mProgress += (barExtraLength - newLength)
				barExtraLength = newLength
			}
		} else {
			pausedTimeWithoutGrowing += deltaTimeInMilliSeconds
		}
	}

	/**
	 * Reset the count (in increment mode)
	 */
	fun resetCount() {
		mProgress = 0.0f
		mTargetProgress = 0.0f
		invalidate()
	}

	/**
	 * Turn off spin mode
	 */
	fun stopSpinning() {
		isSpinning = false
		mProgress = 0.0f
		mTargetProgress = 0.0f
		invalidate()
	}

	/**
	 * Puts the view on spin mode
	 */
	fun spin() {
		lastTimeAnimated = SystemClock.uptimeMillis()
		isSpinning = true
		invalidate()
	}

	private fun runCallbackA() {
		callback?.onProgressUpdate(-1f)
	}

	private fun runCallback() {
		callback?.let {
			val normalizedProgress = (mProgress * 100 / 360.0f).roundToInt().toFloat() / 100
			it.onProgressUpdate(normalizedProgress)
		}
	}

	/**
	 * Set the progress to a specific value,
	 * the bar will be set instantly to that value
	 *
	 * @param progress the progress between 0 and 1
	 */
	fun setInstantProgress(progress: Float) {
		var pg = progress
		if (isSpinning) {
			mProgress = 0.0f
			isSpinning = false
		}

		if (pg > 1.0f) {
			pg -= 1.0f
		} else if (pg < 0) {
			pg = 0f
		}

		if (pg == mTargetProgress) {
			return
		}

		mTargetProgress = min((pg * 360.0f).toDouble(), 360.0).toFloat()
		mProgress = mTargetProgress
		lastTimeAnimated = SystemClock.uptimeMillis()
		invalidate()
	}

	// Great way to save a view's state http://stackoverflow.com/a/7089687/1991053
	public override fun onSaveInstanceState(): Parcelable {
		val superState = super.onSaveInstanceState()

		val ss = WheelSavedState(superState)

		// We save everything that can be changed at runtime
		ss.mProgress = this.mProgress
		ss.mTargetProgress = this.mTargetProgress
		ss.isSpinning = this.isSpinning
		ss.spinSpeed = this.spinSpeed
		ss.barWidth = this.barWidth
		ss.barColor = this.barColor
		ss.rimWidth = this.rimWidth
		ss.rimColor = this.rimColor
		ss.circleRadius = this.circleRadius
		ss.linearProgress = this.linearProgress
		ss.fillRadius = this.fillRadius

		return ss
	}

	public override fun onRestoreInstanceState(state: Parcelable) {
		val wheelState = state as? WheelSavedState ?: return super.onRestoreInstanceState(state)
		super.onRestoreInstanceState(wheelState.superState)
		super.onRestoreInstanceState(wheelState.superState)
		this.mProgress = wheelState.mProgress
		this.mTargetProgress = wheelState.mTargetProgress
		this.isSpinning = wheelState.isSpinning
		this.spinSpeed = wheelState.spinSpeed
		this.barWidth = wheelState.barWidth
		this.barColor = wheelState.barColor
		this.rimWidth = wheelState.rimWidth
		this.rimColor = wheelState.rimColor
		this.circleRadius = wheelState.circleRadius
		this.linearProgress = wheelState.linearProgress
		this.fillRadius = wheelState.fillRadius

		this.lastTimeAnimated = SystemClock.uptimeMillis()
//		if (state !is WheelSavedState) {
//			super.onRestoreInstanceState(state)
//			return
//		} else {
//			super.onRestoreInstanceState(state.superState)
//			this.mProgress = state.mProgress
//			this.mTargetProgress = state.mTargetProgress
//			this.isSpinning = state.isSpinning
//			this.spinSpeed = state.spinSpeed
//			this.barWidth = state.barWidth
//			this.barColor = state.barColor
//			this.rimWidth = state.rimWidth
//			this.rimColor = state.rimColor
//			this.circleRadius = state.circleRadius
//			this.linearProgress = state.linearProgress
//			this.fillRadius = state.fillRadius
//
//			this.lastTimeAnimated = SystemClock.uptimeMillis()
//		}
	}

	var progress: Float
		/**
		 * @return the current progress between 0.0 and 1.0,
		 * if the wheel is indeterminate, then the result is -1
		 */
		get() = if (isSpinning) -1f else mProgress / 360.0f
		/**
		 * Set the progress to a specific value,
		 * the bar will smoothly animate until that value
		 *
		 * @param progress the progress between 0 and 1
		 */
		set(progress) {
			var pg = progress
			if (isSpinning) {
				mProgress = 0.0f
				isSpinning = false

				runCallback()
			}

			if (pg > 1.0f) {
				pg -= 1.0f
			} else if (pg < 0) {
				pg = 0f
			}

			if (pg == mTargetProgress) {
				return
			}

			// If we are currently in the right position
			// we set again the last time animated so the
			// animation starts smooth from here
			if (mProgress == mTargetProgress) {
				lastTimeAnimated = SystemClock.uptimeMillis()
			}

			mTargetProgress = min((progress * 360.0f).toDouble(), 360.0).toFloat()

			invalidate()
		}

	//----------------------------------
	//Getters + setters
	//----------------------------------

	/**
	 * Sets the determinate progress mode
	 *
	 * @param isLinear if the progress should increase linearly
	 */
	fun setLinearProgress(isLinear: Boolean) {
		linearProgress = isLinear
		if (!isSpinning) {
			invalidate()
		}
	}

	/**
	 * @return the radius of the wheel in pixels
	 */
	fun getCircleRadius(): Int {
		return circleRadius
	}

	/**
	 * Sets the radius of the wheel
	 *
	 * @param circleRadius the expected radius, in pixels
	 */
	fun setCircleRadius(circleRadius: Int) {
		this.circleRadius = circleRadius
		if (!isSpinning) {
			invalidate()
		}
	}

	/**
	 * @return the width of the spinning bar
	 */
	fun getBarWidth(): Int {
		return barWidth
	}

	/**
	 * Sets the width of the spinning bar
	 *
	 * @param barWidth the spinning bar width in pixels
	 */
	fun setBarWidth(barWidth: Int) {
		this.barWidth = barWidth
		if (!isSpinning) {
			invalidate()
		}
	}

	/**
	 * @return the color of the spinning bar
	 */
	fun getBarColor(): Int {
		return barColor
	}

	/**
	 * Sets the color of the spinning bar
	 *
	 * @param barColor The spinning bar color
	 */
	fun setBarColor(barColor: Int) {
		this.barColor = barColor
		setupPaints()
		if (!isSpinning) {
			invalidate()
		}
	}

	/**
	 * @return the color of the wheel's contour
	 */
	fun getRimColor(): Int {
		return rimColor
	}

	/**
	 * Sets the color of the wheel's contour
	 *
	 * @param rimColor the color for the wheel
	 */
	fun setRimColor(rimColor: Int) {
		this.rimColor = rimColor
		setupPaints()
		if (!isSpinning) {
			invalidate()
		}
	}

	/**
	 * @return the base spinning speed, in full circle turns per second
	 * (1.0 equals on full turn in one second), this value also is applied for
	 * the smoothness when setting a progress
	 */
	fun getSpinSpeed(): Float {
		return spinSpeed / 360.0f
	}

	/**
	 * Sets the base spinning speed, in full circle turns per second
	 * (1.0 equals on full turn in one second), this value also is applied for
	 * the smoothness when setting a progress
	 *
	 * @param spinSpeed the desired base speed in full turns per second
	 */
	fun setSpinSpeed(spinSpeed: Float) {
		this.spinSpeed = spinSpeed * 360.0f
	}

	/**
	 * @return the width of the wheel's contour in pixels
	 */
	fun getRimWidth(): Int {
		return rimWidth
	}

	/**
	 * Sets the width of the wheel's contour
	 *
	 * @param rimWidth the width in pixels
	 */
	fun setRimWidth(rimWidth: Int) {
		this.rimWidth = rimWidth
		if (!isSpinning) {
			invalidate()
		}
	}

	interface ProgressCallback {
		/**
		 * Method to call when the progress reaches a value
		 * in order to avoid float precision issues, the progress
		 * is rounded to a float with two decimals.
		 *
		 *
		 * In indeterminate mode, the callback is called each time
		 * the wheel completes an animation cycle, with, the progress value is -1.0f
		 *
		 * @param progress a double value between 0.00 and 1.00 both included
		 */
		fun onProgressUpdate(progress: Float)
	}

	internal class WheelSavedState : BaseSavedState {
		var mProgress: Float = 0f
		var mTargetProgress: Float = 0f
		var isSpinning: Boolean = false
		var spinSpeed: Float = 0f
		var barWidth: Int = 0
		var barColor: Int = 0
		var rimWidth: Int = 0
		var rimColor: Int = 0
		var circleRadius: Int = 0
		var linearProgress: Boolean = false
		var fillRadius: Boolean = false

		constructor(superState: Parcelable?) : super(superState)

		private constructor(`in`: Parcel) : super(`in`) {
			this.mProgress = `in`.readFloat()
			this.mTargetProgress = `in`.readFloat()
			this.isSpinning = `in`.readByte().toInt() != 0
			this.spinSpeed = `in`.readFloat()
			this.barWidth = `in`.readInt()
			this.barColor = `in`.readInt()
			this.rimWidth = `in`.readInt()
			this.rimColor = `in`.readInt()
			this.circleRadius = `in`.readInt()
			this.linearProgress = `in`.readByte().toInt() != 0
			this.fillRadius = `in`.readByte().toInt() != 0
		}

		override fun writeToParcel(out: Parcel, flags: Int) {
			super.writeToParcel(out, flags)
			out.writeFloat(this.mProgress)
			out.writeFloat(this.mTargetProgress)
			out.writeByte((if (isSpinning) 1 else 0).toByte())
			out.writeFloat(this.spinSpeed)
			out.writeInt(this.barWidth)
			out.writeInt(this.barColor)
			out.writeInt(this.rimWidth)
			out.writeInt(this.rimColor)
			out.writeInt(this.circleRadius)
			out.writeByte((if (linearProgress) 1 else 0).toByte())
			out.writeByte((if (fillRadius) 1 else 0).toByte())
		}

		companion object {
			//required field that makes Parcelables from a Parcel
			@JvmField
			val CREATOR: Parcelable.Creator<WheelSavedState?> =
				object : Parcelable.Creator<WheelSavedState?> {
					override fun createFromParcel(`in`: Parcel): WheelSavedState {
						return WheelSavedState(`in`)
					}

					override fun newArray(size: Int): Array<WheelSavedState?> {
						return arrayOfNulls(size)
					}
				}
		}
	}
}
