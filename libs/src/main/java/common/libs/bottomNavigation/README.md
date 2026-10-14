# MyBottomNavigationView (Android Custom View)

`MyBottomNavigationView` là thư viện Custom View viết bằng Kotlin thay thế toàn diện cho `BottomNavigationView` của Google Material. View được thiết kế dưới dạng banner nằm ngang với các tab nút có chiều cao bằng nhau tuyệt đối, hỗ trợ truyền danh sách tab linh hoạt qua code, tùy biến giao diện đa dạng và tích hợp hiệu ứng chuyển động vật lý cao cấp.

---

## 🌟 Tính năng nổi bật

- **Danh sách Tab động (Dynamic Items):** Không phụ thuộc vào tệp `menu.xml` cố định. Cho phép thêm, xóa, cập nhật số lượng nút tab linh hoạt trực tiếp từ Kotlin code qua hàm `setItems(...)`.
- **Phân tách Padding & Spacing độc lập:** Tách bạch tuyệt đối giữa khoảng đệm của View cha với viền ngoài (`barPadding`, `barPaddingHorizontal`, `barPaddingVertical`) và khoảng cách giữa các nút liền kề (`itemSpacing`).
- **Hiệu ứng chuyển động Indicator Background:**
  - `FADE`: Ẩn/hiện mượt mà tại vị trí tab mới (Chế độ mặc định).
  - `SLIDE_SPRING`: Trượt nảy vật lý lò xo (Overshoot Interpolator) kết hợp hiệu ứng đàn hồi nảy nhẹ khi về đích.
  - `SLIDE_SMOOTH`: Trượt tịnh tiến mượt mà (Decelerate Interpolator).
  - `NONE`: Chuyển vị trí ngay tức thì.
- **Hiệu ứng Squash & Stretch (Mỏng chiều cao theo độ dài di chuyển):**
  - Chiều cao viên capsule background tự động mỏng đi tỷ lệ thuận với quãng đường di chuyển (nhảy 1 tab mỏng vừa, nhảy 2-3 tab mỏng nhiều tạo cảm giác khí động học lướt nhanh).
  - Khi tiếp cận đích đến và dừng lại, background **nở dần dần ra** và trở về 100% kích thước ban đầu.
- **Tùy biến góc bo tròn (Corner Radius) riêng biệt:**
  - Hỗ trợ thiết lập góc bo tròn riêng cho lớp phủ khi được chọn (`itemCornerRadiusSelected`).
  - Hỗ trợ góc bo tròn riêng cho lớp phủ khi KHÔNG được chọn (`itemCornerRadiusUnselected`).
- **Tùy biến Background Selected & Unselected:** Hỗ trợ background riêng cho item được chọn và background (overlay) mờ nhẹ cho các item không được chọn.
- **Tùy biến Icon & Title chi tiết:** Mỗi tab hỗ trợ icon riêng (Selected/Unselected, kèm Tint màu), Font chữ (Typeface / Font Resource), kích cỡ chữ (sp) và màu chữ riêng biệt cho 2 trạng thái.
- **Đồng bộ với ViewPager2:** Tích hợp hàm mở rộng tiện lợi `setupWithViewPager2` đồng bộ hai chiều mượt mà giữa ViewPager2 và Bottom Navigation.
- **State Restoration:** Tự động lưu và khôi phục tab đang chọn khi xoay màn hình (Configuration Changes).
- **Gọn gàng & Tối ưu:** Loại bỏ toàn bộ ripple effect không mong muốn và không chứa các thành phần badge dư thừa.

---

## 🚀 Tích hợp vào dự án

### 1. Cấu hình Gradle

Đảm bảo module `:myBottomNavigation` đã được khai báo trong `settings.gradle.kts`:
```kotlin
include(":app")
include(":myBottomNavigation")
```

Trong tệp `build.gradle.kts` của module ứng dụng (`app`):
```kotlin
dependencies {
    implementation(project(":myBottomNavigation"))
}
```

---

## 📖 Hướng dẫn sử dụng

### 1. Khai báo trong Layout XML

```xml
<me.agent.mybottomnavigation.MyBottomNavigationView
    android:id="@+id/myBottomNav"
    android:layout_width="match_parent"
    android:layout_height="72dp"
    android:layout_marginHorizontal="16dp"
    android:layout_marginBottom="16dp"
    android:background="@drawable/bg_bottom_nav_rounded"
    app:barElevation="6dp"
    app:barPadding="8dp"
    app:itemSpacing="8dp"
    app:indicatorAnimationMode="slideSpring"
    app:indicatorSpringTension="1.4"
    app:indicatorThinOnMove="true"
    app:indicatorThinFactor="1.0"
    app:itemCornerRadiusSelected="100dp"
    app:itemCornerRadiusUnselected="100dp"
    app:itemIconSize="24dp"
    app:itemIconTitleSpacing="4dp"
    app:itemUnselectedBackground="#0D000000" />
```

### 2. Khởi tạo danh sách Item trong Kotlin

Mỗi tab button được đại diện bởi đối tượng `BottomBarItem`:

