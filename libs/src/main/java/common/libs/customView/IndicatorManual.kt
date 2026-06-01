package common.libs.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import common.libs.R

class IndicatorManual @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
	private var indicatorCount = 0
	private var selectedIndex = 0
	private var selectedColor = 0xFF000000.toInt()
	private var unselectedColor = 0xFFCCCCCC.toInt()
	private var radius = 10f
	private var spacing = 20f

	private var selectedDrawable: Drawable? = null
	private var unselectedDrawable: Drawable? = null

	init {
		orientation = HORIZONTAL
		gravity = Gravity.CENTER

		context.theme.obtainStyledAttributes(
			attrs,
			R.styleable.IndicatorManual,
			0, 0
		).apply {
			try {
				indicatorCount = getInt(R.styleable.IndicatorManual_indicatorCount, 0)
				selectedIndex = getInt(R.styleable.IndicatorManual_indicatorSelectedIndex, 0)
				selectedColor =
					getColor(R.styleable.IndicatorManual_indicatorSelectedColor, 0xFF000000.toInt())
				unselectedColor = getColor(
					R.styleable.IndicatorManual_indicatorUnselectedColor,
					0xFFCCCCCC.toInt()
				)
				radius = getDimension(R.styleable.IndicatorManual_indicatorRadius, 10f)
				spacing = getDimension(R.styleable.IndicatorManual_indicatorSpacing, 20f)
				selectedDrawable =
					getDrawable(R.styleable.IndicatorManual_indicatorSelectedDrawable)
				unselectedDrawable =
					getDrawable(R.styleable.IndicatorManual_indicatorUnselectedDrawable)
			} finally {
				recycle()
			}
		}
		refreshIndicators()
	}

	private fun refreshIndicators() {
		removeAllViews()
		if (indicatorCount <= 0) return

		for (i in 0..indicatorCount) {
			val imageView = ImageView(context)
			val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
			if (i > 0) {
				lp.marginStart = spacing.toInt()
			}
			imageView.layoutParams = lp

			updateIndicatorState(imageView, i == selectedIndex)
			addView(imageView)
		}
	}

	private fun updateIndicatorState(imageView: ImageView, isSelected: Boolean) {
		val drawable = if (isSelected) selectedDrawable else unselectedDrawable

		if (drawable != null) {
			imageView.setImageDrawable(drawable)
			if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
				imageView.layoutParams.width = (radius * 2).toInt()
				imageView.layoutParams.height = (radius * 2).toInt()
			} else {
				imageView.layoutParams.width = LayoutParams.WRAP_CONTENT
				imageView.layoutParams.height = LayoutParams.WRAP_CONTENT
			}
		} else {
			val color = if (isSelected) selectedColor else unselectedColor
			val shape = GradientDrawable().apply {
				shape = GradientDrawable.OVAL
				setColor(color)
				setSize((radius * 2).toInt(), (radius * 2).toInt())
			}
			imageView.setImageDrawable(shape)
		}
	}

	fun setCount(count: Int) {
		if (this.indicatorCount != count) {
			this.indicatorCount = count
			refreshIndicators()
		}
	}

	fun setSelection(index: Int) {
		if (index in 0..indicatorCount && index != selectedIndex) {
			(getChildAt(selectedIndex) as? ImageView)?.let { updateIndicatorState(it, false) }
			this.selectedIndex = index
			(getChildAt(selectedIndex) as? ImageView)?.let { updateIndicatorState(it, true) }
		}
	}

	fun setSelectedDrawable(drawable: Drawable?) {
		this.selectedDrawable = drawable
		refreshIndicators()
	}

	fun setUnselectedDrawable(drawable: Drawable?) {
		this.unselectedDrawable = drawable
		refreshIndicators()
	}

	fun setSpacing(spacing: Float) {
		this.spacing = spacing
		for (i in 1 until childCount) {
			val lp = getChildAt(i).layoutParams as LayoutParams
			lp.marginStart = spacing.toInt()
			getChildAt(i).layoutParams = lp
		}
	}

	fun getCount(): Int = indicatorCount
	fun getSelection(): Int = selectedIndex
}