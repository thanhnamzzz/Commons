/* Clone from https://github.com/kartollika/Island-NavigationBar
 * from commit 74ba9ce on 08-03-2019
 *
 * Customize a few more things
 */
package common.libs.navigationBar

import android.annotation.SuppressLint
import android.content.Context
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.AttributeSet
import android.view.MenuInflater
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.IdRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.withStyledAttributes
import common.libs.R


class IslandNavigationBarView(
    context: Context,
    private val attrs: AttributeSet?,
    defStyleAttr: Int,
) : ConstraintLayout(context, attrs, defStyleAttr),
    IslandNavigationBar {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    interface OnTabActionListener {
        fun onTabSelected(@IdRes tabId: Int)
        fun onTabReselected(@IdRes tabId: Int)
        fun onTabUnselected(@IdRes tabId: Int)
    }

    enum class ChainMode(val constraintMode: Int) {
        SPREAD(ConstraintSet.CHAIN_SPREAD),
        PACKED(ConstraintSet.CHAIN_PACKED),
        SPREAD_INSIDE(ConstraintSet.CHAIN_SPREAD_INSIDE)
    }

    var tabsCount: Int = 0
        private set

    private var navigationTabs: MutableList<IslandNavigationTabView> = mutableListOf()
    private var menuRes: Int = 0
    private var onTabSelectedListener: OnTabActionListener? = null
    private var chainMode: ChainMode = DEFAULT_BAR_DISTRIBUTION
    private lateinit var tabToggleInterpolator: Interpolator
    private var toggleDuration: Int = 0
    private var currentTab: Int = 0
    private var isHiddenTitleTab: Boolean = true /* When tab unselected */

    init {
        initContent()

        post {
            reloadTabs()
        }
    }

    private fun addTab(tab: IslandNavigationTabView) {
        addView(tab)
    }

//    private fun addTabs(vararg tabs: IslandNavigationTabView) {
//        tabs.forEach { addTab(it) }
//    }

    fun getTabPositionById(id: Int) = navigationTabs.find { it.tabId == id }?.tabPosition ?: -1

    fun getTabById(id: Int): IslandNavigationTabView = findViewById(id)

    /* ======================================
     * IslandNavigationBar interface
     * ====================================== */

    override fun inflateBarFromMenu(menuRes: Int) {
        this.menuRes = menuRes
        initMenu(menuRes)
    }

    override fun setBarSelectedTab(position: Int) {
        if (currentTab == position || position < 0 || position + 1 > navigationTabs.size) {
            return
        }
        navigationTabs[position].performClick()
    }

    override fun setBarToggleTabsInterpolator(interpolator: Interpolator) {
        this.tabToggleInterpolator = interpolator
    }

    override fun setBarTabsToggleDuration(duration: Int) {
        this.toggleDuration = duration
    }

    override fun setBarTabsDistribution(chainMode: ChainMode) {
        this.chainMode = chainMode
        updateTabItems()
    }

    override fun setOnTabActionListener(listener: OnTabActionListener) {
        onTabSelectedListener = listener
        if (navigationTabs.size > currentTab) {
            navigationTabs[currentTab].performClick()
        }
    }

    override fun isHiddenTitleTab(hiddenTitleTab: Boolean) {
        isHiddenTitleTab = hiddenTitleTab
    }

    /* ======================================
     * Private functions
     * ====================================== */

    private fun initContent() {
        context.withStyledAttributes(attrs, R.styleable.IslandNavigationBarView, 0, 0) {

            if (hasValue(R.styleable.IslandNavigationBarView_barMenu)) {
                menuRes = getResourceId(R.styleable.IslandNavigationBarView_barMenu, 0)
                initMenu(menuRes)
            }

            currentTab = getInt(R.styleable.IslandNavigationBarView_barSelectedTab, 0)

            toggleDuration = getInt(
                R.styleable.IslandNavigationBarView_barTabToggleDuration,
                resources.getInteger(R.integer.default_bar_toggle_animation_duration)
            )

            chainMode = ChainMode.entries.toTypedArray()[getInt(
                R.styleable.IslandNavigationBarView_barDistribution,
                0
            )]

            tabToggleInterpolator =
                AnimationUtils.loadInterpolator(
                    context,
                    getResourceId(
                        R.styleable.IslandNavigationBarView_barToggleInterpolator,
                        android.R.anim.accelerate_decelerate_interpolator
                    )
                )
            if (hasValue(R.styleable.IslandNavigationBarView_barHiddenTitleTab)) {
                isHiddenTitleTab =
                    getBoolean(R.styleable.IslandNavigationBarView_barHiddenTitleTab, true)
            }
        }
    }

    private fun updateTabItems() {
        createChains()
    }

    private fun createChains() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        val chainIdsList = IntArray(navigationTabs.size)
        val chainWeightList = FloatArray(navigationTabs.size)

        for (i in 0 until navigationTabs.size) {
            val id = navigationTabs[i].id
            chainIdsList[i] = id
            chainWeightList[i] = 0.0f
            constraintSet.connect(
                id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                0
            )
            constraintSet.connect(
                id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                0
            )
        }

        constraintSet.createHorizontalChain(
            id, ConstraintSet.LEFT,
            id, ConstraintSet.RIGHT,
            chainIdsList, chainWeightList,
            chainMode.constraintMode
        )
        constraintSet.applyTo(this)
    }

    @SuppressLint("RestrictedApi")
    private fun initMenu(menuRes: Int) {
        if (menuRes == 0) {
            return
        }
        val menu = MenuBuilder(context)
        MenuInflater(context).inflate(menuRes, menu)
        for (menuItem in menu.nonActionItems.also { it.addAll(menu.actionItems) }) {
            addTab(IslandNavigationTabView(context).apply {
                id = menuItem.itemId
                tabId = menuItem.itemId
                setTabTitle(menuItem.title.toString())
                setTabIcon(menuItem.icon)
            })
        }
    }

    private fun reloadTabs() {
        navigationTabs.clear()
        var index = 0

        if (childCount > 5) {
            throw IllegalArgumentException("Count of tabs of BottomBar must be at most 5")
        }

        for (i in 0 until childCount) {
            try {
                navigationTabs.add(getChildAt(i) as IslandNavigationTabView)
            } catch (_: ClassCastException) {
                throw java.lang.ClassCastException("All children of IslandNavigationBar must be type of IslandNavigationTab")
            }

            val tab = navigationTabs[i]
            tab.also {
                it.setOnClickListener { view ->
                    if (currentTab == it.tabPosition) {
                        onTabSelectedListener?.onTabReselected(navigationTabs[currentTab].tabId)
                    } else {
                        TransitionManager.beginDelayedTransition(
                            this@IslandNavigationBarView,
                            TransitionSet()
                                .addTransition(ChangeBounds().apply {
                                    interpolator = tabToggleInterpolator
                                    duration = toggleDuration.toLong()
                                })
                        )
                        onTabSelectedListener?.onTabSelected(navigationTabs[it.tabPosition].let { tab ->
                            tab.selectTab()
                            tab.tabId
                        })
                        onTabSelectedListener?.onTabUnselected(navigationTabs[currentTab].let { tab ->
                            tab.deselectTab(hiddenTitleTab = isHiddenTitleTab)
                            tab.tabId
                        })
                    }
                    currentTab = it.tabPosition
                }
                it.tabPosition = index++
                tab.setInitialSelectedStatus(currentTab == it.tabPosition, isHiddenTitleTab)
                if (currentTab == it.tabPosition) {
                    onTabSelectedListener?.onTabSelected(tab.tabId)
                }
            }
        }
        tabsCount = navigationTabs.size
        updateTabItems()
    }

    companion object {
        private val DEFAULT_BAR_DISTRIBUTION = ChainMode.SPREAD
    }
}