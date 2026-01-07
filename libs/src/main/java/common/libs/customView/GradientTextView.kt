package common.libs.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import common.libs.R
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toColorInt

class GradientTextView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
	private var colors: IntArray = intArrayOf(Color.RED, Color.YELLOW, Color.BLUE)
	private var orientation: ColorOrientation = ColorOrientation.HORIZONTAL
	private var textShader: LinearGradient? = null
	private var color1: Int = 0
	private var color2: Int = 0
	private var color3: Int = 0
	private var color4: Int = 0
	private var color5: Int = 0

	init {
		attrs?.let {
			context.withStyledAttributes(it, R.styleable.GradientTextView, 0, 0) {
				color1 = getColor(R.styleable.GradientTextView_text_color1, 0)
				color2 = getColor(R.styleable.GradientTextView_text_color2, 0)
				color3 = getColor(R.styleable.GradientTextView_text_color3, 0)
				color4 = getColor(R.styleable.GradientTextView_text_color4, 0)
				color5 = getColor(R.styleable.GradientTextView_text_color5, 0)
				val ar = intArrayOf(color1, color2, color3, color4, color5)
				colors = ar.filter { a -> a != 0 }.toIntArray()
				if (colors.isEmpty()) colors = intArrayOf(Color.RED, Color.YELLOW, Color.BLUE)
				val i = getInt(R.styleable.GradientTextView_orientationColorTextView, 0)
				orientation = when (i) {
					1 -> ColorOrientation.VERTICAL
					2 -> ColorOrientation.DIAGONAL
					else -> ColorOrientation.HORIZONTAL
				}
			}
		}
		invalidateGradient()
	}

	/**For color list under IntArray*/
	fun setGradientColors(colors: IntArray) {
		this.colors = colors
		invalidateGradient()
		invalidate()
	}

	/**For color list under code #RRGGBB*/
	fun setGradientColors(colorStrings: Array<String>) {
		this.colors = colorStrings.map { it.toColorInt() }.toIntArray()
		invalidateGradient()
		invalidate()
	}

	fun setGradientOrientation(orientation: ColorOrientation) {
		this.orientation = orientation
		invalidateGradient()
		invalidate()
	}

	private var previousWidth: Float = 0f
	private fun invalidateGradient() {
		val width = paint.measureText(text.toString())
		if (width > 0 && (textShader == null || width != previousWidth)) {
			textShader = when (orientation) {
				ColorOrientation.HORIZONTAL -> LinearGradient(
					0f,
					0f,
					width,
					0f,
					colors,
					null,
					Shader.TileMode.CLAMP
				)

				ColorOrientation.VERTICAL -> LinearGradient(
					0f,
					0f,
					0f,
					textSize,
					colors,
					null,
					Shader.TileMode.CLAMP
				)

				ColorOrientation.DIAGONAL -> LinearGradient(
					0f,
					0f,
					width,
					textSize,
					colors,
					null,
					Shader.TileMode.CLAMP
				)
			}
			previousWidth = width
		}
	}

	override fun onDraw(canvas: Canvas) {
		invalidateGradient()
		paint.shader = textShader
		super.onDraw(canvas)
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		invalidateGradient()
	}

	enum class ColorOrientation {
		HORIZONTAL, VERTICAL, DIAGONAL
	}
}