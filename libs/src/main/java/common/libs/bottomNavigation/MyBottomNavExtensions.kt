package common.libs.bottomNavigation

import androidx.viewpager2.widget.ViewPager2

/**
 * Tự động đồng bộ hai chiều giữa [MyBottomNavigationView] và [ViewPager2]:
 * - Khi người dùng bấm tab trên [MyBottomNavigationView], ViewPager2 sẽ tự cuộn tới trang tương ứng.
 * - Khi người dùng vuốt đổi trang trên ViewPager2, tab tương ứng trên [MyBottomNavigationView] sẽ tự được chọn.
 */
fun MyBottomNavigationView.setupWithViewPager2(viewPager2: ViewPager2) {
    var isProgrammaticChange = false

    // 1. Khi tab thay đổi -> Cuộn ViewPager2
    setOnItemSelectedListener { position, _ ->
        if (!isProgrammaticChange) {
            viewPager2.setCurrentItem(position, true)
        }
        true
    }

    // 2. Khi ViewPager2 lướt trang -> Chọn tab
    viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (selectedPosition != position) {
                isProgrammaticChange = true
                selectItem(position, animate = true, notify = false)
                isProgrammaticChange = false
            }
        }
    })
}
