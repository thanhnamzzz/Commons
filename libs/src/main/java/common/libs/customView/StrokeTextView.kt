package common.libs.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Join
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import common.libs.R
import androidx.core.content.withStyledAttributes

class StrokeTextView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

	private var textColor: Int = Color.BLACK
	private var borderColor: Int = Color.WHITE
	private var borderWidth: Float = 5f
	private var borderJoin: Join = Join.MITER

	init {
		context.withStyledAttributes(attrs, R.styleable.StrokeTextView, 0, 0) {
			textColor = getColor(R.styleable.StrokeTextView_strokeTextColor, Color.BLACK)
			borderColor =
				getColor(R.styleable.StrokeTextView_strokeBorderColor, Color.WHITE)
			borderWidth = getFloat(R.styleable.StrokeTextView_strokeBorderWidth, 5f)
			borderJoin = Join.entries.toTypedArray()[getInt(
				R.styleable.StrokeTextView_strokeBorderJoin,
				1
			)]
		}
	}

	override fun onDraw(canvas: Canvas) {

		//Draw border text
		setTextColor(borderColor)
		paint.style = Paint.Style.STROKE
		paint.strokeWidth = borderWidth
		paint.strokeJoin = borderJoin
		super.onDraw(canvas)

		//Draw text
		setTextColor(textColor)
		paint.style = Paint.Style.FILL
		super.onDraw(canvas)
	}

	fun borderColor(color: Int) {
		borderColor = color
		invalidate()
	}

	fun textColor(color: Int) {
		textColor = color
		invalidate()
	}

	fun borderWidth(width: Float) {
		borderWidth = width
		invalidate()
	}

	fun borderJoin(join: Join) {
		borderJoin = join
		invalidate()
	}
}