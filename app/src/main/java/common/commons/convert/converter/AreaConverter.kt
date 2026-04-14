package common.commons.convert.converter

import android.content.Context
import common.commons.R
import common.commons.convert.models.ConversionUnit
import common.commons.convert.models.SQUARE_POSTFIX

class AreaConverter(context: Context) {
    var units = listOf<ConversionUnit>()

    init {
        // Gốc là Square Metre (m2) = 1.0
        units = listOf(
            ConversionUnit(
                context.getString(R.string.squaremillimetre),
                0.000001,
                context.getString(R.string.millimetresymbol) + SQUARE_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.squarecentimetre),
                0.0001,
                context.getString(R.string.centimetresymbol) + SQUARE_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.square_decimetre),
                0.01,
                context.getString(R.string.decimetresymbol) + SQUARE_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.squaremetre),
                1.0,
                context.getString(R.string.metresymbol) + SQUARE_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.squarekilometre),
                1000000.0,
                context.getString(R.string.kilometresymbol) + SQUARE_POSTFIX,
            ),
            // Land Units
            ConversionUnit(
                context.getString(R.string.are),
                100.0,
                context.getString(R.string.aresymbol),
            ),
            ConversionUnit(
                context.getString(R.string.hectare),
                10000.0,
                context.getString(R.string.hectaresymbol),
            ),
            // Imperial/US
            ConversionUnit(
                context.getString(R.string.squareinch),
                0.00064516,
                context.getString(R.string.inchsymbol) + SQUARE_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.squarefoot),
                0.09290304,
                context.getString(R.string.footsymbol) + SQUARE_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.squareyard),
                0.83612736,
                context.getString(R.string.yardsymbol) + SQUARE_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.acre),
                4046.856,
                context.getString(R.string.acresymbol),
            ),
            ConversionUnit(
                context.getString(R.string.squaremile),
                2589988.11,
                context.getString(R.string.milesymbol) + SQUARE_POSTFIX,
            )
        )
    }
}
