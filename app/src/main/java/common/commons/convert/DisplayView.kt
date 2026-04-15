package common.commons.convert

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.text.parseAsHtml
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import common.commons.convert.models.ConversionUnit
import common.commons.convert.models.ConvertData
import common.commons.convert.models.MC
import common.commons.databinding.DisplayBinding
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class DisplayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private companion object {
        const val TEXT_SIZE_LARGE = 48f
        const val TEXT_SIZE_MEDIUM = 42f
        const val TEXT_SIZE_SMALL = 36f
        const val THRESHOLD_MEDIUM = 10
        const val THRESHOLD_SMALL = 15
        const val COMMA = ","
        const val DOT = "."
    }

    private val binding = DisplayBinding.inflate(LayoutInflater.from(context), this)
    private var units: List<ConversionUnit> = emptyList()

    private val decimalFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = ','
    })

    private var sourceIndex = 0
        set(value) {
            if (field != value) {
                field = value
                onUnitChanged()
            }
        }
    private var resultIndex = 0
        set(value) {
            if (field != value) {
                field = value
                onUnitChanged()
            }
        }

    var convertData: ConvertData
        get() = ConvertData(
            binding.sourceValue.text.toString(),
            binding.resultValue.text.toString(),
            sourceIndex,
            resultIndex,
        )
        set(value) = with(binding) {
            sourceValue.setText(formatDisplay(value.value))
            sourceIndex = value.from
            resultIndex = value.to
            sourceSpinner.setText(units.getOrNull(sourceIndex)?.name ?: "", false)
            resultSpinner.setText(units.getOrNull(resultIndex)?.name ?: "", false)
            performCalculation()
        }

    init {
        orientation = VERTICAL
        setupListeners()
    }

    private fun setupListeners() = with(binding) {
        fab.setOnClickListener {
            convertData = convertData.swap()
        }

        sourceValue.showSoftInputOnFocus = false
        resultValue.showSoftInputOnFocus = false

        sourceValue.doAfterTextChanged { text ->
            val original = text.toString()
            val formatted = formatDisplay(original.unformat())

            if (original != formatted) {
                sourceValue.setText(formatted)
                sourceValue.setSelection(formatted.length)
            } else {
                adjustTextSize(sourceValue)
                performCalculation()
            }
        }

        resultValue.doAfterTextChanged {
            adjustTextSize(resultValue)
        }

        sourceSpinner.setOnItemClickListener { _, _, position, _ -> sourceIndex = position }
        sourceSpinner.setOnDismissListener { sourceSpinner.clearFocus() }

        resultSpinner.setOnItemClickListener { _, _, position, _ -> resultIndex = position }
        resultSpinner.setOnDismissListener { resultSpinner.clearFocus() }
    }

    private fun onUnitChanged() {
        updateSuffixes()
        performCalculation()
    }

    private fun adjustTextSize(editText: EditText) {
        val length = editText.text.length
        val size = when {
            length > THRESHOLD_SMALL -> TEXT_SIZE_SMALL
            length > THRESHOLD_MEDIUM -> TEXT_SIZE_MEDIUM
            else -> TEXT_SIZE_LARGE
        }
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setUnits(list: List<ConversionUnit>) {
        units = list
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, units.map { it.name })

        with(binding) {
            sourceSpinner.setAdapter(adapter)
            resultSpinner.setAdapter(adapter)
            
            val hasUnits = units.isNotEmpty()
            sourceSpinner.isEnabled = hasUnits
            resultSpinner.isEnabled = hasUnits

            if (hasUnits) {
                sourceIndex = 0
                resultIndex = if (units.size > 1) 1 else 0
                sourceSpinner.setText(units[sourceIndex].name, false)
                resultSpinner.setText(units[resultIndex].name, false)
            }
        }
    }

    fun clear() {
        binding.sourceValue.text?.clear()
        binding.resultValue.text?.clear()
    }

    fun appendText(text: CharSequence) {
        val editable = binding.sourceValue.text ?: return
        val current = editable.toString().unformat()

        if (text == DOT) {
            when {
                current.isEmpty() -> editable.append("0.")
                !current.contains(DOT) -> editable.append(DOT)
            }
            return
        }

        if (current == "0") {
            editable.replace(0, 1, text)
        } else {
            editable.append(text)
        }
    }

    fun removeLastDigit() {
        binding.sourceValue.text?.let {
            if (it.isNotEmpty()) it.delete(it.length - 1, it.length)
        }
    }

    private fun performCalculation() {
        val input = binding.sourceValue.text.toString().unformat()
        if (input.isEmpty() || input == DOT) {
            binding.resultValue.setText("")
            return
        }

        try {
            val cleanInput = input.removeSuffix(DOT)
            val source = BigDecimal(cleanInput, MC)
            val sourceFactor = units[sourceIndex].factor.toBigDecimal()
            val resultFactor = units[resultIndex].factor.toBigDecimal()
            val result = source * sourceFactor / resultFactor
            
            val resultStr = result.stripTrailingZeros().toPlainString()
            binding.resultValue.setText(formatDisplay(resultStr))
        } catch (_: Exception) {
            binding.resultValue.setText("")
        }
    }

    private fun formatDisplay(value: String): String {
        if (value.isEmpty() || value == DOT || value == "-") return value

        val parts = value.split(DOT)
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) DOT + parts[1] else ""

        val formattedInt = try {
            if (integerPart.isEmpty() || integerPart == "-") integerPart
            else decimalFormat.format(BigDecimal(integerPart))
        } catch (_: Exception) {
            integerPart
        }

        return formattedInt + decimalPart
    }

    private fun updateSuffixes() {
        if (units.isEmpty()) return
        binding.sourceValueContainer.setHtmlSuffix(units[sourceIndex].unitSymbol)
        binding.resultValueContainer.setHtmlSuffix(units[resultIndex].unitSymbol)
    }

    private fun TextInputLayout.setHtmlSuffix(text: String) {
        suffixText = text.parseAsHtml()
    }

    private fun String.unformat() = replace(COMMA, "")
}
