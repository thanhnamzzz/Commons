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
    private val binding = DisplayBinding.inflate(LayoutInflater.from(context), this)
    private var listConversionUnit: List<ConversionUnit> = emptyList()

    private var sourceIndex = 0
        set(value) {
            field = value
            updateSuffixes()
            performCalculation()
        }
    private var resultIndex = 0
        set(value) {
            field = value
            updateSuffixes()
            performCalculation()
        }

    var convertData: ConvertData
        get() = with(binding) {
            ConvertData(
                sourceValue.text.toString(),
                resultValue.text.toString(),
                sourceIndex,
                resultIndex,
            )
        }
        set(value) = with(binding) {
            sourceValue.setText(formatDisplay(value.value))
            sourceIndex = value.from
            resultIndex = value.to
            sourceSpinner.setText(listConversionUnit.getOrNull(sourceIndex)?.name ?: "", false)
            resultSpinner.setText(listConversionUnit.getOrNull(resultIndex)?.name ?: "", false)
            performCalculation()
        }

    init {
        orientation = VERTICAL
        with(binding) {
            fab.setOnClickListener {
                val current = convertData
                convertData = current.swap()
            }
            sourceValue.showSoftInputOnFocus = false
            resultValue.showSoftInputOnFocus = false

            sourceValue.doAfterTextChanged { text ->
                val original = text.toString()
                val clean = original.replace(",", "")
                val formatted = formatDisplay(clean)

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

            sourceSpinner.setOnItemClickListener { _, _, position, _ ->
                sourceIndex = position
            }

            sourceSpinner.setOnDismissListener {
                sourceSpinner.clearFocus()
            }

            resultSpinner.setOnItemClickListener { _, _, position, _ ->
                resultIndex = position
            }

            resultSpinner.setOnDismissListener {
                resultSpinner.clearFocus()
            }
        }
    }

    private fun adjustTextSize(editText: EditText) {
        val text = editText.text.toString()
        val length = text.length
        val newSizeSp = when {
            length > 15 -> 24f
            length > 10 -> 36f
            else -> 48f
        }
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSizeSp)
    }

    fun setUnits(list: List<ConversionUnit>) {
        listConversionUnit = list
        val adapter = ArrayAdapter(
            context, android.R.layout.simple_list_item_1,
            listConversionUnit.map { it.name }
        )

        with(binding) {
            sourceSpinner.setAdapter(adapter)
            resultSpinner.setAdapter(adapter)
            if (listConversionUnit.isEmpty()) {
                sourceSpinner.isEnabled = false
                resultSpinner.isEnabled = false
            } else {
                sourceIndex = 0
                resultIndex = 1
                sourceSpinner.setText(listConversionUnit[sourceIndex].name, false)
                resultSpinner.setText(listConversionUnit[resultIndex].name, false)
                sourceSpinner.isEnabled = true
                resultSpinner.isEnabled = true
            }
        }
    }

    fun clear() {
        binding.resultValue.text?.clear()
        binding.sourceValue.text?.clear()
    }

    fun appendText(text: CharSequence) {
        val editable = binding.sourceValue.text ?: return
        val current = editable.toString().replace(",", "")

        if (text == ".") {
            when {
                current.isEmpty() -> editable.append("0.")
                !current.contains(".") -> editable.append(".")
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
            if (it.isNotEmpty()) {
                it.delete(it.length - 1, it.length)
            }
        }
    }

    private fun performCalculation() {
        val input = binding.sourceValue.text.toString().replace(",", "")
        if (input.isEmpty() || input == ".") {
            binding.resultValue.setText("")
            return
        }

        try {
            val cleanInput = if (input.endsWith(".")) input.dropLast(1) else input
            val source = BigDecimal(cleanInput, MC)
            val sourceFactor = listConversionUnit[sourceIndex].factor.toBigDecimal()
            val resultFactor = listConversionUnit[resultIndex].factor.toBigDecimal()
            val result = source * sourceFactor / resultFactor
            
            val resultStr = result.stripTrailingZeros().toPlainString()
            binding.resultValue.setText(formatDisplay(resultStr))
        } catch (_: Exception) {
            binding.resultValue.setText("")
        }
    }

    private fun formatDisplay(value: String): String {
        if (value.isEmpty()) return ""
        if (value == "." || value == "-") return value

        val clean = value.replace(",", "")
        val parts = clean.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) "." + parts[1] else ""

        val formattedInt = try {
            if (integerPart.isEmpty() || integerPart == "-") {
                integerPart
            } else {
                val symbols = DecimalFormatSymbols(Locale.US)
                symbols.groupingSeparator = ','
                val df = DecimalFormat("#,###", symbols)
                df.format(BigDecimal(integerPart))
            }
        } catch (_: Exception) {
            integerPart
        }

        return formattedInt + decimalPart
    }

    private fun updateSuffixes() {
        if (listConversionUnit.isEmpty()) return
        binding.sourceValueContainer.setHtmlSuffixText(listConversionUnit[sourceIndex].unitSymbol)
        binding.resultValueContainer.setHtmlSuffixText(listConversionUnit[resultIndex].unitSymbol)
    }

    private fun TextInputLayout.setHtmlSuffixText(text: String) {
        suffixText = text.parseAsHtml()
    }
}
