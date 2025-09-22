package common.libs.customView

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import common.libs.R

class GradientBackgroundButton @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {
	private var color1: Int = 0
	private var color2: Int = 0
	private var color3: Int = 0
	private var color4: Int = 0
	private var color5: Int = 0
	private var colorsArray: IntArray
	private var radius: Float = 0f
	private var strokeWidth: Int = 0
	private var strokeColor: Int = Color.BLACK
	private var rippleColor: Int = ContextCompat.getColor(context, R.color.ripple_button)
	private var orientationGradient: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT

	init {
		attrs?.let {
			context.withStyledAttributes(it, R.styleable.GradientBackgroundButton, 0, 0) {
				color1 = getColor(R.styleable.GradientBackgroundButton_button_color1, 0)
				color2 = getColor(R.styleable.GradientBackgroundButton_button_color2, 0)
				color3 = getColor(R.styleable.GradientBackgroundButton_button_color3, 0)
				color4 = getColor(R.styleable.GradientBackgroundButton_button_color4, 0)
				color5 = getColor(R.styleable.GradientBackgroundButton_button_color5, 0)
				radius =
					getDimension(R.styleable.GradientBackgroundButton_cornerRadius, 0f)
				strokeWidth = getDimensionPixelSize(
					R.styleable.GradientBackgroundButton_strokeWidth,
					0
				)
				strokeColor =
					getColor(R.styleable.GradientBackgroundButton_strokeColor, Color.BLACK)
				rippleColor =
					getColor(R.styleable.GradientBackgroundButton_rippleColor, rippleColor)
				val i = getInt(R.styleable.GradientBackgroundButton_orientationColor, 6)
				orientationGradient = convertOrientation(i)
			}
		}
		val ar = intArrayOf(color1, color2, color3, color4, color5)
		colorsArray = ar.filter { it != 0 }.toIntArray()
		if (colorsArray.isEmpty()) {
			colorsArray =
				intArrayOf(ContextCompat.getColor(context, R.color.constant_background_button))
		}
		setGradientBackground()
	}

	private fun setGradientBackground() {
		val gradientBackground = GradientDrawable().apply {
			shape = GradientDrawable.RECTANGLE
			colors = colorsArray
			cornerRadius = radius
			orientation = orientationGradient
			if (strokeWidth > 0) {
				setStroke(strokeWidth, strokeColor)
			}
		}
		val colorStateList = ColorStateList.valueOf(rippleColor)
		background = RippleDrawable(colorStateList, gradientBackground, null)
	}

	fun setListColorBackground(list: IntArray) {
		colorsArray = list
		setGradientBackground()
		invalidate()
	}

	fun setRadius(radius: Float) {
		this.radius = radius
		setGradientBackground()
		invalidate()
	}

	fun setStroke(strokeWidth: Int, strokeColor: Int = Color.BLACK) {
		this.strokeWidth = strokeWidth
		this.strokeColor = strokeColor
		setGradientBackground()
		invalidate()
	}

	fun setRippleColor(rippleColor: Int) {
		this.rippleColor = rippleColor
		setGradientBackground()
		invalidate()
	}

	fun setOrientation(orientation: GradientDrawable.Orientation) {
		this.orientationGradient = orientation
		setGradientBackground()
		invalidate()
	}

	private fun convertOrientation(i: Int): GradientDrawable.Orientation {
		return when (i) {
			0 -> GradientDrawable.Orientation.TOP_BOTTOM
			1 -> GradientDrawable.Orientation.TR_BL
			2 -> GradientDrawable.Orientation.RIGHT_LEFT
			3 -> GradientDrawable.Orientation.BR_TL
			4 -> GradientDrawable.Orientation.BOTTOM_TOP
			5 -> GradientDrawable.Orientation.BL_TR
			7 -> GradientDrawable.Orientation.TL_BR
			else -> GradientDrawable.Orientation.LEFT_RIGHT
		}
	}
}