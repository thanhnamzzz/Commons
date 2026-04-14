package common.commons.convert.converter

import android.content.Context
import common.commons.R
import common.commons.convert.models.CUBIC_POSTFIX
import common.commons.convert.models.ConversionUnit

class VolumeConverter(context: Context) {
    var units = listOf<ConversionUnit>()

    init {
        units = listOf(
            ConversionUnit(
                context.getString(R.string.cubicmetre),
                1.0,
                context.getString(R.string.metresymbol) + CUBIC_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.litre),
                0.001,
                context.getString(R.string.litresymbol),
            ),
            ConversionUnit(
                context.getString(R.string.millilitre),
                0.000001,
                context.getString(R.string.millilitresymbol),
            ),
            // US Units
            ConversionUnit(
                context.getString(R.string.gallon),
                0.00378541,
                context.getString(R.string.gallonsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.us_quart),
                0.000946353,
                context.getString(R.string.qt),
            ),
            ConversionUnit(
                context.getString(R.string.us_pint),
                0.000473176,
                context.getString(R.string.pt),
            ),
            ConversionUnit(
                context.getString(R.string.us_cup),
                0.000236588,
                context.getString(R.string.cup),
            ),
            ConversionUnit(
                context.getString(R.string.barrel),
                0.158988,
                context.getString(R.string.barrelsymbol),
            ),
            // Imperial/Other
            ConversionUnit(
                context.getString(R.string.cubic_inch),
                0.000016387,
                context.getString(R.string.inch) + CUBIC_POSTFIX,
            ),
            ConversionUnit(
                context.getString(R.string.cubic_foot),
                0.0283168,
                context.getString(R.string.ft) + CUBIC_POSTFIX,
            )
        )
    }
}
