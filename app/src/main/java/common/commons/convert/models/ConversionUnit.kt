package common.commons.convert.models

import java.math.MathContext
import java.math.RoundingMode

data class ConversionUnit(
    val name: String,
    val factor: Double,
    val unitSymbol: String,
) {
    companion object {
        val EMPTY = ConversionUnit("", 1.0, "")
    }
}

val MC = MathContext(9, RoundingMode.HALF_UP)
const val SQUARE_POSTFIX = "²"
const val CUBIC_POSTFIX = "³"
