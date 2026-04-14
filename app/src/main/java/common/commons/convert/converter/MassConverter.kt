package common.commons.convert.converter

import android.content.Context
import common.commons.R
import common.commons.convert.models.ConversionUnit

class MassConverter(context: Context) {
    var units = listOf<ConversionUnit>()
    init {
        units = listOf(
            ConversionUnit(
                context.getString(R.string.kilogram),
                1.0,
                context.getString(R.string.kilogramsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.gram),
                0.001,
                context.getString(R.string.gramsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.milligram),
                0.000001,
                context.getString(R.string.milligramsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.microgram),
                0.000000001,
                context.getString(R.string.microg),
            ),
            ConversionUnit(
                context.getString(R.string.tonne),
                1000.0,
                context.getString(R.string.tonnesymbol),
            ),
            ConversionUnit(
                context.getString(R.string.hundredweight),
                100.0,
                context.getString(R.string.hundredweightsymbol),
            ),
            // Hệ Anh/Mỹ
            ConversionUnit(
                context.getString(R.string.pound),
                0.45359237,
                context.getString(R.string.lb),
            ),
            ConversionUnit(
                context.getString(R.string.ounce),
                0.02834952,
                context.getString(R.string.oz),
            )
        )
    }
}
