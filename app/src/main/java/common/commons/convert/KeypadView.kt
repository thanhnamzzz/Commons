package common.commons.convert

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import common.commons.R
import common.commons.databinding.KeypadBinding

interface OnKeypadListener {
    fun onNumericClick(digit: String)
    fun onBackspaceClick(isLong: Boolean)
}

class KeypadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = KeypadBinding.inflate(LayoutInflater.from(context), this)
    private var listener: OnKeypadListener? = null

    init {
        orientation = HORIZONTAL
        setupListeners()
    }

    fun setOnKeypadListener(listener: OnKeypadListener) {
        this.listener = listener
    }

    private fun setupListeners() {
        val numericClickListener = OnClickListener { view ->
            BUTTON_MAPPER.get(view.id)?.let { listener?.onNumericClick(it) }
        }

        with(binding) {
            val numericButtons = listOf(
                button0, button1, button2, button3, button4,
                button5, button6, button7, button8, button9,
                buttonDot
            )
            numericButtons.forEach { it.setOnClickListener(numericClickListener) }
            buttonBackspace.setActionListener { listener?.onBackspaceClick(it) }
        }
    }

    private fun View.setActionListener(action: (isLong: Boolean) -> Unit) {
        setOnClickListener { action(false) }
        setOnLongClickListener {
            action(true)
            true
        }
    }

    companion object {
        private val BUTTON_MAPPER = SparseArray<String>().apply {
            put(R.id.button0, "0")
            put(R.id.button1, "1")
            put(R.id.button2, "2")
            put(R.id.button3, "3")
            put(R.id.button4, "4")
            put(R.id.button5, "5")
            put(R.id.button6, "6")
            put(R.id.button7, "7")
            put(R.id.button8, "8")
            put(R.id.button9, "9")
            put(R.id.button_dot, ".")
        }
    }
}