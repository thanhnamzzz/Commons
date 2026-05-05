package common.libs.transitionButton

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Property
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

class CircularAnimatedDrawable(color: Int, private val borderWidth: Float) : Drawable(), Animatable {

    companion object {
        private val ANGLE_INTERPOLATOR: Interpolator = LinearInterpolator()
        private val SWEEP_INTERPOLATOR: Interpolator = DecelerateInterpolator()
        private const val ANGLE_ANIMATOR_DURATION = 2000
        private const val SWEEP_ANIMATOR_DURATION = 600
        const val MIN_SWEEP_ANGLE = 30
    }

    private val fBounds = RectF()
    private var mObjectAnimatorSweep: ObjectAnimator? = null
    private var mObjectAnimatorAngle: ObjectAnimator? = null
    private var mModeAppearing = false
    private val mPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
        this.color = color
    }

    private var mCurrentGlobalAngleOffset = 0f
    private var mCurrentGlobalAngle = 0f
        set(value) {
            field = value
            invalidateSelf()
        }
    private var mCurrentSweepAngle = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    private var mRunning = false

    private val mAngleProperty = object : Property<CircularAnimatedDrawable, Float>(Float::class.java, "angle") {
        override fun get(`object`: CircularAnimatedDrawable): Float {
            return `object`.mCurrentGlobalAngle
        }

        override fun set(`object`: CircularAnimatedDrawable, value: Float) {
            `object`.mCurrentGlobalAngle = value
        }
    }

    private val mSweepProperty = object : Property<CircularAnimatedDrawable, Float>(Float::class.java, "arc") {
        override fun get(`object`: CircularAnimatedDrawable): Float {
            return `object`.mCurrentSweepAngle
        }

        override fun set(`object`: CircularAnimatedDrawable, value: Float) {
            `object`.mCurrentSweepAngle = value
        }
    }

    init {
        setupAnimations()
    }

    override fun draw(canvas: Canvas) {
        var startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
        var sweepAngle = mCurrentSweepAngle
        if (!mModeAppearing) {
            startAngle += sweepAngle
            sweepAngle = 360f - sweepAngle - MIN_SWEEP_ANGLE
        } else {
            sweepAngle += MIN_SWEEP_ANGLE.toFloat()
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSPARENT", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    private fun toggleAppearingMode() {
        mModeAppearing = !mModeAppearing
        if (mModeAppearing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        fBounds.left = bounds.left + borderWidth / 2f + .5f
        fBounds.right = bounds.right - borderWidth / 2f - .5f
        fBounds.top = bounds.top + borderWidth / 2f + .5f
        fBounds.bottom = bounds.bottom - borderWidth / 2f - .5f
    }

    private fun setupAnimations() {
        mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, mAngleProperty, 360f).apply {
            interpolator = ANGLE_INTERPOLATOR
            duration = ANGLE_ANIMATOR_DURATION.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
        }

        mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, mSweepProperty, 360f - MIN_SWEEP_ANGLE * 2).apply {
            interpolator = SWEEP_INTERPOLATOR
            duration = SWEEP_ANIMATOR_DURATION.toLong()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {
                    toggleAppearingMode()
                }
            })
        }
    }

    override fun start() {
        if (isRunning) {
            return
        }
        mRunning = true
        mObjectAnimatorAngle?.start()
        mObjectAnimatorSweep?.start()
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) {
            return
        }
        mRunning = false
        mObjectAnimatorAngle?.cancel()
        mObjectAnimatorSweep?.cancel()
        invalidateSelf()
    }

    override fun isRunning(): Boolean {
        return mRunning
    }
}
