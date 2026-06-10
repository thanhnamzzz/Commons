package common.libs.customView

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.isGone
import common.libs.R
import kotlin.math.max
import kotlin.math.min

class FlowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
	ViewGroup(context, attrs) {

	var isFlow: Boolean = DEFAULT_FLOW
		set(value) {
			field = value
			requestLayout()
		}

	var childSpacing: Int = DEFAULT_CHILD_SPACING
		set(value) {
			field = value
			requestLayout()
		}

	var minChildSpacing: Int = DEFAULT_CHILD_SPACING
		set(value) {
			field = value
			requestLayout()
		}

	var childSpacingForLastRow: Int = DEFAULT_CHILD_SPACING_FOR_LAST_ROW
		set(value) {
			field = value
			requestLayout()
		}

	var rowSpacing: Float = DEFAULT_ROW_SPACING
		set(value) {
			field = value
			requestLayout()
		}

	var maxRows: Int = DEFAULT_MAX_ROWS
		set(value) {
			field = value
			requestLayout()
		}

	var gravity: Int = UNSPECIFIED_GRAVITY
		set(value) {
			field = value
			requestLayout()
		}

	var rowVerticalGravity: Int = ROW_VERTICAL_GRAVITY_AUTO
		set(value) {
			field = value
			requestLayout()
		}

	var isRtl: Boolean = DEFAULT_RTL
		set(value) {
			field = value
			requestLayout()
		}

	private var adjustedRowSpacing = DEFAULT_ROW_SPACING
	private var exactMeasuredHeight = 0

	private val horizontalSpacingForRow = mutableListOf<Float>()
	private val heightForRow = mutableListOf<Int>()
	private val widthForRow = mutableListOf<Int>()
	private val childNumForRow = mutableListOf<Int>()

	init {
		context.withStyledAttributes(attrs, R.styleable.FlowLayout, 0, 0) {
			isFlow = getBoolean(R.styleable.FlowLayout_flFlow, DEFAULT_FLOW)

			val defaultSpacing = dpToPx(DEFAULT_CHILD_SPACING.toFloat()).toInt()
			childSpacing = getDimensionOrInt(R.styleable.FlowLayout_flChildSpacing, defaultSpacing)
			minChildSpacing =
				getDimensionOrInt(R.styleable.FlowLayout_flMinChildSpacing, defaultSpacing)

			childSpacingForLastRow = getDimensionOrInt(
				R.styleable.FlowLayout_flChildSpacingForLastRow,
				SPACING_UNDEFINED
			)

			rowSpacing = getDimensionOrInt(
				R.styleable.FlowLayout_flRowSpacing,
				dpToPx(DEFAULT_ROW_SPACING).toInt()
			).toFloat()

			maxRows = getInt(R.styleable.FlowLayout_flMaxRows, DEFAULT_MAX_ROWS)
			isRtl = getBoolean(R.styleable.FlowLayout_flRtl, DEFAULT_RTL)
			gravity = getInt(R.styleable.FlowLayout_android_gravity, UNSPECIFIED_GRAVITY)
			rowVerticalGravity =
				getInt(R.styleable.FlowLayout_flRowVerticalGravity, ROW_VERTICAL_GRAVITY_AUTO)
		}
	}

	private fun TypedArray.getDimensionOrInt(index: Int, defValue: Int): Int {
		val tv = TypedValue()
		if (getValue(index, tv)) {
			return if (tv.type == TypedValue.TYPE_DIMENSION) {
				getDimensionPixelSize(index, defValue)
			} else {
				getInt(index, defValue)
			}
		}
		return defValue
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val widthSize = MeasureSpec.getSize(widthMeasureSpec)
		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
		val heightSize = MeasureSpec.getSize(heightMeasureSpec)
		val heightMode = MeasureSpec.getMode(heightMeasureSpec)

		horizontalSpacingForRow.clear()
		heightForRow.clear()
		widthForRow.clear()
		childNumForRow.clear()

		var measuredHeight = 0
		var measuredWidth = 0
		var rowWidth = 0
		var maxChildHeightInRow = 0
		var childNumInRow = 0
		val rowSize = widthSize - paddingLeft - paddingRight
		var rowTotalChildWidth = 0
		val allowFlow = widthMode != MeasureSpec.UNSPECIFIED && isFlow

		val actualChildSpacing =
			if (childSpacing == SPACING_AUTO && widthMode == MeasureSpec.UNSPECIFIED) 0 else childSpacing
		val tmpSpacing =
			if (actualChildSpacing == SPACING_AUTO) minChildSpacing else actualChildSpacing

		for (i in 0 until childCount) {
			val child = getChildAt(i)
			if (child.isGone) continue

			val lp = child.layoutParams as? MarginLayoutParams
			if (lp != null) {
				measureChildWithMargins(
					child,
					widthMeasureSpec,
					0,
					heightMeasureSpec,
					measuredHeight
				)
			} else {
				measureChild(child, widthMeasureSpec, heightMeasureSpec)
			}

			val childWidth = child.measuredWidth + (lp?.let { it.leftMargin + it.rightMargin } ?: 0)
			val childHeight =
				child.measuredHeight + (lp?.let { it.topMargin + it.bottomMargin } ?: 0)

			if (allowFlow && rowWidth + childWidth > rowSize) {
				horizontalSpacingForRow.add(
					getSpacingForRow(
						actualChildSpacing,
						rowSize,
						rowTotalChildWidth,
						childNumInRow
					)
				)
				childNumForRow.add(childNumInRow)
				heightForRow.add(maxChildHeightInRow)
				widthForRow.add(rowWidth - tmpSpacing)

				if (horizontalSpacingForRow.size <= maxRows) {
					measuredHeight += maxChildHeightInRow
				}
				measuredWidth = max(measuredWidth, rowWidth)

				childNumInRow = 1
				rowWidth = childWidth + tmpSpacing
				rowTotalChildWidth = childWidth
				maxChildHeightInRow = childHeight
			} else {
				childNumInRow++
				rowWidth += childWidth + tmpSpacing
				rowTotalChildWidth += childWidth
				maxChildHeightInRow = max(maxChildHeightInRow, childHeight)
			}
		}

		// Measure remaining child views in the last row
		val lastRowSpacing = when (childSpacingForLastRow) {
			SPACING_ALIGN -> if (horizontalSpacingForRow.isNotEmpty()) horizontalSpacingForRow.last()
			else getSpacingForRow(actualChildSpacing, rowSize, rowTotalChildWidth, childNumInRow)

			SPACING_UNDEFINED -> getSpacingForRow(
				actualChildSpacing,
				rowSize,
				rowTotalChildWidth,
				childNumInRow
			)

			else -> getSpacingForRow(
				childSpacingForLastRow,
				rowSize,
				rowTotalChildWidth,
				childNumInRow
			)
		}

		horizontalSpacingForRow.add(lastRowSpacing)
		childNumForRow.add(childNumInRow)
		heightForRow.add(maxChildHeightInRow)
		widthForRow.add(rowWidth - tmpSpacing)

		if (horizontalSpacingForRow.size <= maxRows) {
			measuredHeight += maxChildHeightInRow
		}
		measuredWidth = max(measuredWidth, rowWidth)

		measuredWidth = when {
			actualChildSpacing == SPACING_AUTO -> widthSize
			widthMode == MeasureSpec.UNSPECIFIED -> measuredWidth + paddingLeft + paddingRight
			else -> min(measuredWidth + paddingLeft + paddingRight, widthSize)
		}

		measuredHeight += paddingTop + paddingBottom
		val rowNum = min(horizontalSpacingForRow.size, maxRows)
		val actualRowSpacing =
			if (rowSpacing == SPACING_AUTO.toFloat() && heightMode == MeasureSpec.UNSPECIFIED) 0f else rowSpacing

		if (actualRowSpacing == SPACING_AUTO.toFloat()) {
			adjustedRowSpacing =
				if (rowNum > 1) (heightSize - measuredHeight).toFloat() / (rowNum - 1) else 0f
			measuredHeight = heightSize
		} else {
			adjustedRowSpacing = actualRowSpacing
			if (rowNum > 1) {
				val totalSpacing = (adjustedRowSpacing * (rowNum - 1)).toInt()
				measuredHeight =
					if (heightMode == MeasureSpec.UNSPECIFIED) measuredHeight + totalSpacing
					else min(measuredHeight + totalSpacing, heightSize)
			}
		}

		exactMeasuredHeight = measuredHeight
		setMeasuredDimension(
			if (widthMode == MeasureSpec.EXACTLY) widthSize else measuredWidth,
			if (heightMode == MeasureSpec.EXACTLY) heightSize else measuredHeight
		)
	}

	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		val isLayoutRtl = layoutDirection == LAYOUT_DIRECTION_RTL || isRtl
		var x = if (isLayoutRtl) width - paddingRight else paddingLeft
		var y = paddingTop

		val vGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
		val hGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK

		when (vGravity) {
			Gravity.CENTER_VERTICAL -> y += (b - t - paddingTop - paddingBottom - exactMeasuredHeight) / 2
			Gravity.BOTTOM -> y += b - t - paddingTop - paddingBottom - exactMeasuredHeight
		}

		val layoutWidth = r - l
		val hPadding = paddingLeft + paddingRight
		x += getHorizontalGravityOffsetForRow(hGravity, layoutWidth, hPadding, 0)

		val vRowGravity = rowVerticalGravity and Gravity.VERTICAL_GRAVITY_MASK
		val rowCount = childNumForRow.size
		var childIdx = 0
		val maxVisibleRows = min(rowCount, maxRows)

		for (row in 0 until maxVisibleRows) {
			val childNum = childNumForRow[row]
			val rowHeight = heightForRow[row]
			val spacing = horizontalSpacingForRow[row]
			var i = 0

			while (i < childNum && childIdx < childCount) {
				val child = getChildAt(childIdx++)
				if (child.isGone) continue
				i++

				val lp = child.layoutParams as? MarginLayoutParams
				val mL = lp?.leftMargin ?: 0
				val mT = lp?.topMargin ?: 0
				val mR = lp?.rightMargin ?: 0
				val mB = lp?.bottomMargin ?: 0

				val cW = child.measuredWidth
				val cH = child.measuredHeight

				var childTop = y + mT
				when (vRowGravity) {
					Gravity.BOTTOM -> childTop = y + rowHeight - mB - cH
					Gravity.CENTER_VERTICAL -> childTop = y + mT + (rowHeight - mT - mB - cH) / 2
				}

				if (isLayoutRtl) {
					child.layout(x - mR - cW, childTop, x - mR, childTop + cH)
					x -= (cW + spacing + mL + mR).toInt()
				} else {
					child.layout(x + mL, childTop, x + mL + cW, childTop + cH)
					x += (cW + spacing + mL + mR).toInt()
				}
			}

			x = if (isLayoutRtl) width - paddingRight else paddingLeft
			x += getHorizontalGravityOffsetForRow(hGravity, layoutWidth, hPadding, row + 1)
			y += (rowHeight + adjustedRowSpacing).toInt()
		}

		for (i in childIdx until childCount) {
			val child = getChildAt(i)
			if (!child.isGone) child.layout(0, 0, 0, 0)
		}
	}

	private fun getHorizontalGravityOffsetForRow(
		hGravity: Int,
		parentWidth: Int,
		hPadding: Int,
		row: Int
	): Int {
		if (childSpacing == SPACING_AUTO || row !in widthForRow.indices || (childNumForRow.getOrNull(
				row
			) ?: 0) <= 0
		) return 0
		val availableWidth = parentWidth - hPadding - widthForRow[row]
		return when (hGravity) {
			Gravity.CENTER_HORIZONTAL -> availableWidth / 2
			Gravity.END -> availableWidth
			else -> 0
		}
	}

	override fun generateLayoutParams(p: LayoutParams) = MarginLayoutParams(p)
	override fun generateLayoutParams(attrs: AttributeSet) = MarginLayoutParams(context, attrs)

	private fun getSpacingForRow(
		spacingAttr: Int,
		rowSize: Int,
		usedSize: Int,
		childNum: Int
	): Float {
		return if (spacingAttr == SPACING_AUTO) {
			if (childNum > 1) (rowSize - usedSize).toFloat() / (childNum - 1) else 0f
		} else {
			spacingAttr.toFloat()
		}
	}

	private fun dpToPx(dp: Float) =
		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

	companion object {
		const val SPACING_AUTO = -65536
		const val SPACING_ALIGN = -65537
		private const val SPACING_UNDEFINED = -65538
		private const val UNSPECIFIED_GRAVITY = -1
		private const val ROW_VERTICAL_GRAVITY_AUTO = -65536
		private const val DEFAULT_FLOW = true
		private const val DEFAULT_CHILD_SPACING = 0
		private const val DEFAULT_CHILD_SPACING_FOR_LAST_ROW = SPACING_UNDEFINED
		private const val DEFAULT_ROW_SPACING = 0f
		private const val DEFAULT_RTL = false
		private const val DEFAULT_MAX_ROWS = Int.MAX_VALUE
	}
}