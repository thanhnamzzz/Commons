package common.commons.bottomSheet

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import common.commons.databinding.ActivityBottomSheetBinding
import common.libs.SimpleActivity

class BottomSheetActivity :
	SimpleActivity<ActivityBottomSheetBinding>(ActivityBottomSheetBinding::inflate) {

	private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
	private var i = 0
	private val sheetModal by lazy { ModalSheetFragment() }
	private val sheetModalFull by lazy { ModalSheetFullFragment() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			binding.bottomSheetContainer.setPadding(0, 0, 0, systemBars.bottom)
			insets
		}

		setupPersistentFragment()
		setupBottomSheet()
		setupListeners()
	}

	private fun setupPersistentFragment() {
		// Thêm Fragment vào FrameLayout của Bottom Sheet
		supportFragmentManager.beginTransaction()
			.replace(binding.bottomSheetContainer.id, PersistentSheetFragment())
			.commit()
	}

	private fun setupBottomSheet() {
		// Khởi tạo BottomSheetBehavior từ FrameLayout
		bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

		// Ẩn Bottom Sheet ngay khi khởi tạo
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

		bottomSheetBehavior.addBottomSheetCallback(object :
			BottomSheetBehavior.BottomSheetCallback() {
			override fun onStateChanged(bottomSheet: View, newState: Int) {
				when (newState) {
					BottomSheetBehavior.STATE_EXPANDED -> {
						// Trạng thái mở rộng hoàn toàn
					}
					BottomSheetBehavior.STATE_COLLAPSED -> {
						// Trạng thái thu gọn (đến mức peekHeight)
					}
					BottomSheetBehavior.STATE_DRAGGING -> {
						// Người dùng đang kéo bottom sheet
					}
					BottomSheetBehavior.STATE_SETTLING -> {
						// Bottom sheet đang tự trượt về vị trí cố định
					}
					BottomSheetBehavior.STATE_HIDDEN -> {
						// Bottom sheet đã bị ẩn hoàn toàn
					}
					BottomSheetBehavior.STATE_HALF_EXPANDED -> {
						// Trạng thái mở rộng một nửa
					}
				}
			}

			override fun onSlide(bottomSheet: View, slideOffset: Float) {
				// slideOffset: 0 (collapsed) -> 1 (expanded), -1 (hidden)
			}
		})
	}

	private fun setupListeners() {
		// Tương tác với thành phần của Main Activity
		binding.btnMainAction.setOnClickListener {
			i++
			binding.textOutput.text = "Main Action Click Count: $i"
		}

		// Các thao tác điều khiển từ Activity
		binding.btnShowSheet.setOnClickListener {
			bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
		}

		binding.btnHideSheet.setOnClickListener {
			bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
		}

		binding.btnFullSheet.setOnClickListener {
			bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
		}

		// Mở Modal Bottom Sheet (Loại Fragment Dialog)
		binding.btnOpenModal.setOnClickListener {
			sheetModal.show(supportFragmentManager, "ModalSheet")
		}
		binding.btnOpenModalFull.setOnClickListener {
			sheetModalFull.show(supportFragmentManager, "ModalSheetFull")
		}
	}
}
