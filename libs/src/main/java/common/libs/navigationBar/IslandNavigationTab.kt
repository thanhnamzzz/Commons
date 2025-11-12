package common.libs.navigationBar

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.FontRes

interface IslandNavigationTab {
    fun setTabTitle(title: String)
    fun setTabTitleFont(@FontRes fontRes: Int)
    fun setTabTitleSize(textSize: Float)
    fun getTabTitle(): String
    fun setTabIcon(drawable: Drawable?)
    fun setTabToggleDuration(duration: Int)
    fun setTabBackground(background: Drawable?)
    fun setTabTitleActiveColor(color: Int)
    fun setTabTitleInactiveColor(color: Int)
    fun setTabTitleColorsStateList(colorStateList: ColorStateList)
    fun setCustomInternalPadding(padding: Int)
}