```kotlin
val items = listOf(
    BottomBarItem(
        id = 1,
        title = "Trang chủ",
        iconSelectedRes = R.drawable.ic_home,
        iconUnselectedRes = R.drawable.ic_home,
        iconTintSelected = ContextCompat.getColor(requireContext(), R.color.primary),
        iconTintUnselected = ContextCompat.getColor(requireContext(), R.color.text_secondary),
        textColorSelected = ContextCompat.getColor(requireContext(), R.color.primary),
        textColorUnselected = ContextCompat.getColor(requireContext(), R.color.text_secondary)
    ),
    BottomBarItem(
        id = 2,
        title = "Mua sắm",
        iconSelectedRes = R.drawable.ic_shopping_bag,
        iconUnselectedRes = R.drawable.ic_shopping_bag,
        iconTintSelected = ContextCompat.getColor(requireContext(), R.color.primary),
        iconTintUnselected = ContextCompat.getColor(requireContext(), R.color.text_secondary),
        textColorSelected = ContextCompat.getColor(requireContext(), R.color.primary),
        textColorUnselected = ContextCompat.getColor(requireContext(), R.color.text_secondary)
    ),
    BottomBarItem(
        id = 3,
        title = "Cài đặt",
        iconSelectedRes = R.drawable.ic_tune,
        iconUnselectedRes = R.drawable.ic_tune,
        iconTintSelected = ContextCompat.getColor(requireContext(), R.color.primary),
        iconTintUnselected = ContextCompat.getColor(requireContext(), R.color.text_secondary),
        textColorSelected = ContextCompat.getColor(requireContext(), R.color.primary),
        textColorUnselected = ContextCompat.getColor(requireContext(), R.color.text_secondary)
    )
)

// Gắn danh sách tab và chọn vị trí ban đầu (mặc định tab 0)
myBottomNav.setItems(items, defaultSelectedIndex = 0)
```

### 3. Lắng nghe sự kiện chọn Tab

```kotlin
// 1. Khi người dùng bấm chọn một tab
myBottomNav.setOnItemSelectedListener { position, item ->
    // Trả về true để chấp nhận đổi tab, false để từ chối
    Log.d("BottomNav", "Đang chọn Tab: $position - ${item.title}")
    true
}

// 2. Khi người dùng chạm lại chính tab đang được chọn (Reselection)
myBottomNav.setOnItemReselectedListener { position, item ->
    Log.d("BottomNav", "Chạm lại Tab hiện tại: ${item.title}")
    // Thường dùng để scroll lên đầu danh sách (Scroll to top)
}
```

### 4. Đồng bộ với ViewPager2

Chỉ cần gọi 1 dòng lệnh mở rộng:

```kotlin
myBottomNav.setupWithViewPager2(viewPager2)
```

- Bấm tab trên `MyBottomNavigationView` -> `ViewPager2` tự động cuộn trang.
- Vuốt trang trên `ViewPager2` -> `MyBottomNavigationView` tự động chuyển tab tương ứng.

---

## 🎨 Bảng tra cứu thuộc tính XML (Attributes Reference)

| Thuộc tính XML | Kiểu dữ liệu | Mặc định | Mô tả chi tiết |
| :--- | :---: | :---: | :--- |
| `app:indicatorAnimationMode` | `enum` | `fade` | Chế độ hiệu ứng: `fade`, `slideSpring`, `slideSmooth`, `none`. |
| `app:indicatorDuration` | `integer` | `300` | Thời gian chạy animation (ms). |
| `app:indicatorSpringTension` | `float` | `1.3` | Độ nảy đàn hồi (Overshoot tension) cho chế độ `slideSpring`. |
| `app:indicatorThinOnMove` | `boolean` | `true` | Bật/tắt hiệu ứng mỏng chiều cao theo độ dài di chuyển và nở ra khi dừng lại. |
| `app:indicatorThinFactor` | `float` | `1.0` | Hệ số cường độ mỏng (1.0 = chuẩn, 1.5 = mỏng sâu hơn, 0.0 = tắt). |
| `app:itemSpacing` | `dimension` | `8dp` | **Khoảng cách ĐỘC LẬP giữa các nút tab liền kề.** Không dính dáng đến padding viền ngoài. |
| `app:barPadding` | `dimension` | `0dp` | Khoảng cách đệm giữa viền ngoài View cha và toàn bộ nội dung (4 phía). |
| `app:barPaddingHorizontal` | `dimension` | `0dp` | Khoảng cách đệm trái và phải của thanh bar. |
| `app:barPaddingVertical` | `dimension` | `0dp` | Khoảng cách đệm trên và dưới của thanh bar. |
| `app:indicatorPaddingHorizontal` | `dimension` | `0dp` | Khoảng cách thu nhỏ nội bộ của indicator bên trong ô (trái/phải). |
| `app:indicatorPaddingVertical` | `dimension` | `0dp` | Khoảng cách thu nhỏ nội bộ của indicator bên trong ô (trên/dưới). |
| `app:itemSelectedBackground` | `ref \| color` | Pill tím nhạt | Background hoặc màu cho item khi được chọn. |
| `app:itemUnselectedBackground` | `ref \| color` | `null` | Background hoặc màu cho item khi KHÔNG được chọn. |
| `app:itemCornerRadius` | `dimension` | `100dp` | Góc bo tròn mặc định dùng chung cho cả khi chọn và không chọn. |
| `app:itemCornerRadiusSelected` | `dimension` | `100dp` | Góc bo tròn riêng biệt khi item được chọn. |
| `app:itemCornerRadiusUnselected` | `dimension` | `100dp` | Góc bo tròn riêng biệt khi item KHÔNG được chọn. |
| `app:itemIconSize` | `dimension` | `24dp` | Kích thước icon (vuông). |
| `app:itemIconTitleSpacing` | `dimension` | `4dp` | Khoảng cách dọc giữa Icon và Text Title. |
| `app:itemFontFamily` | `reference` | `null` | Font chữ tùy biến áp dụng chung cho tất cả các tab (VD: `@font/roboto`). |
| `app:barElevation` | `dimension` | `0dp` | Độ đổ bóng của toàn bộ thanh Bar. |

