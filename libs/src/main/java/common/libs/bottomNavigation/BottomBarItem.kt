package common.libs.bottomNavigation

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes

/**
 * Model đại diện cho một Button Item trong [MyBottomNavigationView].
 *
 * @property id ID định danh của button (tiện dùng với NavController hoặc tab index).
 * @property title Tiêu đề hiển thị dưới icon.
 * @property iconSelected Drawable icon khi item ở trạng thái được chọn.
 * @property iconUnselected Drawable icon khi item ở trạng thái chưa được chọn.
 * @property iconSelectedRes Resource ID của icon khi được chọn.
 * @property iconUnselectedRes Resource ID của icon khi chưa chọn.
 * @property iconTintSelected Màu phủ lên icon khi chọn (tùy chọn, null nếu giữ nguyên màu drawable gốc).
 * @property iconTintUnselected Màu phủ lên icon khi chưa chọn (tùy chọn, null nếu giữ nguyên màu drawable gốc).
 * @property textSizeSp Kích thước chữ (sp). Mặc định 12sp.
 * @property typeface Custom Typeface cho text (nếu có).
 * @property fontRes Custom Font Resource ID (R.font.xxx) cho text (nếu có).
 * @property textColorSelected Màu text khi item được chọn.
 * @property textColorUnselected Màu text khi item chưa được chọn.
 * @property cornerRadiusSelected Góc bo tròn lớp phủ riêng cho item này khi được chọn (px/dp, null nếu dùng cài đặt chung).
 * @property cornerRadiusUnselected Góc bo tròn lớp phủ riêng cho item này khi không chọn (px/dp, null nếu dùng cài đặt chung).
 * @property unselectedBackground Background lớp phủ riêng cho item này khi không chọn.
 * @property unselectedBackgroundRes Resource ID background lớp phủ khi không chọn.
 * @property tag Dữ liệu đính kèm tùy biến.
 */
data class BottomBarItem(
    val id: Int,
    val title: CharSequence,
    val iconSelected: Drawable? = null,
    val iconUnselected: Drawable? = null,
    @param:DrawableRes val iconSelectedRes: Int? = null,
    @param:DrawableRes val iconUnselectedRes: Int? = null,
    @param:ColorInt val iconTintSelected: Int? = null,
    @param:ColorInt val iconTintUnselected: Int? = null,
    val textSizeSp: Float = 12f,
    val typeface: Typeface? = null,
    @param:FontRes val fontRes: Int? = null,
    @param:ColorInt val textColorSelected: Int,
    @param:ColorInt val textColorUnselected: Int,
    val cornerRadiusSelected: Float? = null,
    val cornerRadiusUnselected: Float? = null,
    val unselectedBackground: Drawable? = null,
    @param:DrawableRes val unselectedBackgroundRes: Int? = null,
    val tag: Any? = null
)