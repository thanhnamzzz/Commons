package common.commons.convert.converter

import android.content.Context
import common.commons.R
import common.commons.convert.models.ConversionUnit

class LengthConverter(context: Context) {
    var units = listOf<ConversionUnit>()
    init {
        units = listOf(
            ConversionUnit(
                context.getString(R.string.metre),
                1.0,
                context.getString(R.string.metresymbol),
            ),
            ConversionUnit(
                context.getString(R.string.millimetre),
                0.001,
                context.getString(R.string.millimetresymbol),
            ),
            ConversionUnit(
                context.getString(R.string.centimetre),
                0.01,
                context.getString(R.string.centimetresymbol),
            ),
            ConversionUnit(
                context.getString(R.string.decimetre),
                0.1,
                context.getString(R.string.decimetresymbol),
            ),
            ConversionUnit(
                context.getString(R.string.kilometre),
                1000.0,
                context.getString(R.string.kilometresymbol),
            ),
            // Imperial/US
            ConversionUnit(
                context.getString(R.string.inch),
                0.0254,
                context.getString(R.string.inchsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.foot),
                0.3048,
                context.getString(R.string.footsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.yard),
                0.9144,
                context.getString(R.string.yardsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.mile),
                1609.344,
                context.getString(R.string.milesymbol),
            ),
            ConversionUnit(
                context.getString(R.string.nautical_mile),
                1852.0,
                context.getString(R.string.nmi),
            ),
            // Astronomical
            ConversionUnit(
                context.getString(R.string.astronomicalunit),
                149597870700.0,
                context.getString(R.string.astronomicalunitsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.lightyear),
                9460730472580800.0,
                context.getString(R.string.lightyearsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.parsec),
                3.0856776e16,
                context.getString(R.string.parsecsymbol),
            ),
        )
    }
}
