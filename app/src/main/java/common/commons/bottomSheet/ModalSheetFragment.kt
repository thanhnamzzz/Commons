package common.commons.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import common.commons.databinding.FragmentModalSheetBinding

class ModalSheetFragment : BottomSheetDialogFragment() {

	private var _binding: FragmentModalSheetBinding? = null
	private val binding get() = _binding!!
	private var expanded = MutableLiveData(0)

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentModalSheetBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.btnClose.setOnClickListener {
			dismiss()
		}

		expanded.observe(viewLifecycleOwner) {
			binding.index.text = it.toString()
		}
	}

	override fun onStart() {
		super.onStart()
		val dialog = dialog as? BottomSheetDialog
		val bottomSheet =
			dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
		bottomSheet?.let {
			val behavior = BottomSheetBehavior.from(it)
			behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
				override fun onStateChanged(bottomSheet: View, newState: Int) {
					when (newState) {
						BottomSheetBehavior.STATE_EXPANDED -> {
							// Trạng thái mở rộng hoàn toàn
							expanded.postValue(expanded.value?.plus(1))
						}

						BottomSheetBehavior.STATE_COLLAPSED -> {
							// Trạng thái thu gọn (đến mức peekHeight)
						}

						BottomSheetBehavior.STATE_DRAGGING -> {
							// Người dùng đang kéo bottom sheet lên hoặc xuống
						}

						BottomSheetBehavior.STATE_SETTLING -> {
							// Bottom sheet đang tự di chuyển về một vị trí cố định sau khi người dùng thả tay
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
					// slideOffset: 0 (collapsed) -> 1 (expanded)
				}
			})
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
