/* Clone from https://github.com/kartollika/Island-NavigationBar
 * from commit 74ba9ce on 08-03-2019
 *
 * Customize a few more things
 */
package common.libs.navigationBar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import common.libs.R

class IslandNavigationTabView(
    private val context: Context,
    private val attrs: AttributeSet?,
    defStyleAttr: Int,
) : LinearLayout(context, attrs, defStyleAttr),
    IslandNavigationTab {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    var tabId = 0
        set(value) {
            field = value
            id = value
        }

    var tabPosition: Int = 0
        internal set

    var isTabSelected = false
        private set

    private var tabTitle: String = ""
    private var tabIcon: Drawable? = null
    private var tabToggleDuration: Int = 0
    private var tabBackground: Drawable? = null

    private var tabTitleActiveColor: Int = DEFAULT_ACTIONS_ACTIVE_COLOR
    private var tabTitleInactiveColor: Int = DEFAULT_ACTIONS_INACTIVE_COLOR

    /* View fields */
    private lateinit var tabContainer: ViewGroup
    private lateinit var tabTitleTextView: TextView
    private lateinit var tabIconView: ImageView

    init {
        initViews()
        initContent()
    }

    internal fun setInitialSelectedStatus(initialSelected: Boolean, hiddenTitleTab: Boolean) {
        if (initialSelected) {
            isTabSelected = true
            tabIconView.isSelected = true

            if (background is TransitionDrawable) {
                val transitionDrawable = background as TransitionDrawable
                transitionDrawable.startTransition(tabToggleDuration)
            }
            tabTitleTextView.setTextColor(tabTitleActiveColor)

            if (tabTitle.isNotEmpty()) {
                tabTitleTextView.visibility = VISIBLE
            }
        } else {
            if (background !is TransitionDrawable) {
                tabBackground = null
            }
            isTabSelected = false
            tabIconView.isSelected = false
            if (hiddenTitleTab)
                tabTitleTextView.visibility = GONE
        }
    }

    internal fun selectTab() {
        tabIconView.isSelected = true
        tabTitleTextView.isSelected = true

        if (background is TransitionDrawable) {
            val transitionDrawable = background as TransitionDrawable
            transitionDrawable.startTransition(tabToggleDuration)
        } else {
            background = tabBackground
        }
        tabTitleTextView.setTextColor(tabTitleActiveColor)

        if (tabTitle.isNotEmpty()) {
            tabTitleTextView.visibility = VISIBLE
        }
    }

    internal fun deselectTab(hiddenTitleTab: Boolean) {
        tabIconView.isSelected = false
        tabTitleTextView.isSelected = false

        if (background is TransitionDrawable) {
            val transitionDrawable = background as TransitionDrawable
            transitionDrawable.reverseTransition(tabToggleDuration)
        } else {
            tabBackground = null
        }
        tabTitleTextView.setTextColor(tabTitleInactiveColor)
        if (hiddenTitleTab)
            tabTitleTextView.visibility = GONE
    }


    /* ======================================
     * IslandNavigationTabBar interface
     * ====================================== */

    override fun setTabTitle(title: String) {
        tabTitle = title
        tabTitleTextView.text = title
    }

    override fun setTabTitleFont(@FontRes fontRes: Int) {
        if (fontRes != -1)
            tabTitleTextView.typeface = ResourcesCompat.getFont(context, fontRes)
    }

    override fun setTabTitleSize(textSize: Float) {
        tabTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    }

    override fun getTabTitle(): String = tabTitle

    override fun setTabIcon(drawable: Drawable?) {
        tabIcon = drawable
        tabIconView.setImageDrawable(drawable)
    }

    override fun setTabToggleDuration(duration: Int) {
        tabToggleDuration = duration
    }

    override fun setTabTitleActiveColor(color: Int) {
        tabTitleActiveColor = color
        if (!isTabSelected) {
            tabTitleTextView.setTextColor(color)
        }
    }

    override fun setTabTitleInactiveColor(color: Int) {
        tabTitleActiveColor = color
        if (!isTabSelected) {
            tabTitleTextView.setTextColor(color)
        }
    }

    override fun setTabTitleColorsStateList(colorStateList: ColorStateList) {
        tabTitleTextView.setTextColor(colorStateList)
    }

    override fun setTabBackground(background: Drawable?) {
        tabBackground = background
        this.background = background
    }

    override fun setCustomInternalPadding(padding: Int) {
        setPadding(padding, padding, padding, padding)
    }


    /* ======================================
     * Private functions
     * ====================================== */

    private fun initContent() {
        tabId = id

        context.withStyledAttributes(attrs, R.styleable.IslandNavigationTabView, 0, 0) {

            if (hasValue(R.styleable.IslandNavigationTabView_tabTitle)) {
                setTabTitle(getString(R.styleable.IslandNavigationTabView_tabTitle)!!)
            }

            if (hasValue(R.styleable.IslandNavigationTabView_tabTitleFont)) {
                val fontRes = getResourceId(R.styleable.IslandNavigationTabView_tabTitleFont, -1)
                setTabTitleFont(fontRes)
            }

            if (hasValue(R.styleable.IslandNavigationTabView_tabTitleSize)) {
                val titleSizePx = getDimension(
                    R.styleable.IslandNavigationTabView_tabTitleSize,
                    tabTitleTextView.textSize // giá trị mặc định
                )
                setTabTitleSize(titleSizePx)
            }

            if (hasValue(R.styleable.IslandNavigationTabView_tabIcon)) {
                setTabIcon(getDrawable(R.styleable.IslandNavigationTabView_tabIcon))
            }

            if (hasValue(R.styleable.IslandNavigationTabView_tabBackground)) {
                setTabBackground(getDrawable(R.styleable.IslandNavigationTabView_tabBackground))
            }

            setTabToggleDuration(
                getInt(
                    R.styleable.IslandNavigationTabView_tabToggleDuration,
                    resources.getInteger(R.integer.default_tab_toggle_animation_duration)
                )
            )

            val defaultInternalPadding =
                resources.getDimension(R.dimen.default_tab_internal_padding)
            setCustomInternalPadding(
                getDimension(
                    R.styleable.IslandNavigationTabView_tabCustomPadding,
                    defaultInternalPadding
                ).toInt()
            )

            if (hasValue(R.styleable.IslandNavigationTabView_tabTitleActiveColor)) {
                tabTitleActiveColor = getColor(
                    R.styleable.IslandNavigationTabView_tabTitleActiveColor,
                    DEFAULT_ACTIONS_ACTIVE_COLOR
                )
            }

            if (hasValue(R.styleable.IslandNavigationTabView_tabTitleInactiveColor)) {
                tabTitleInactiveColor = getColor(
                    R.styleable.IslandNavigationTabView_tabTitleInactiveColor,
                    DEFAULT_ACTIONS_INACTIVE_COLOR
                )
            }

        }
    }

    private fun initViews() {
        val tabLayoutContainer =
            LayoutInflater.from(context).inflate(R.layout.bottombar_item, this)
        with(tabLayoutContainer) {
            tabContainer = this@IslandNavigationTabView
            tabTitleTextView = findViewById(R.id.bottombar_tab_textview)
            tabTitleTextView.setTextColor(tabTitleInactiveColor)
            tabIconView = findViewById(R.id.bottombar_tab_icon_imageview)
        }
    }

//    private fun changeDrawableTint(drawable: Drawable?, color: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            drawable?.setTint(color)
//        } else {
//            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
//        }
//    }

    companion object {
        private const val DEFAULT_ACTIONS_ACTIVE_COLOR = Color.GRAY
        private const val DEFAULT_ACTIONS_INACTIVE_COLOR = Color.GRAY
    }
}