---

## 🛠 Bảng tra cứu Public Kotlin APIs

### Quản lý Tab & Trạng thái

```kotlin
// Truyền danh sách tab
fun setItems(items: List<BottomBarItem>, defaultSelectedIndex: Int = 0)

// Chọn tab từ code
fun selectItem(position: Int, animate: Boolean = true, notify: Boolean = true)

// Lấy vị trí tab đang chọn
val selectedPosition: Int

// Lấy đối tượng BottomBarItem đang chọn
val selectedItem: BottomBarItem?

// Lấy toàn bộ danh sách item hiện tại
val items: List<BottomBarItem>
```

### Quản lý Hiệu ứng Chuyển động

```kotlin
// Đổi chế độ animation: IndicatorAnimationMode.FADE, SLIDE_SPRING, SLIDE_SMOOTH, NONE
var indicatorAnimationMode: IndicatorAnimationMode

// Bật / tắt nhanh hiệu ứng trượt có vật lý
var isSlideAnimationEnabled: Boolean

// Cài đặt thông số vật lý lò xo
fun setSpringPhysics(tension: Float = 1.3f, durationMs: Long = 300L)

// Bật/tắt hiệu ứng mỏng chiều cao khi di chuyển (Squash & Stretch)
fun setThinOnMoveEnabled(enabled: Boolean)

// Cài đặt hệ số mỏng chiều cao
fun setThinFactor(factor: Float)
```

### Tuỳ biến Bo Góc (Corner Radius) & Khoảng cách (Padding/Spacing)

```kotlin
// Cài đặt bo góc chung cho cả chọn và không chọn (dp)
fun setCornerRadius(allDp: Float)

// Cài đặt bo góc riêng biệt (dp)
fun setCornerRadius(selectedDp: Float, unselectedDp: Float)
fun setCornerRadiusSelected(radiusDp: Float)
fun setCornerRadiusUnselected(radiusDp: Float)

// Cài đặt khoảng cách giữa các item liền kề (dp)
fun setItemSpacingDp(spacingDp: Float)

// Cài đặt khoảng đệm của View cha (dp)
fun setBarPaddingDp(allDp: Float)
fun setBarPaddingDp(horizontalDp: Float, verticalDp: Float)

// Cài đặt màu background
fun setItemSelectedBackgroundColor(color: Int)
fun setItemUnselectedBackgroundColor(color: Int)
fun setBarBackgroundColor(color: Int)

// Cài đặt Font chữ chung cho toàn bộ các tab
fun setItemFontFamily(@FontRes fontRes: Int)
fun setItemTypeface(typeface: Typeface?)
```

---

## 💡 Cấu trúc Model `BottomBarItem`

```kotlin
data class BottomBarItem(
    val id: Int,
    val title: String,
    
    // Icon
    val iconSelected: Drawable? = null,
    val iconUnselected: Drawable? = null,
    @DrawableRes val iconSelectedRes: Int? = null,
    @DrawableRes val iconUnselectedRes: Int? = null,
    @ColorInt val iconTintSelected: Int? = null,
    @ColorInt val iconTintUnselected: Int? = null,

    // Title & Typography
    val textSizeSp: Float = 11f,
    val typeface: Typeface? = null,
    @FontRes val fontRes: Int? = null,
    @ColorInt val textColorSelected: Int = Color.parseColor("#4338CA"),
    @ColorInt val textColorUnselected: Int = Color.parseColor("#64748B"),

    // Tuỳ biến ghi đè cho từng tab riêng lẻ (nếu cần)
    val unselectedBackground: Drawable? = null,
    @DrawableRes val unselectedBackgroundRes: Int? = null,
    val cornerRadiusSelected: Float? = null,
    val cornerRadiusUnselected: Float? = null
)
```

---

## 📝 Giấy phép (License)
Dự án được phân phối nội bộ trong cấu trúc module `SingleActivityXML`.
Mọi đóng góp và nâng cấp tính năng vui lòng tuân thủ cấu trúc đóng gói trong module `:myBottomNavigation`.
