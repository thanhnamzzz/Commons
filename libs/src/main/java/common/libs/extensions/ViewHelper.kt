package common.libs.extensions

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View

fun View.visibility() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.getPointLocation(): Point {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    val point = Point()
    point.x = location[0]
    point.y = location[1]
    return point
}

fun View.getBackgroundColor(): Int {
    val bg = background
    if (bg is ColorDrawable) return bg.color
    // Nếu background null hoặc không phải ColorDrawable, lấy từ theme
    val typedValue = TypedValue()
    val resolved = context.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
    return if (resolved && typedValue.type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT) {
        typedValue.data
    } else {
        Color.TRANSPARENT
    }
}