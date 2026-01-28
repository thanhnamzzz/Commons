package common.libs.blurView

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import common.libs.blurView.BlurController.Companion.DEFAULT_BLUR_RADIUS
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave

class PreDrawBlurController(
	val blurView: View,
	private val rootView: ViewGroup,
	private var overlayColor: Int,
	private val blurAlgorithm: BlurAlgorithm?,
	private val scaleFactor: Float,
	private val applyNoise: Boolean
) : BlurController {

	companion object {
		@ColorInt
		const val TRANSPARENT: Int = 0
	}

	private var blurRadius: Float = DEFAULT_BLUR_RADIUS

	private var internalCanvas: BlurViewCanvas? = null
	private var internalBitmap: Bitmap? = null

	private val rootLocation = IntArray(2)
	private val blurViewLocation = IntArray(2)

	private val drawListener = ViewTreeObserver.OnPreDrawListener {
		updateBlur()
		true
	}

	private var blurEnabled = true
	private var initialized = false

	private var frameClearDrawable: Drawable? = null

	init {
		val measuredWidth = blurView.measuredWidth
		val measuredHeight = blurView.measuredHeight
		init(measuredWidth, measuredHeight)
	}

	private fun init(measuredWidth: Int, measuredHeight: Int) {
		setBlurAutoUpdate(true)
		val sizeScaler = SizeScaler(scaleFactor)
		if (sizeScaler.isZeroSized(measuredWidth, measuredHeight)) {
			blurView.setWillNotDraw(true)
			return
		}

		blurView.setWillNotDraw(false)
		val bitmapSize = sizeScaler.scale(measuredWidth, measuredHeight)
		internalBitmap = createBitmap(bitmapSize.width, bitmapSize.height, blurAlgorithm!!.supportedBitmapConfig)
		internalCanvas = BlurViewCanvas(internalBitmap!!)
		initialized = true
		updateBlur()
	}

	private fun updateBlur() {
		if (!blurEnabled || !initialized) return

		val bitmap = internalBitmap ?: return
		val canvas = internalCanvas ?: return

		if (frameClearDrawable == null) {
			bitmap.eraseColor(Color.TRANSPARENT)
		} else {
			frameClearDrawable?.draw(canvas)
		}

		canvas.withSave {
			setupInternalCanvasMatrix()
			rootView.draw(this)
		}

		blurAndSave()
	}

	/**
	 * Set up matrix to draw starting from blurView's position
	 */
	private fun setupInternalCanvasMatrix() {
		rootView.getLocationOnScreen(rootLocation)
		blurView.getLocationOnScreen(blurViewLocation)

		val left = blurViewLocation[0] - rootLocation[0]
		val top = blurViewLocation[1] - rootLocation[1]

		val bitmap = internalBitmap ?: return
		val scaleFactorH = blurView.height.toFloat() / bitmap.height
		val scaleFactorW = blurView.width.toFloat() / bitmap.width

		val scaledLeftPosition = -left / scaleFactorW
		val scaledTopPosition = -top / scaleFactorH

		internalCanvas?.translate(scaledLeftPosition, scaledTopPosition)
		internalCanvas?.scale(1 / scaleFactorW, 1 / scaleFactorH)
	}

	override fun draw(canvas: Canvas?): Boolean {
		if (!blurEnabled || !initialized) return true
		if (canvas is BlurViewCanvas) return false

		val bitmap = internalBitmap ?: return true

		val scaleFactorH = blurView.height.toFloat() / bitmap.height.toFloat()
		val scaleFactorW = blurView.width.toFloat() / bitmap.width.toFloat()

		canvas?.let { c ->
			c.save()
			c.clipRect(
				0f,
				0f,
				blurView.width.toFloat(),
				blurView.height.toFloat()
			)

			c.save()
			c.scale(scaleFactorW, scaleFactorH)
			blurAlgorithm!!.render(canvas, bitmap)
			c.restore()

			if (applyNoise) {
				Noise.apply(
					c,
					blurView.context,
					blurView.width,
					blurView.height
				)
			}

			if (overlayColor != TRANSPARENT) {
				c.drawColor(overlayColor)
			}
			c.restore()

//			c.withScale(scaleFactorW, scaleFactorH) {
//				blurAlgorithm!!.render(this, bitmap)
//			}
//
//			if (applyNoise) {
//				Noise.apply(c, blurView.context, blurView.width, blurView.height)
//			}
//			if (overlayColor != TRANSPARENT) {
//				c.drawColor(overlayColor)
//			}
		}
		return true
	}

	private fun blurAndSave() {
		val blurred = blurAlgorithm?.blur(internalBitmap!!, blurRadius)
		internalBitmap = blurred
		if (!blurAlgorithm!!.canModifyBitmap()) {
			internalCanvas?.setBitmap(blurred)
		}
	}

	override fun updateBlurViewSize() {
		val measuredWidth = blurView.measuredWidth
		val measuredHeight = blurView.measuredHeight
		init(measuredWidth, measuredHeight)
	}

	override fun destroy() {
		setBlurAutoUpdate(false)
		blurAlgorithm?.destroy()
		initialized = false
	}

	override fun setBlurRadius(radius: Float): BlurViewFacade {
		blurRadius = radius
		return this
	}

	override fun setFrameClearDrawable(frameClearDrawable: Drawable?): BlurViewFacade {
		this.frameClearDrawable = frameClearDrawable
		return this
	}

	override fun setBlurEnabled(enabled: Boolean): BlurViewFacade {
		blurEnabled = enabled
		setBlurAutoUpdate(enabled)
		blurView.invalidate()
		return this
	}

	override fun setBlurAutoUpdate(enabled: Boolean): BlurViewFacade {
		rootView.viewTreeObserver.removeOnPreDrawListener(drawListener)
		blurView.viewTreeObserver.removeOnPreDrawListener(drawListener)
		if (enabled) {
			rootView.viewTreeObserver.addOnPreDrawListener(drawListener)
			if (rootView.windowId != blurView.windowId) {
				blurView.viewTreeObserver.addOnPreDrawListener(drawListener)
			}
		}
		return this
	}

	override fun setOverlayColor(overlayColor: Int): BlurViewFacade {
		if (this.overlayColor != overlayColor) {
			this.overlayColor = overlayColor
			blurView.invalidate()
		}
		return this
	}
}