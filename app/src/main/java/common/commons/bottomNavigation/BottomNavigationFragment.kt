package common.commons.bottomNavigation

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import common.commons.R
import common.commons.databinding.FragmentBottomNavigationBinding
import common.libs.SimpleFragment
import common.libs.bottomNavigation.BottomBarItem
import common.libs.bottomNavigation.IndicatorAnimationMode
import common.libs.bottomNavigation.setupWithViewPager2
import common.libs.views.TypeToast
import common.libs.views.toastMess

class BottomNavigationFragment : SimpleFragment<FragmentBottomNavigationBinding>(
	FragmentBottomNavigationBinding::inflate
) {
	private var is4Items = true
	private var radiusState = 0
	private var thinState = 0
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initBottomNav()
		initControlButtons()
	}

	private fun initBottomNav() {
		val items = createBottomNavItems(is4Items = true)
		fBinding.llButtonAnimation.check(R.id.btnModeSpring)

		// 1. Cung cấp danh sách item qua code
		fBinding.myBottomNav.setItems(items, defaultSelectedIndex = 0)

		// 2. Thiết lập ViewPager2 với Adapter mẫu
		setupViewPager(items)

		// 3. Tự động đồng bộ ViewPager2 và Bottom Navigation
		fBinding.myBottomNav.setupWithViewPager2(fBinding.viewPager2)

		// 4. Lắng nghe sự kiện chọn Tab
		fBinding.myBottomNav.setOnItemSelectedListener { position, item ->
			fBinding.tvCurrentSelection.text = "Đang chọn: Tab $position (${item.title})"
			fBinding.viewPager2.setCurrentItem(position, true)
			true
		}

		// 5. Lắng nghe sự kiện click lại Tab (Reselection)
		fBinding.myBottomNav.setOnItemReselectedListener { position, item ->
			fBinding.viewPager2.setCurrentItem(position, true)
//            requireActivity().toastMess("Đã chạm lại Tab: ${item.title}", TypeToast.NONE)
		}
	}

	private fun createBottomNavItems(is4Items: Boolean): List<BottomBarItem> {
		val colorSelected = ContextCompat.getColor(requireContext(), R.color.primary)
		val colorUnselected = ContextCompat.getColor(requireContext(), R.color.text_secondary)

		val list = mutableListOf(
			BottomBarItem(
				id = 1,
				title = "Trang chủ",
				iconSelectedRes = R.drawable.ic_home,
				iconUnselectedRes = R.drawable.ic_home,
				iconTintSelected = colorSelected,
				iconTintUnselected = colorUnselected,
				textColorSelected = colorSelected,
				textColorUnselected = colorUnselected
			),
			BottomBarItem(
				id = 2,
				title = "Mua sắm",
				iconSelectedRes = R.drawable.ic_shopping_bag,
				iconUnselectedRes = R.drawable.ic_shopping_bag,
				iconTintSelected = colorSelected,
				iconTintUnselected = colorUnselected,
				textColorSelected = colorSelected,
				textColorUnselected = colorUnselected
			),
			BottomBarItem(
				id = 3,
				title = "Thư viện",
				iconSelectedRes = R.drawable.ic_photo,
				iconUnselectedRes = R.drawable.ic_photo,
				iconTintSelected = colorSelected,
				iconTintUnselected = colorUnselected,
				textColorSelected = colorSelected,
				textColorUnselected = colorUnselected
			)
		)

		if (is4Items) {
			list.add(
				BottomBarItem(
					id = 4,
					title = "Cài đặt",
					iconSelectedRes = R.drawable.ic_tune,
					iconUnselectedRes = R.drawable.ic_tune,
					iconTintSelected = colorSelected,
					iconTintUnselected = colorUnselected,
					textColorSelected = colorSelected,
					textColorUnselected = colorUnselected
				)
			)
		}

		return list
	}

	private fun setupViewPager(items: List<BottomBarItem>) {
		fBinding.viewPager2.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
			override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
				val tv = TextView(parent.context).apply {
					layoutParams = ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT
					)
					gravity = Gravity.CENTER
					textSize = 18f
					setTextColor(ContextCompat.getColor(context, R.color.text_primary))
				}
				return object : RecyclerView.ViewHolder(tv) {}
			}

			override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
				val item = items[position]
				(holder.itemView as TextView).text = "📱 Nội dung Màn hình\n\n${item.title}\n(Index: $position)"
			}

			override fun getItemCount(): Int = items.size
		}
	}

	private fun initControlButtons() {
		// 1. Chế độ Ẩn / Hiện (Fade) - Mặc định
		fBinding.btnModeFade.setOnClickListener {
			fBinding.myBottomNav.indicatorAnimationMode = IndicatorAnimationMode.FADE
			requireActivity().toastMess("Đã chuyển sang chế độ: Ẩn/Hiện (Fade)", TypeToast.NONE)
		}

		// 2. Chế độ Trượt nảy Vật lý (Slide Spring)
		fBinding.btnModeSpring.setOnClickListener {
			fBinding.myBottomNav.indicatorAnimationMode = IndicatorAnimationMode.SLIDE_SPRING
			fBinding.myBottomNav.setSpringPhysics(tension = 1.4f, durationMs = 320L)
			requireActivity().toastMess("Đã chuyển sang: Trượt nảy Vật lý (Spring)", TypeToast.NONE)
		}

		// 3. Chế độ Trượt Mượt (Slide Smooth)
		fBinding.btnModeSmooth.setOnClickListener {
			fBinding.myBottomNav.indicatorAnimationMode = IndicatorAnimationMode.SLIDE_SMOOTH
			requireActivity().toastMess("Đã chuyển sang: Trượt Mượt (Smooth)", TypeToast.NONE)
		}

		// 4. Test Thay đổi số lượng Button động từ Code (3 ↔ 4)
		fBinding.btnToggleItemCount.setOnClickListener {
			is4Items = !is4Items
			val newItems = createBottomNavItems(is4Items)
			fBinding.myBottomNav.setItems(newItems, defaultSelectedIndex = 0)
			setupViewPager(newItems)
			requireActivity().toastMess("Số lượng nút hiện tại: ${newItems.size}", TypeToast.NONE)
		}

		// 5. Test Thay đổi Góc Bo Tròn (Corner Radius) cho Selected và Unselected
		fBinding.btnToggleCornerRadius.setOnClickListener {
			radiusState = (radiusState + 1) % 4
			when (radiusState) {
				0 -> {
					fBinding.myBottomNav.setCornerRadius(selectedDp = 16f, unselectedDp = 16f)
//                    requireActivity().toastMess("Bo góc: 16dp (Chọn) & 16dp (Không chọn)", TypeToast.NONE)
					fBinding.btnToggleCornerRadius.text = "Bo Góc 16f"
				}
				1 -> {
					fBinding.myBottomNav.setCornerRadius(selectedDp = 100f, unselectedDp = 100f)
//                    requireActivity().toastMess("Bo góc: 100dp (Pill tròn toàn phần)", TypeToast.NONE)
					fBinding.btnToggleCornerRadius.text = "Bo Góc 100f"
				}
				2 -> {
					fBinding.myBottomNav.setCornerRadius(selectedDp = 6f, unselectedDp = 6f)
//                    requireActivity().toastMess("Bo góc: 6dp (Vuông bo nhẹ)", TypeToast.NONE)
					fBinding.btnToggleCornerRadius.text = "Bo Góc 6f"
				}
				3 -> {
					fBinding.myBottomNav.setCornerRadius(selectedDp = 24f, unselectedDp = 8f)
//                    requireActivity().toastMess("Bo góc: 24dp (Chọn) & 8dp (Không chọn)", TypeToast.NONE)
					fBinding.btnToggleCornerRadius.text = "Bo Góc 24f"
				}
			}
		}

		// 6. Test Bật/Tắt và Chỉnh Mức Độ Mỏng Chiều Cao Khi Di Chuyển
		fBinding.btnToggleThinEffect.setOnClickListener {
			thinState = (thinState + 1) % 3
			when (thinState) {
				0 -> {
					fBinding.myBottomNav.setThinOnMoveEnabled(true)
					fBinding.myBottomNav.setThinFactor(1.0f)
					fBinding.btnToggleThinEffect.text = "Hiệu ứng mỏng theo độ dài: Bật (Chuẩn 1.0x)"
				}
				1 -> {
					fBinding.myBottomNav.setThinOnMoveEnabled(true)
					fBinding.myBottomNav.setThinFactor(1.5f)
					fBinding.btnToggleThinEffect.text = "Hiệu ứng mỏng theo độ dài: Bật (Sâu 1.5x)"
				}
				2 -> {
					fBinding.myBottomNav.setThinOnMoveEnabled(false)
					fBinding.btnToggleThinEffect.text = "Hiệu ứng mỏng theo độ dài: Tắt"
				}
			}
		}
	}
}