package common.libs.extensions

import android.graphics.Color

const val DARK_GREY = 0xFF333333.toInt()

fun Int.addBit(bit: Int) = this or bit
fun Int.removeBit(bit: Int) = addBit(bit) - bit

/** Tính toán độ tương phản màu */
fun Int.getContrastColor(): Int {
    val y = (299 * Color.red(this) + 587 * Color.green(this) + 114 * Color.blue(this)) / 1000
//    return if (y >= 149 && this != Color.BLACK) DARK_GREY else Color.WHITE
    return if (y >= 149) DARK_GREY else Color.WHITE
}