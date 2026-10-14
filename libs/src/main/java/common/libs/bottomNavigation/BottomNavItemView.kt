package common.libs.bottomNavigation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Outline
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * View đại diện cho một Button đơn lẻ trong Bottom Navigation Bar.
 * Cấu trúc 2 lớp:
 * - Lớp 1: Lớp phủ unselectedOverlayView (hiển thị khi KHÔNG được chọn, có bo góc cornerRadiusUnselected).
 * - Lớp 2: Container chứa Icon + Title căn giữa dọc.
 */
class BottomNavItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val unselectedOverlayView: View
    private val contentLayout: LinearLayout
    private val ivIcon: ImageView
    private val tvTitle: TextView

    private var currentItem: BottomBarItem? = null
    private var isCurrentlySelected: Boolean = false
    private var isViewInitialized: Boolean = false

    var iconSizePx: Int = dpToPx(24f)
        set(value) {
            field = value
            if (isViewInitialized) {
                val lp = ivIcon.layoutParams as? LinearLayout.LayoutParams
                if (lp != null) {
                    lp.width = value
                    lp.height = value
                    ivIcon.layoutParams = lp
                }
            }
        }

    var iconTitleSpacingPx: Int = dpToPx(4f)
        set(value) {
            field = value
            if (isViewInitialized) {
                val lp = tvTitle.layoutParams as? LinearLayout.LayoutParams
                if (lp != null) {
                    lp.topMargin = value
                    tvTitle.layoutParams = lp
                }
            }
        }

    init {
        clipChildren = false
        clipToPadding = false

        // 1. Lớp phủ khi KHÔNG được chọn (Unselected Overlay)
        unselectedOverlayView = View(context).apply {
            visibility = INVISIBLE
            alpha = 0f
        }
        val overlayLp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER
        }
        addView(unselectedOverlayView, overlayLp)

        // 2. Container Icon + Title căn giữa
        contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            clipChildren = false
            clipToPadding = false
        }

        // Icon
        ivIcon = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val iconLp = LinearLayout.LayoutParams(iconSizePx, iconSizePx).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        contentLayout.addView(ivIcon, iconLp)

        // Text Title
        tvTitle = TextView(context).apply {
            gravity = Gravity.CENTER
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        val titleLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = iconTitleSpacingPx
        }
        contentLayout.addView(tvTitle, titleLp)

        val contentLp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(contentLayout, contentLp)

        isViewInitialized = true
    }

    /**
     * Cập nhật khoảng cách lề cho unselectedOverlayView đồng bộ với indicatorView
     */
    fun updateOverlayMargins(marginH: Int, marginV: Int) {
        val lp = unselectedOverlayView.layoutParams as? LayoutParams ?: return
        if (lp.leftMargin != marginH || lp.topMargin != marginV ||
            lp.rightMargin != marginH || lp.bottomMargin != marginV) {
            lp.setMargins(marginH, marginV, marginH, marginV)
            unselectedOverlayView.layoutParams = lp
        }
    }

    /**
     * Gắn dữ liệu model vào view
     */
    fun bindItem(
        item: BottomBarItem,
        isSelected: Boolean,
        globalUnselectedDrawable: Drawable?,
        globalRadiusUnselected: Float,
        globalTypeface: Typeface? = null,
        globalFontRes: Int? = null
    ) {
        this.currentItem = item
        this.isCurrentlySelected = isSelected

        // Cấu hình Text Title
        tvTitle.text = item.title
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.textSizeSp)

        // Cấu hình Font chữ: Ưu tiên font của từng Item -> nếu không có thì dùng font cài đặt chung
        when {
            item.typeface != null -> tvTitle.typeface = item.typeface
            item.fontRes != null -> {
                try {
                    tvTitle.typeface = ResourcesCompat.getFont(context, item.fontRes)
                } catch (_: Exception) {
                }
            }
            globalTypeface != null -> tvTitle.typeface = globalTypeface
            globalFontRes != null && globalFontRes != 0 -> {
                try {
                    tvTitle.typeface = ResourcesCompat.getFont(context, globalFontRes)
                } catch (_: Exception) {
                }
            }
        }

        // Cấu hình Unselected Overlay
        val unselectedDrawable = item.unselectedBackground
            ?: item.unselectedBackgroundRes?.let { ContextCompat.getDrawable(context, it) }
            ?: globalUnselectedDrawable

        val radiusUnselected = item.cornerRadiusUnselected ?: globalRadiusUnselected
        applyCornerRadius(unselectedOverlayView, unselectedDrawable, radiusUnselected)

        // Cập nhật trạng thái hiển thị
        applySelectedState(isSelected = isSelected, animate = false, hasUnselectedBg = unselectedDrawable != null)
    }

    /**
     * Cập nhật trạng thái Selected / Unselected
     */
    fun setSelectedState(isSelected: Boolean, animate: Boolean, hasUnselectedBg: Boolean) {
        if (this.isCurrentlySelected == isSelected && animate) return
        this.isCurrentlySelected = isSelected

        applySelectedState(isSelected = isSelected, animate = animate, hasUnselectedBg = hasUnselectedBg)
    }

    private fun applySelectedState(isSelected: Boolean, animate: Boolean, hasUnselectedBg: Boolean) {
        val item = currentItem ?: return

        // 1. Cập nhật Icon (Selected vs Unselected)
        val iconDrawable = if (isSelected) {
            item.iconSelected ?: item.iconSelectedRes?.let { ContextCompat.getDrawable(context, it) }
        } else {
            item.iconUnselected ?: item.iconUnselectedRes?.let { ContextCompat.getDrawable(context, it) }
        } ?: item.iconUnselected ?: item.iconSelected

        ivIcon.setImageDrawable(iconDrawable)

        // Tint icon nếu có cài đặt
        val tintColor = if (isSelected) item.iconTintSelected else item.iconTintUnselected
        if (tintColor != null) {
            ivIcon.imageTintList = ColorStateList.valueOf(tintColor)
        } else {
            ivIcon.imageTintList = null
        }

        // 2. Cập nhật Text Color
        val targetColor = if (isSelected) item.textColorSelected else item.textColorUnselected
        if (animate) {
            val currentColor = tvTitle.currentTextColor
            val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), currentColor, targetColor).apply {
                duration = 200
                addUpdateListener { animator ->
                    tvTitle.setTextColor(animator.animatedValue as Int)
                }
            }
            colorAnimator.start()
        } else {
            tvTitle.setTextColor(targetColor)
        }

        // 3. Hiệu ứng Scale nhẹ icon khi được chọn
        if (animate && isSelected) {
            ivIcon.scaleX = 0.9f
            ivIcon.scaleY = 0.9f
            ivIcon.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(150)
                .setInterpolator(OvershootInterpolator(2.0f))
                .withEndAction {
                    ivIcon.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                }
                .start()
        } else if (!isSelected) {
            ivIcon.scaleX = 1.0f
            ivIcon.scaleY = 1.0f
        }

        // 4. Cập nhật lớp phủ khi không được chọn (Unselected Overlay)
        if (hasUnselectedBg) {
            unselectedOverlayView.visibility = VISIBLE
            val targetAlpha = if (isSelected) 0f else 1f
            if (animate) {
                unselectedOverlayView.animate()
                    .alpha(targetAlpha)
                    .setDuration(200)
                    .start()
            } else {
                unselectedOverlayView.alpha = targetAlpha
            }
        } else {
            unselectedOverlayView.visibility = INVISIBLE
            unselectedOverlayView.alpha = 0f
        }
    }

    private fun applyCornerRadius(view: View, drawable: Drawable?, radius: Float) {
        if (drawable == null) {
            view.background = null
            view.outlineProvider = null
            view.clipToOutline = false
            return
        }

        val finalDrawable = drawable.constantState?.newDrawable()?.mutate() ?: drawable
        when (finalDrawable) {
            is GradientDrawable -> {
                finalDrawable.cornerRadius = radius
                view.background = finalDrawable
            }

            is ColorDrawable -> {
                val gd = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(finalDrawable.color)
                    cornerRadius = radius
                }
                view.background = gd
            }

            else -> {
                view.background = finalDrawable
            }
        }

        if (radius > 0f) {
            view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(v: View, outline: Outline) {
                    outline.setRoundRect(0, 0, v.width, v.height, radius)
                }
            }
            view.clipToOutline = true
        } else {
            view.outlineProvider = null
            view.clipToOutline = false
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}