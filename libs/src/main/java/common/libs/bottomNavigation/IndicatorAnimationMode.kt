package common.libs.bottomNavigation

/**
 * Các chế độ hiệu ứng khi thay đổi item được chọn:
 * - [FADE]: Mặc định - Ẩn/hiện mượt mà background tại item được chọn và không chọn.
 * - [SLIDE_SPRING]: Hiệu ứng vật lý trượt ngang có độ nảy đàn hồi (Spring/Overshoot).
 * - [SLIDE_SMOOTH]: Hiệu ứng trượt ngang mượt mà tiêu chuẩn (Decelerate Interpolator).
 * - [NONE]: Đổi trạng thái tức thì không có animation.
 */
enum class IndicatorAnimationMode(val value: Int) {
    FADE(0),
    SLIDE_SPRING(1),
    SLIDE_SMOOTH(2),
    NONE(3);

    companion object {
        fun fromValue(value: Int): IndicatorAnimationMode {
            return entries.firstOrNull { it.value == value } ?: FADE
        }
    }
}
