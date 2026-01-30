/* Clone from https://github.com/Dimezis/BlurView */

package common.libs.blurView

import android.content.Context
import android.graphics.Canvas
import android.graphics.RenderNode
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * A FrameLayout that records a snapshot of its children on a RenderNode.
 * This snapshot is used by the BlurView to apply blur effect.
 */
class BlurTarget @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
	defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
	@JvmField
	var renderNode: RenderNode? = null

	init {
		if (canUseHardwareRendering) {
			renderNode = RenderNode("BlurViewHost node")
		}
	}

	override fun dispatchDraw(canvas: Canvas) {
		if (canUseHardwareRendering && canvas.isHardwareAccelerated) {
			renderNode!!.setPosition(0, 0, width, height)
			val recordingCanvas = renderNode!!.beginRecording()
			super.dispatchDraw(recordingCanvas)
			renderNode!!.endRecording()
			canvas.drawRenderNode(renderNode!!)
//		} else {
//			try {
//				super.dispatchDraw(canvas)
//			} catch (e: Exception) {
//				Log.e("Namzzz", "BlurTarget: dispatchDraw", e)
//				return
//			}
		}
	}

	companion object {
		// Need both RenderNode (API 29) and RenderEffect (API 31) to be available for a full hardware rendering pipeline
		@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
		@JvmField
		val canUseHardwareRendering: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
	}
}
