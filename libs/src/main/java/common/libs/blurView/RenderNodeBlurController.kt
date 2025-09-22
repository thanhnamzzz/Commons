/* Clone from https://github.com/Dimezis/BlurView
* from commit fd1c9de 25-07-2025 */

package common.libs.blurView

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import androidx.core.graphics.withScale
import common.libs.blurView.Noise.apply

@RequiresApi(api = Build.VERSION_CODES.S)
class RenderNodeBlurController(
	private val blurView: BlurView,
	private val target: BlurTarget,
	private var overlayColor: Int,
	private val scaleFactor: Float,
	private val applyNoise: Boolean
) : BlurController {
	private val targetLocation = IntArray(2)
	private val blurViewLocation = IntArray(2)

	private val blurNode = RenderNode("BlurView node")

	private var frameClearDrawable: Drawable? = null
	private var blurRadius = 1f
	private var enabled = true
	private var cachedBitmap: Bitmap? = null
	private var fallbackBlur: RenderScriptBlur? = null

	private val drawListener = ViewTreeObserver.OnPreDrawListener {
		saveOnScreenLocation()
		updateRenderNodeProperties()
		true
	}

	init {
		blurView.setWillNotDraw(false)
		blurView.viewTreeObserver.addOnPreDrawListener(drawListener)
	}

	override fun draw(canvas: Canvas?): Boolean {
		if (!enabled) return true
		saveOnScreenLocation()

		canvas?.let {
			if (it.isHardwareAccelerated) hardwarePath(it)
			else softwarePath(it)
		}
		return true
	}

	// Not doing any scaleFactor-related manipulations here, because RenderEffect blur internally
	// already scales down the snapshot depending on the blur radius.
	// https://cs.android.com/android/platform/superproject/main/+/main:external/skia/src/core/SkImageFilterTypes.cpp;drc=61197364367c9e404c7da6900658f1b16c42d0da;l=2103
	// https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/libs/hwui/jni/RenderEffect.cpp;l=39;drc=61197364367c9e404c7da6900658f1b16c42d0da?q=nativeCreateBlurEffect&ss=android%2Fplatform%2Fsuperproject%2Fmain
	private fun hardwarePath(canvas: Canvas) {

		// TODO would be good to keep it the size of the BlurView instead of the target, but then the animation
		//  like translation and rotation would go out of bounds. Not sure if there's a good fix for this
		blurNode.setPosition(0, 0, target.width, target.height)
		updateRenderNodeProperties()

		drawSnapshot()

		// Draw on the system canvas
		canvas.drawRenderNode(blurNode)
		if (applyNoise) {
			apply(canvas, blurView.context, blurView.width, blurView.height)
		}
		if (overlayColor != Color.TRANSPARENT) {
			canvas.drawColor(overlayColor)
		}
	}
	private fun updateRenderNodeProperties() {
		val layoutTranslationX = -left.toFloat()
		val layoutTranslationY = -top.toFloat()

		// Pivot point for the rotation and scale (in case it's applied)
		blurNode.pivotX = blurView.width / 2f - layoutTranslationX
		blurNode.pivotY = blurView.height / 2f - layoutTranslationY
		blurNode.translationX = layoutTranslationX
		blurNode.translationY = layoutTranslationY
	}
	private fun drawSnapshot() {
		val recordingCanvas = blurNode.beginRecording()
		if (frameClearDrawable != null) {
			frameClearDrawable!!.draw(recordingCanvas)
		}
		recordingCanvas.drawRenderNode(target.renderNode!!)
		// Looks like the order of this doesn't matter
		applyBlur()
		blurNode.endRecording()
	}

	private fun softwarePath(canvas: Canvas) {
		val sizeScaler = SizeScaler(scaleFactor)
		val original = SizeScaler.Size(blurView.width, blurView.height)
		val scaled = sizeScaler.scale(original)
		if (cachedBitmap == null || cachedBitmap!!.width != scaled.width || cachedBitmap!!.height != scaled.height) {
			cachedBitmap = createBitmap(scaled.width, scaled.height)
		}
		val softwareCanvas = Canvas(cachedBitmap!!)

		softwareCanvas.withSave {
			setupCanvasMatrix(this, original, scaled)
			if (frameClearDrawable != null) {
				frameClearDrawable!!.draw(canvas)
			}
			target.draw(this)
		}

		if (fallbackBlur == null) {
			fallbackBlur = RenderScriptBlur(blurView.context)
		}
		fallbackBlur!!.blur(cachedBitmap!!, blurRadius)
		canvas.withScale(
			original.width.toFloat() / scaled.width,
			original.height.toFloat() / scaled.height
		) {
			fallbackBlur!!.render(this, cachedBitmap!!)
		}
		if (applyNoise) {
			apply(canvas, blurView.context, blurView.width, blurView.height)
		}
		if (overlayColor != Color.TRANSPARENT) {
			canvas.drawColor(overlayColor)
		}
	}

	/**
	 * Set up matrix to draw starting from blurView's position
	 */
	private fun setupCanvasMatrix(
		canvas: Canvas,
		targetSize: SizeScaler.Size,
		scaledSize: SizeScaler.Size
	) {
		// https://github.com/Dimezis/BlurView/issues/128
		val scaleFactorH = targetSize.height.toFloat() / scaledSize.height
		val scaleFactorW = targetSize.width.toFloat() / scaledSize.width

		val scaledLeftPosition = -this.left / scaleFactorW
		val scaledTopPosition = -this.top / scaleFactorH

		canvas.translate(scaledLeftPosition, scaledTopPosition)
		canvas.scale(1 / scaleFactorW, 1 / scaleFactorH)
	}

	private val top: Int
		get() = blurViewLocation[1] - targetLocation[1]

	private val left: Int
		get() = blurViewLocation[0] - targetLocation[0]

	override fun updateBlurViewSize() {
		// No-op, the size is updated in draw method, it's cheap and not called frequently
	}

	override fun destroy() {
		blurNode.discardDisplayList()
		if (fallbackBlur != null) {
			fallbackBlur!!.destroy()
			fallbackBlur = null
		}
	}

	override fun setBlurEnabled(enabled: Boolean): BlurViewFacade? {
		this.enabled = enabled
		blurView.invalidate()
		return this
	}

	override fun setBlurAutoUpdate(enabled: Boolean): BlurViewFacade? {
		blurView.viewTreeObserver.removeOnPreDrawListener(drawListener)
		if (enabled) {
			blurView.viewTreeObserver.addOnPreDrawListener(drawListener)
		}
		return this
	}

	override fun setFrameClearDrawable(frameClearDrawable: Drawable?): BlurViewFacade {
		this.frameClearDrawable = frameClearDrawable
		return this
	}

	override fun setBlurRadius(radius: Float): BlurViewFacade? {
		this.blurRadius = radius
		applyBlur()
		return this
	}

	private fun applyBlur() {
		// scaleFactor is only used to increase the blur radius
		// because RenderEffect already scales down the snapshot when needed.
		val realBlurRadius = blurRadius * scaleFactor
		val blur =
			RenderEffect.createBlurEffect(realBlurRadius, realBlurRadius, Shader.TileMode.CLAMP)
		blurNode.setRenderEffect(blur)
	}

	override fun setOverlayColor(overlayColor: Int): BlurViewFacade? {
		if (this.overlayColor != overlayColor) {
			this.overlayColor = overlayColor
			blurView.invalidate()
		}
		return this
	}

	fun updateRotation(rotation: Float) {
		blurNode.rotationZ = -rotation
	}

	fun updateScaleX(scaleX: Float) {
		blurNode.scaleX = 1 / scaleX
	}

	fun updateScaleY(scaleY: Float) {
		blurNode.scaleY = 1 / scaleY
	}

	private fun saveOnScreenLocation() {
		target.getLocationOnScreen(targetLocation)
		blurView.getLocationOnScreen(blurViewLocation)
	}
}
