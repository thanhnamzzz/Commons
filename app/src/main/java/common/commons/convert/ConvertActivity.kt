package common.commons.convert

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import common.commons.convert.models.ConverterCategory
import common.commons.databinding.ActivityConvertBinding
import common.libs.SimpleActivity

class ConvertActivity : SimpleActivity<ActivityConvertBinding>(ActivityConvertBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initData()
        setupKeyPad()
    }

    private fun initData() {
        val categories = ConverterCategory.entries
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            categories.map { getString(it.titleRes) }
        )
        binding.category.setAdapter(adapter)
        binding.category.setOnDismissListener { binding.category.clearFocus() }
        
        binding.category.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            updateCategorySelection(categories[position])
            binding.display.clear()
        }

        // Khởi tạo mặc định
        if (categories.isNotEmpty()) {
            updateCategorySelection(categories[0])
        }
    }

    private fun updateCategorySelection(category: ConverterCategory) {
        // Cập nhật text hiển thị
        binding.category.setText(getString(category.titleRes), false)
        // Cập nhật icon ở đầu
        binding.categoryContainer.setStartIconDrawable(category.iconRes)
        // Cập nhật danh sách đơn vị đo
        binding.display.setUnits(category.getUnits(this))
    }

    private fun setupKeyPad() {
        binding.keypad.setOnKeypadListener(object : OnKeypadListener {
            override fun onNumericClick(digit: String) {
                binding.display.appendText(digit)
            }

            override fun onBackspaceClick(isLong: Boolean) {
                if (isLong) binding.display.clear() else binding.display.removeLastDigit()
            }
        })
    }
}
