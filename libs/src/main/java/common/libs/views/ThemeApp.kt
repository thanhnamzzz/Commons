package common.libs.views

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

fun Context.getCurrentTheme(): Int {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
}

private fun setTheme(themeMode: Int) {
    AppCompatDelegate.setDefaultNightMode(themeMode)
}

/**MODE_NIGHT_NO = 1*/
fun setThemeLightMode() {
    setTheme(AppCompatDelegate.MODE_NIGHT_NO)
}

/**MODE_NIGHT_YES = 2*/
fun setThemeNightMode() {
    setTheme(AppCompatDelegate.MODE_NIGHT_YES)
}

/**MODE_NIGHT_FOLLOW_SYSTEM = -1*/
fun setThemeFollowSystem() {
    setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}

/**MODE_NIGHT_AUTO_BATTERY = 3*/
fun setThemeAutoBattery() {
    setTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
}