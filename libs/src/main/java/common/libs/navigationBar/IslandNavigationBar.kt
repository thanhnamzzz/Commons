package common.libs.navigationBar

import android.view.animation.Interpolator
import androidx.annotation.MenuRes

interface IslandNavigationBar {
    fun inflateBarFromMenu(@MenuRes menuRes: Int)
    fun setBarSelectedTab(position: Int)
    fun setBarToggleTabsInterpolator(interpolator: Interpolator)
    fun setBarTabsToggleDuration(duration: Int)
    fun setBarTabsDistribution(chainMode: IslandNavigationBarView.ChainMode)
    fun setOnTabActionListener(listener: IslandNavigationBarView.OnTabActionListener)
    fun isHiddenTitleTab(hiddenTitleTab: Boolean)
}