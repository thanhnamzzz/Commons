package common.libs.transitionButton

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import common.libs.extensions.isR30Plus

object WindowUtils {
    /**
     * Returns the screen height in pixels.
     * Supports modern WindowMetrics API on Android R+.
     */
    fun getHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (isR30Plus()) {
            wm.currentWindowMetrics.bounds.height()
        } else {
            val displayMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }
}
