/* Clone from https://github.com/Dimezis/BlurView
* from commit fd1c9de 25-07-2025 */

package common.libs.blurView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.withStyledAttributes
import common.libs.R

/**
 * FrameLayout that blurs its underlying content.
 * Can have children and draw them over blurred background.
 */
class BlurView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
	var blurController: BlurController = NoOpController()

	@ColorInt
	private var overlayColor = 0
	private var blurAutoUpdate = true

	init {
		context.withStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0) {
			overlayColor =
				getColor(R.styleable.BlurView_blurOverlayColor, PreDrawBlurController.Companion.TRANSPARENT)
		}
	}

	override fun draw(canvas: Canvas) {
		val shouldDraw = blurController.draw(canvas)
		if (shouldDraw) {
			super.draw(canvas)
		}
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		blurController.updateBlurViewSize()
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		blurController.setBlurAutoUpdate(false)
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		if (!isHardwareAccelerated) {
			Log.e(TAG, "BlurView can't be used in not hardware-accelerated window!")
		} else {
			blurController.setBlurAutoUpdate(this.blurAutoUpdate)
		}
	}

	/**
	 * @param target      the root to start blur from.
	 * @param algorithm   sets the blur algorithm. Ignored on API >= 31 where efficient hardware rendering pipeline is used.
	 * @param scaleFactor a scale factor to downscale the view snapshot before blurring.
	 * Helps achieving stronger blur and potentially better performance at the expense of blur precision.
	 * The blur radius is essentially the radius * scaleFactor.
	 * @param applyNoise  optional blue noise texture over the blurred content to make it look more natural. True by default.
	 * @return [BlurView] to setup needed params.
	 */
	fun setupWith(
		target: BlurTarget,
		algorithm: BlurAlgorithm?,
		scaleFactor: Float,
		applyNoise: Boolean
	): BlurViewFacade {
		blurController.destroy()
		blurController = if (BlurTarget.Companion.canUseHardwareRendering) {
			// Ignores the blur algorithm, always uses RenderEffect
			RenderNodeBlurController(this, target, overlayColor, scaleFactor, applyNoise)
		} else {
			PreDrawBlurController(
				this,
				target,
				overlayColor,
				algorithm,
				scaleFactor,
				applyNoise
			)
		}

		return blurController
	}

	/**
	 * @param rootView    the root to start blur from.
	 * BlurAlgorithm is automatically picked based on the API version.
	 * It uses RenderEffect on API 31+, and RenderScriptBlur on older versions.
	 * @param scaleFactor a scale factor to downscale the view snapshot before blurring.
	 * Helps achieving stronger blur and potentially better performance at the expense of blur precision.
	 * The blur radius is essentially the radius * scaleFactor.
	 * @param applyNoise  optional blue noise texture over the blurred content to make it look more natural. True by default.
	 * @return [BlurView] to setup needed params.
	 */
	/**
	 * @param rootView root to start blur from.
	 * BlurAlgorithm is automatically picked based on the API version.
	 * It uses RenderEffect on API 31+, and RenderScriptBlur on older versions.
	 * The [DEFAULT_SCALE_FACTOR] scale factor for view snapshot is used.
	 * Blue noise texture is applied by default.
	 * @return [BlurView] to setup needed params.
	 */
	@JvmOverloads
	fun setupWith(
		rootView: BlurTarget,
		scaleFactor: Float = BlurController.Companion.DEFAULT_SCALE_FACTOR,
		applyNoise: Boolean = false
	): BlurViewFacade {
		val algorithm = if (BlurTarget.Companion.canUseHardwareRendering) {
			// Ignores the blur algorithm, always uses RenderNodeBlurController and RenderEffect
			null
		} else {
			RenderScriptBlur(context)
		}
		return setupWith(rootView, algorithm, scaleFactor, applyNoise)
	}

	// Setters duplicated to be able to conveniently change these settings outside of setupWith chain
	/**
	 * @see BlurViewFacade.setBlurRadius
	 */
	fun setBlurRadius(radius: Float): BlurViewFacade? {
		return blurController.setBlurRadius(radius)
	}

	/**
	 * @see BlurViewFacade.setOverlayColor
	 */
	fun setOverlayColor(@ColorInt overlayColor: Int): BlurViewFacade? {
		this.overlayColor = overlayColor
		return blurController.setOverlayColor(overlayColor)
	}

	/**
	 * @see BlurViewFacade.setBlurAutoUpdate
	 */
	fun setBlurAutoUpdate(enabled: Boolean): BlurViewFacade? {
		this.blurAutoUpdate = enabled
		return blurController.setBlurAutoUpdate(enabled)
	}

	/**
	 * @see BlurViewFacade.setBlurEnabled
	 */
	fun setBlurEnabled(enabled: Boolean): BlurViewFacade? {
		return blurController.setBlurEnabled(enabled)
	}

	override fun setRotation(rotation: Float) {
		super.setRotation(rotation)
		notifyRotationChanged(rotation)
	}

	@SuppressLint("NewApi")
	fun notifyRotationChanged(rotation: Float) {
		if (usingRenderNode()) {
			(blurController as RenderNodeBlurController).updateRotation(rotation)
		}
	}

	@SuppressLint("NewApi")
	fun notifyScaleXChanged(scaleX: Float) {
		if (usingRenderNode()) {
			(blurController as RenderNodeBlurController).updateScaleX(scaleX)
		}
	}

	@SuppressLint("NewApi")
	fun notifyScaleYChanged(scaleY: Float) {
		if (usingRenderNode()) {
			(blurController as RenderNodeBlurController).updateScaleY(scaleY)
		}
	}

	private fun usingRenderNode(): Boolean {
		return blurController is RenderNodeBlurController
	}

	companion object {
		private val TAG: String = BlurView::class.java.simpleName
	}
}
