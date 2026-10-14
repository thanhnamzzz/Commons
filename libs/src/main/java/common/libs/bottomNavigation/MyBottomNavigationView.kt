package common.libs.bottomNavigation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import kotlin.math.sin
import androidx.core.graphics.drawable.toDrawable
import common.libs.R
import kotlin.math.abs

/**
 * Custom Bottom Navigation View thay thế hoàn toàn cho Google Material BottomNavigationView.
 *
 * Tính năng:
 * - Dạng banner nằm ngang, các button xếp ngang và chiều cao bằng nhau tuyệt đối.
 * - Danh sách button linh hoạt truyền từ code ([setItems]).
 * - Tách biệt hoàn toàn: Padding của View cha ([setPadding] / [barPaddingHorizontal]) và Khoảng cách giữa các item ([itemSpacing]).
 * - Mỗi button tùy biến icon (selected/unselected), text title (font, size, color selected/unselected).
 * - Background chính cho toàn thanh bar, background cho item được chọn và không được chọn.
 * - Hỗ trợ góc bo tròn riêng biệt cho lớp phủ khi chọn ([itemCornerRadiusSelected]) và không chọn ([itemCornerRadiusUnselected]).
 * - Hỗ trợ hiệu ứng vật lý trượt background ([IndicatorAnimationMode.SLIDE_SPRING]) hoặc ẩn hiện ([IndicatorAnimationMode.FADE] mặc định).
 * - Callback [OnItemSelectedListener], [OnItemReselectedListener].
 * - Bảo toàn trạng thái (State Restoration) khi xoay màn hình.
 */
class MyBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Interfaces callback
    fun interface OnItemSelectedListener {
        fun onItemSelected(position: Int, item: BottomBarItem): Boolean
    }

    fun interface OnItemReselectedListener {
        fun onItemReselected(position: Int, item: BottomBarItem)
    }

    private var onItemSelectedListener: OnItemSelectedListener? = null
    private var onItemReselectedListener: OnItemReselectedListener? = null

    // Danh sách items hiện tại
    private val itemList = mutableListOf<BottomBarItem>()
    private val itemViews = mutableListOf<BottomNavItemView>()

    // Vị trí đang được chọn
    var selectedPosition: Int = 0
        private set

    val selectedItem: BottomBarItem?
        get() = itemList.getOrNull(selectedPosition)

    val items: List<BottomBarItem>
        get() = itemList.toList()

    private var isViewInitialized: Boolean = false

    // Cấu hình hiệu ứng Indicator
    var indicatorAnimationMode: IndicatorAnimationMode = IndicatorAnimationMode.FADE
        set(value) {
            field = value
            if (isViewInitialized) {
                snapIndicatorToPosition(selectedPosition)
            }
        }

    /**
     * Bật / tắt nhanh hiệu ứng trượt có vật lý:
     * - true -> [IndicatorAnimationMode.SLIDE_SPRING]
     * - false -> [IndicatorAnimationMode.FADE] (mặc định ẩn/hiện)
     */
    var isSlideAnimationEnabled: Boolean
        get() = indicatorAnimationMode == IndicatorAnimationMode.SLIDE_SPRING ||
                indicatorAnimationMode == IndicatorAnimationMode.SLIDE_SMOOTH
        set(value) {
            indicatorAnimationMode = if (value) IndicatorAnimationMode.SLIDE_SPRING else IndicatorAnimationMode.FADE
        }

    var indicatorAnimDuration: Long = 300L
    var indicatorSpringTension: Float = 1.3f

    /**
     * Bật / tắt hiệu ứng làm mỏng chiều cao của background theo độ dài di chuyển,
     * và nở dần ra khi dừng lại.
     * Mặc định: true
     */
    var isIndicatorThinOnMoveEnabled: Boolean = true

    /**
     * Hệ số cường độ mỏng chiều cao khi di chuyển (mặc định 1.0f).
     * Càng lớn thì khi lướt càng mỏng nhiều, 0f là tắt.
     */
    var indicatorThinFactor: Float = 1.0f

    /**
     * Khoảng cách ĐỘC LẬP giữa các Button Item liền kề (dp/px).
     * Không ảnh hưởng tới padding mép ngoài của thanh bar!
     */
    var itemSpacing: Int = dpToPx(8f)
        set(value) {
            field = value
            if (isViewInitialized) {
                updateChildItemSpacings()
                requestLayout()
            }
        }

    /**
     * Khoảng đệm thu nhỏ nội bộ của indicator/lớp phủ bên trong ô item (dp/px, mặc định 0)
     */
    var indicatorPaddingHorizontal: Int = 0
        set(value) {
            field = value
            if (isViewInitialized) {
                itemViews.forEach { it.updateOverlayMargins(value, indicatorPaddingVertical) }
                requestLayout()
            }
        }

    var indicatorPaddingVertical: Int = 0
        set(value) {
            field = value
            if (isViewInitialized) {
                itemViews.forEach { it.updateOverlayMargins(indicatorPaddingHorizontal, value) }
                requestLayout()
            }
        }

    // Aliases cho tương thích ngược
    var indicatorMarginHorizontal: Int
        get() = indicatorPaddingHorizontal
        set(value) {
            indicatorPaddingHorizontal = value
        }

    var indicatorMarginVertical: Int
        get() = indicatorPaddingVertical
        set(value) {
            indicatorPaddingVertical = value
        }

    var itemIconSize: Int = dpToPx(24f)
        set(value) {
            field = value
            itemViews.forEach { it.iconSizePx = value }
        }

    var itemIconTitleSpacing: Int = dpToPx(4f)
        set(value) {
            field = value
            itemViews.forEach { it.iconTitleSpacingPx = value }
        }

    // Góc bo tròn lớp phủ khi được chọn
    var itemCornerRadiusSelected: Float = dpToPx(100f).toFloat()
        set(value) {
            field = value
            if (isViewInitialized) {
                applyIndicatorCornerRadius(getSelectedRadius(selectedPosition))
            }
        }

    // Góc bo tròn lớp phủ khi KHÔNG được chọn
    var itemCornerRadiusUnselected: Float = dpToPx(100f).toFloat()
        set(value) {
            field = value
            if (isViewInitialized) {
                refreshAllItemViews()
            }
        }

    // Background / Lớp phủ cho item khi được chọn
    var itemSelectedBackground: Drawable? = null
        set(value) {
            field = value
            if (isViewInitialized) {
                applyIndicatorCornerRadius(getSelectedRadius(selectedPosition))
            }
        }

    // Background / Lớp phủ cho item khi KHÔNG được chọn
    var itemUnselectedBackground: Drawable? = null
        set(value) {
            field = value
            if (isViewInitialized) {
                refreshAllItemViews()
            }
        }

    // Các thành phần UI bên trong
    private val indicatorView: View = View(context).apply {
        visibility = INVISIBLE
    }
    private val itemsContainer: LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        clipChildren = false
        clipToPadding = false
    }

    private var currentIndicatorAnimator: ValueAnimator? = null
    private var isFirstLayout: Boolean = true

    init {
        clipChildren = false
        clipToPadding = false

        // 1. Thêm Indicator View và Items Container vào layout ngay từ đầu
        val indicatorLp = LayoutParams(0, 0).apply {
            gravity = Gravity.TOP or Gravity.START
            topMargin = indicatorPaddingVertical
        }
        addView(indicatorView, indicatorLp)
        val containerLp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(itemsContainer, containerLp)

        // 2. Đọc thuộc tính XML nếu có
        val defaultRadius = dpToPx(100f).toFloat()
        var radiusGeneral: Float? = null
        var radiusSelected: Float? = null
        var radiusUnselected: Float? = null
        var customSelectedBg: Drawable? = null
        var customUnselectedBg: Drawable? = null
        var elevationVal = 0f

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyBottomNavigationView, defStyleAttr, 0)
            try {
                val modeVal = typedArray.getInt(R.styleable.MyBottomNavigationView_indicatorAnimationMode, IndicatorAnimationMode.FADE.value)
                indicatorAnimationMode = IndicatorAnimationMode.fromValue(modeVal)

                indicatorAnimDuration = typedArray.getInt(R.styleable.MyBottomNavigationView_indicatorDuration, 300).toLong()
                indicatorSpringTension = typedArray.getFloat(R.styleable.MyBottomNavigationView_indicatorSpringTension, 1.3f)

                isIndicatorThinOnMoveEnabled = typedArray.getBoolean(R.styleable.MyBottomNavigationView_indicatorThinOnMove, true)
                indicatorThinFactor = typedArray.getFloat(R.styleable.MyBottomNavigationView_indicatorThinFactor, 1.0f)

                customSelectedBg = typedArray.getDrawable(R.styleable.MyBottomNavigationView_itemSelectedBackground)
                customUnselectedBg = typedArray.getDrawable(R.styleable.MyBottomNavigationView_itemUnselectedBackground)

                if (typedArray.hasValue(R.styleable.MyBottomNavigationView_itemCornerRadius)) {
                    radiusGeneral = typedArray.getDimension(R.styleable.MyBottomNavigationView_itemCornerRadius, defaultRadius)
                }
                if (typedArray.hasValue(R.styleable.MyBottomNavigationView_itemCornerRadiusSelected)) {
                    radiusSelected = typedArray.getDimension(R.styleable.MyBottomNavigationView_itemCornerRadiusSelected, defaultRadius)
                }
                if (typedArray.hasValue(R.styleable.MyBottomNavigationView_itemCornerRadiusUnselected)) {
                    radiusUnselected = typedArray.getDimension(R.styleable.MyBottomNavigationView_itemCornerRadiusUnselected, defaultRadius)
                }

                // 2.1 Khoảng cách riêng biệt giữa các Item
                itemSpacing = typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_itemSpacing, dpToPx(8f))

                // 2.2 Padding của View cha (Parent) với nội dung bên trong
                if (typedArray.hasValue(R.styleable.MyBottomNavigationView_barPadding)) {
                    val pad = typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_barPadding, 0)
                    setPadding(pad, pad, pad, pad)
                } else {
                    val padH = typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_barPaddingHorizontal, paddingLeft)
                    val padV = typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_barPaddingVertical, paddingTop)
                    setPadding(padH, padV, padH, padV)
                }

                // 2.3 Padding nội bộ của indicator bên trong ô (mặc định 0 để indicator khớp trọn ô)
                val indPadH = when {
                    typedArray.hasValue(R.styleable.MyBottomNavigationView_indicatorPaddingHorizontal) ->
                        typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_indicatorPaddingHorizontal, 0)
                    typedArray.hasValue(R.styleable.MyBottomNavigationView_indicatorMarginHorizontal) ->
                        typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_indicatorMarginHorizontal, 0)
                    else -> 0
                }
                val indPadV = when {
                    typedArray.hasValue(R.styleable.MyBottomNavigationView_indicatorPaddingVertical) ->
                        typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_indicatorPaddingVertical, 0)
                    typedArray.hasValue(R.styleable.MyBottomNavigationView_indicatorMarginVertical) ->
                        typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_indicatorMarginVertical, 0)
                    else -> 0
                }
                indicatorPaddingHorizontal = indPadH
                indicatorPaddingVertical = indPadV

                itemIconSize = typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_itemIconSize, dpToPx(24f))
                itemIconTitleSpacing = typedArray.getDimensionPixelSize(R.styleable.MyBottomNavigationView_itemIconTitleSpacing, dpToPx(4f))

                elevationVal = typedArray.getDimension(R.styleable.MyBottomNavigationView_barElevation, 0f)
            } finally {
                typedArray.recycle()
            }
        }

        itemCornerRadiusSelected = radiusSelected ?: radiusGeneral ?: defaultRadius
        itemCornerRadiusUnselected = radiusUnselected ?: radiusGeneral ?: defaultRadius
        itemUnselectedBackground = customUnselectedBg

        // 3. Đánh dấu view đã khởi tạo thành công để cho phép các setter hoạt động
        isViewInitialized = true

        itemSelectedBackground = customSelectedBg ?: ContextCompat.getDrawable(context, R.drawable.bg_my_indicator_pill)
        applyIndicatorCornerRadius(getSelectedRadius(selectedPosition))

        // 4. Đổ bóng / Elevation nếu có
        if (elevationVal > 0f) {
            elevation = elevationVal
            outlineProvider = ViewOutlineProvider.BACKGROUND
        }

        // Mặc định gán background toàn bar nếu chưa có
        if (background == null) {
            background = ContextCompat.getDrawable(context, R.drawable.bg_my_bottom_bar_default)
        }
    }

    /**
     * Cài đặt khoảng cách giữa các item liền kề (đơn vị dp)
     */
    fun setItemSpacingDp(spacingDp: Float) {
        this.itemSpacing = dpToPx(spacingDp)
    }

    /**
     * Cài đặt khoảng cách đệm (padding) từ mép View cha tới các item bên trong (đơn vị dp)
     */
    fun setBarPaddingDp(horizontalDp: Float, verticalDp: Float) {
        val hPx = dpToPx(horizontalDp)
        val vPx = dpToPx(verticalDp)
        setPadding(hPx, vPx, hPx, vPx)
    }

    fun setBarPaddingDp(allDp: Float) {
        val pad = dpToPx(allDp)
        setPadding(pad, pad, pad, pad)
    }

    /**
     * Cài đặt góc bo tròn chung cho cả khi chọn và không chọn
     * @param allDp Kích thước bo góc tính theo dp
     */
    fun setCornerRadius(allDp: Float) {
        val px = dpToPx(allDp).toFloat()
        this.itemCornerRadiusSelected = px
        this.itemCornerRadiusUnselected = px
    }

    /**
     * Cài đặt góc bo tròn riêng biệt cho khi chọn và không chọn
     * @param selectedDp Góc bo tròn khi chọn (dp)
     * @param unselectedDp Góc bo tròn khi không chọn (dp)
     */
    fun setCornerRadius(selectedDp: Float, unselectedDp: Float) {
        this.itemCornerRadiusSelected = dpToPx(selectedDp).toFloat()
        this.itemCornerRadiusUnselected = dpToPx(unselectedDp).toFloat()
    }

    fun setCornerRadiusSelected(radiusDp: Float) {
        this.itemCornerRadiusSelected = dpToPx(radiusDp).toFloat()
    }

    fun setCornerRadiusUnselected(radiusDp: Float) {
        this.itemCornerRadiusUnselected = dpToPx(radiusDp).toFloat()
    }

    fun setItemSelectedBackgroundColor(color: Int) {
        this.itemSelectedBackground = color.toDrawable()
    }

    fun setItemUnselectedBackgroundColor(color: Int) {
	    this.itemUnselectedBackground = color.toDrawable()
    }

    /**
     * Truyền danh sách Item vào Bottom Navigation.
     * @param items Danh sách các button.
     * @param defaultSelectedIndex Vị trí tab được chọn mặc định (0..size-1).
     */
    fun setItems(items: List<BottomBarItem>, defaultSelectedIndex: Int = 0) {
        if (items.isEmpty()) return

        itemList.clear()
        itemList.addAll(items)

        itemsContainer.removeAllViews()
        itemViews.clear()

        val validSelectedIndex = defaultSelectedIndex.coerceIn(0, items.lastIndex)
        this.selectedPosition = validSelectedIndex

        // Tạo từng button nằm ngang, chia đều chiều rộng qua layout_weight = 1
        items.forEachIndexed { index, item ->
            val itemView = BottomNavItemView(context).apply {
                iconSizePx = this@MyBottomNavigationView.itemIconSize
                iconTitleSpacingPx = this@MyBottomNavigationView.itemIconTitleSpacing
                updateOverlayMargins(indicatorPaddingHorizontal, indicatorPaddingVertical)

                val isSelected = (index == validSelectedIndex)
                bindItem(
                    item = item,
                    isSelected = isSelected,
                    globalUnselectedDrawable = itemUnselectedBackground,
                    globalRadiusUnselected = itemCornerRadiusUnselected
                )

                // Không có ripple khi click
                setOnClickListener {
                    handleItemClick(index)
                }
            }

            // Áp dụng itemSpacing: CHỈ đặt marginEnd cho các phần tử đứng trước, phần tử cuối cùng marginEnd = 0
            val itemLp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f).apply {
                marginEnd = if (index < items.lastIndex) {
                    itemSpacing
                } else {
                    0
                }
            }
            itemsContainer.addView(itemView, itemLp)
            itemViews.add(itemView)
        }

        applyIndicatorCornerRadius(getSelectedRadius(validSelectedIndex))

        isFirstLayout = true
        requestLayout()
        post {
            if (itemViews.isNotEmpty()) {
                snapIndicatorToPosition(selectedPosition)
            }
        }
    }

    private fun updateChildItemSpacings() {
        itemViews.forEachIndexed { index, itemView ->
            val lp = itemView.layoutParams as? LinearLayout.LayoutParams ?: return@forEachIndexed
            val targetMarginEnd = if (index < itemViews.lastIndex) itemSpacing else 0
            if (lp.marginEnd != targetMarginEnd) {
                lp.marginEnd = targetMarginEnd
                itemView.layoutParams = lp
            }
        }
    }

    private fun refreshAllItemViews() {
        itemViews.forEachIndexed { index, itemView ->
            val item = itemList.getOrNull(index) ?: return@forEachIndexed
            itemView.bindItem(
                item = item,
                isSelected = (index == selectedPosition),
                globalUnselectedDrawable = itemUnselectedBackground,
                globalRadiusUnselected = itemCornerRadiusUnselected
            )
        }
    }

    /**
     * Xử lý khi người dùng chạm vào một tab
     */
    private fun handleItemClick(position: Int) {
        val item = itemList.getOrNull(position) ?: return

        if (position == selectedPosition) {
            onItemReselectedListener?.onItemReselected(position, item)
            return
        }

        val shouldSelect = onItemSelectedListener?.onItemSelected(position, item) ?: true
        if (shouldSelect) {
            selectItem(position, animate = true, notify = false)
        }
    }

    /**
     * Chọn tab từ code.
     * @param position Vị trí cần chọn (0..items.size-1).
     * @param animate Có chạy animation hay không.
     * @param notify Có gọi callback [onItemSelectedListener] hay không.
     */
    fun selectItem(position: Int, animate: Boolean = true, notify: Boolean = true) {
        if (position !in itemList.indices) return
        if (position == selectedPosition) return

        val prevPosition = selectedPosition
        selectedPosition = position

        val item = itemList[position]
        if (notify) {
            onItemSelectedListener?.onItemSelected(position, item)
        }

        // Cập nhật trạng thái từng Item View (icon & text color & unselected overlay)
        val hasUnselectedBg = hasUnselectedBackground()
        itemViews.getOrNull(prevPosition)?.setSelectedState(isSelected = false, animate = animate, hasUnselectedBg = hasUnselectedBg)
        itemViews.getOrNull(position)?.setSelectedState(isSelected = true, animate = animate, hasUnselectedBg = hasUnselectedBg)

        // Di chuyển Indicator Background
        moveIndicatorToPosition(prevPosition, position, animate)
    }

    private fun hasUnselectedBackground(): Boolean {
        return itemUnselectedBackground != null || itemList.any { it.unselectedBackground != null || it.unselectedBackgroundRes != null }
    }

    /**
     * Thiết lập Listener khi người dùng chọn tab
     */
    fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        this.onItemSelectedListener = listener
    }

    fun setOnItemSelectedListener(listener: (position: Int, item: BottomBarItem) -> Boolean) {
        this.onItemSelectedListener = OnItemSelectedListener { pos, item -> listener(pos, item) }
    }

    /**
     * Thiết lập Listener khi người dùng click lại chính tab đang chọn
     */
    fun setOnItemReselectedListener(listener: OnItemReselectedListener?) {
        this.onItemReselectedListener = listener
    }

    fun setOnItemReselectedListener(listener: (position: Int, item: BottomBarItem) -> Unit) {
        this.onItemReselectedListener = OnItemReselectedListener { pos, item -> listener(pos, item) }
    }

    /**
     * Cấu hình thông số vật lý cho chuyển động lò xo (Spring)
     * @param tension Độ nảy đàn hồi (khuyến nghị 1.1f -> 1.8f)
     * @param durationMs Thời gian di chuyển (ms)
     */
    fun setSpringPhysics(tension: Float = 1.3f, durationMs: Long = 300L) {
        this.indicatorSpringTension = tension
        this.indicatorAnimDuration = durationMs
    }

    /**
     * Bật / tắt hiệu ứng làm mỏng chiều cao theo quãng đường di chuyển và nở ra khi dừng lại.
     */
    fun setThinOnMoveEnabled(enabled: Boolean) {
        this.isIndicatorThinOnMoveEnabled = enabled
    }

    /**
     * Cài đặt hệ số mỏng chiều cao khi di chuyển (mặc định 1.0f).
     * @param factor Hệ số cường độ mỏng (1.0f là chuẩn, 0f là tắt, 1.5f là mỏng sâu)
     */
    fun setThinFactor(factor: Float) {
        this.indicatorThinFactor = factor.coerceAtLeast(0f)
    }

    /**
     * Cập nhật background cho toàn bộ thanh bar
     */
    fun setBarBackground(drawable: Drawable?) {
        background = drawable
    }

    fun setBarBackgroundColor(color: Int) {
        background = color.toDrawable()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (itemList.isEmpty()) return

        val count = itemList.size
        val availableWidth = (measuredWidth - paddingLeft - paddingRight).coerceAtLeast(0)
        val availableHeight = (measuredHeight - paddingTop - paddingBottom).coerceAtLeast(0)

        val totalGaps = (count - 1) * itemSpacing
        val totalItemsWidth = (availableWidth - totalGaps).coerceAtLeast(0)
        val singleItemWidth = totalItemsWidth / count

        val targetWidth = (singleItemWidth - 2 * indicatorPaddingHorizontal).coerceAtLeast(0)
        val targetHeight = (availableHeight - 2 * indicatorPaddingVertical).coerceAtLeast(0)

        val lp = indicatorView.layoutParams as? LayoutParams
        if (lp != null) {
            lp.width = targetWidth
            lp.height = targetHeight
            lp.gravity = Gravity.TOP or Gravity.START
            lp.leftMargin = 0
            lp.topMargin = indicatorPaddingVertical
        }

        indicatorView.measure(
            MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(targetHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (itemList.isEmpty()) return

        // 1. Cập nhật margins cho các item views
        itemViews.forEach {
            it.updateOverlayMargins(indicatorPaddingHorizontal, indicatorPaddingVertical)
        }

        // 2. Định vị bounds chính xác cho indicatorView theo item đang chọn
        val targetWidth = getItemWidth(selectedPosition)
        val targetHeight = getItemHeight(selectedPosition)
        val indLeft = paddingLeft
        val indTop = paddingTop + indicatorPaddingVertical
        indicatorView.layout(indLeft, indTop, indLeft + targetWidth, indTop + targetHeight)

        // 3. Định vị toạ độ ban đầu cho Indicator
        if (isFirstLayout || changed) {
            snapIndicatorToPosition(selectedPosition)
            isFirstLayout = false
        }
    }

    /**
     * Tính toán toạ độ translationX chính xác cho Indicator View:
     * Vì indicatorView.left = paddingLeft và itemsContainer.left = paddingLeft,
     * toạ độ translationX cần di chuyển CHÍNH LÀ vị trí của targetView trong itemsContainer:
     * translationX = targetView.left + indicatorPaddingHorizontal
     */
    private fun getItemStartX(position: Int): Float {
        val targetView = itemViews.getOrNull(position)
        return if (targetView != null && targetView.width > 0) {
            targetView.left.toFloat() + indicatorPaddingHorizontal
        } else {
            if (itemList.isEmpty()) return indicatorPaddingHorizontal.toFloat()
            val count = itemList.size
            val availableWidth = (width - paddingLeft - paddingRight).coerceAtLeast(0)
            val totalGaps = (count - 1) * itemSpacing
            val totalItemsWidth = (availableWidth - totalGaps).coerceAtLeast(0)
            val singleItemWidth = totalItemsWidth.toFloat() / count
            (position * (singleItemWidth + itemSpacing)) + indicatorPaddingHorizontal
        }
    }

    private fun getItemWidth(position: Int): Int {
        val targetView = itemViews.getOrNull(position)
        return if (targetView != null && targetView.width > 0) {
            (targetView.width - 2 * indicatorPaddingHorizontal).coerceAtLeast(0)
        } else {
            if (itemList.isEmpty()) return 0
            val count = itemList.size
            val availableWidth = (width - paddingLeft - paddingRight).coerceAtLeast(0)
            val totalGaps = (count - 1) * itemSpacing
            val totalItemsWidth = (availableWidth - totalGaps).coerceAtLeast(0)
            ((totalItemsWidth / count) - 2 * indicatorPaddingHorizontal).coerceAtLeast(0)
        }
    }

    private fun getItemHeight(position: Int): Int {
        val targetView = itemViews.getOrNull(position)
        return if (targetView != null && targetView.height > 0) {
            (targetView.height - 2 * indicatorPaddingVertical).coerceAtLeast(0)
        } else {
            (height - paddingTop - paddingBottom - 2 * indicatorPaddingVertical).coerceAtLeast(0)
        }
    }

    private fun getSelectedRadius(position: Int): Float {
        return itemList.getOrNull(position)?.cornerRadiusSelected ?: itemCornerRadiusSelected
    }

    private fun applyIndicatorCornerRadius(radius: Float) {
        applyCornerRadius(indicatorView, itemSelectedBackground, radius)
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
        view.invalidateOutline()
    }

    private fun updateIndicatorBounds(w: Int, h: Int) {
        val indLeft = paddingLeft
        val indTop = paddingTop + indicatorPaddingVertical
        indicatorView.layout(indLeft, indTop, indLeft + w, indTop + h)
        val lp = indicatorView.layoutParams
        if (lp != null && (lp.width != w || lp.height != h)) {
            lp.width = w
            lp.height = h
        }
    }

    private fun updateIndicatorWidth(w: Int) {
        indicatorView.layout(
            indicatorView.left,
            indicatorView.top,
            indicatorView.left + w,
            indicatorView.bottom
        )
    }

    private fun snapIndicatorToPosition(position: Int) {
        currentIndicatorAnimator?.cancel()
        val targetX = getItemStartX(position)
        val targetWidth = getItemWidth(position)
        val targetHeight = getItemHeight(position)
        updateIndicatorBounds(targetWidth, targetHeight)

        indicatorView.translationX = targetX
        indicatorView.alpha = 1f
        indicatorView.scaleX = 1f
        indicatorView.scaleY = 1f
        indicatorView.visibility = if (itemList.isNotEmpty()) VISIBLE else INVISIBLE
        applyIndicatorCornerRadius(getSelectedRadius(position))
    }

    /**
     * Di chuyển Indicator từ vị trí cũ sang vị trí mới với hiệu ứng tương ứng
     */
    private fun moveIndicatorToPosition(fromPos: Int, toPos: Int, animate: Boolean) {
        currentIndicatorAnimator?.cancel()
        indicatorView.animate().cancel()

        val targetX = getItemStartX(toPos)
        val startRadius = getSelectedRadius(fromPos)
        val targetRadius = getSelectedRadius(toPos)

        val targetWidth = getItemWidth(toPos)
        val targetHeight = getItemHeight(toPos)
        val startWidth = indicatorView.width.toFloat()

        if (!animate || indicatorAnimationMode == IndicatorAnimationMode.NONE) {
            snapIndicatorToPosition(toPos)
            return
        }

        indicatorView.visibility = VISIBLE
        val startX = indicatorView.translationX

        // Thiết lập tâm co dãn chính giữa để hiệu ứng mỏng đối xứng trên và dưới
        indicatorView.pivotX = indicatorView.width / 2f
        indicatorView.pivotY = indicatorView.height / 2f

        // Tính toán độ mỏng tối đa tỷ lệ thuận theo độ dài di chuyển (số bước tab):
        // Nhảy 1 tab: mỏng ~22% (scaleY ~ 0.78)
        // Nhảy 2 tab: mỏng ~31% (scaleY ~ 0.69)
        // Nhảy 3 tab: mỏng ~40% (scaleY ~ 0.60)
        // Nhảy 4+ tab: mỏng ~47% (scaleY ~ 0.53)
        val stepDistance = abs(toPos - fromPos).coerceAtLeast(1)
        val maxThinAmount = if (isIndicatorThinOnMoveEnabled && indicatorThinFactor > 0f) {
            ((0.22f + (stepDistance - 1) * 0.09f) * indicatorThinFactor).coerceIn(0f, 0.48f)
        } else {
            0f
        }

        when (indicatorAnimationMode) {
            IndicatorAnimationMode.FADE -> {
                // Ẩn mượt ở vị trí cũ và hiện mượt ở vị trí mới
                indicatorView.animate()
                    .alpha(0f)
                    .setDuration(100)
                    .withEndAction {
                        updateIndicatorBounds(targetWidth, targetHeight)
                        indicatorView.translationX = targetX
                        indicatorView.scaleX = 1.0f
                        indicatorView.scaleY = 1.0f
                        applyIndicatorCornerRadius(targetRadius)
                        indicatorView.animate()
                            .alpha(1f)
                            .setDuration(120)
                            .start()
                    }
                    .start()
            }

            IndicatorAnimationMode.SLIDE_SPRING -> {
                indicatorView.alpha = 1f
                // Hiệu ứng vật lý lò xo (Overshoot) kết hợp mỏng chiều cao và nở ra khi dừng
                currentIndicatorAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = indicatorAnimDuration
                    interpolator = OvershootInterpolator(indicatorSpringTension)

                    addUpdateListener { animator ->
                        val fraction = animator.animatedFraction
                        val currentVal = animator.animatedValue as Float
                        indicatorView.translationX = startX + (targetX - startX) * currentVal

                        if (startWidth != targetWidth.toFloat() && startWidth > 0f) {
                            val curW = (startWidth + (targetWidth - startWidth) * currentVal).toInt()
                            updateIndicatorWidth(curW)
                        }

                        // Sóng co dãn hình sin mượt mà: mỏng dần khi đạt đỉnh tốc độ rồi nở dần ra khi dừng lại
                        val wave = sin(fraction * Math.PI).toFloat()
                        if (maxThinAmount > 0f) {
                            val currentThin = maxThinAmount * wave
                            // 1. Mỏng chiều cao theo độ dài di chuyển
                            indicatorView.scaleY = 1.0f - currentThin

                            // 2. Nở nhẹ chiều ngang (Squash & Stretch) để bảo toàn thể tích
                            val horizontalStretch = currentThin * 0.35f
                            indicatorView.scaleX = 1.0f + horizontalStretch
                        } else {
                            val stretch = wave * 0.08f
                            indicatorView.scaleX = 1.0f + stretch
                            indicatorView.scaleY = 1.0f
                        }

                        // Chuyển đổi mượt mà góc bo tròn nếu 2 tab có radius khác nhau
                        if (startRadius != targetRadius) {
                            val currentRadius = startRadius + (targetRadius - startRadius) * currentVal
                            applyIndicatorCornerRadius(currentRadius)
                        }
                    }

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            indicatorView.translationX = targetX
                            indicatorView.scaleX = 1.0f
                            indicatorView.scaleY = 1.0f
                            updateIndicatorBounds(targetWidth, targetHeight)
                            applyIndicatorCornerRadius(targetRadius)
                        }
                    })
                    start()
                }
            }

            IndicatorAnimationMode.SLIDE_SMOOTH -> {
                indicatorView.alpha = 1f
                // Hiệu ứng trượt tịnh tiến mượt mà kết hợp mỏng chiều cao và nở ra khi dừng
                currentIndicatorAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = indicatorAnimDuration
                    interpolator = DecelerateInterpolator(1.5f)

                    addUpdateListener { animator ->
                        val fraction = animator.animatedFraction
                        val currentVal = animator.animatedValue as Float
                        indicatorView.translationX = startX + (targetX - startX) * currentVal

                        if (startWidth != targetWidth.toFloat() && startWidth > 0f) {
                            val curW = (startWidth + (targetWidth - startWidth) * currentVal).toInt()
                            updateIndicatorWidth(curW)
                        }

                        // Sóng co dãn hình sin mượt mà: mỏng dần và nở ra khi dừng lại
                        val wave = sin(fraction * Math.PI).toFloat()
                        if (maxThinAmount > 0f) {
                            val currentThin = maxThinAmount * wave
                            indicatorView.scaleY = 1.0f - currentThin
                            val horizontalStretch = currentThin * 0.35f
                            indicatorView.scaleX = 1.0f + horizontalStretch
                        } else {
                            indicatorView.scaleX = 1.0f
                            indicatorView.scaleY = 1.0f
                        }

                        if (startRadius != targetRadius) {
                            val currentRadius = startRadius + (targetRadius - startRadius) * currentVal
                            applyIndicatorCornerRadius(currentRadius)
                        }
                    }

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            indicatorView.translationX = targetX
                            indicatorView.scaleX = 1.0f
                            indicatorView.scaleY = 1.0f
                            updateIndicatorBounds(targetWidth, targetHeight)
                            applyIndicatorCornerRadius(targetRadius)
                        }
                    })
                    start()
                }
            }

            IndicatorAnimationMode.NONE -> {
                snapIndicatorToPosition(toPos)
            }
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    // =========================================================================
    // STATE RESTORATION (Bảo toàn vị trí tab khi xoay màn hình)
    // =========================================================================

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return Bundle().apply {
            putParcelable(KEY_SUPER_STATE, superState)
            putInt(KEY_SELECTED_POS, selectedPosition)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState = state.getParcelable<Parcelable>(KEY_SUPER_STATE)
            super.onRestoreInstanceState(superState)
            val savedPos = state.getInt(KEY_SELECTED_POS, 0)
            if (savedPos in itemList.indices) {
                selectItem(savedPos, animate = false, notify = false)
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        private const val KEY_SUPER_STATE = "my_nav_super_state"
        private const val KEY_SELECTED_POS = "my_nav_selected_pos"
    }
}
