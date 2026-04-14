package common.commons.convert.converter

import android.content.Context
import common.commons.R
import common.commons.convert.models.ConversionUnit

class SpeedConverter(context: Context) {
    var units = listOf<ConversionUnit>()

    init {
        units = listOf(
            ConversionUnit(
                context.getString(R.string.kilometresperhour),
                1.0,
                context.getString(R.string.kmpssymbol),
            ),
            ConversionUnit(
                context.getString(R.string.metrespersecond),
                3.6,
                context.getString(R.string.mpssymbol),
            ),
            ConversionUnit(
                context.getString(R.string.milesperhour),
                1.609344,
                context.getString(R.string.miphsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.footspersecond),
                1.09728,
                context.getString(R.string.ftpssymbol),
            ),
            ConversionUnit(
                context.getString(R.string.knots),
                1.852,
                context.getString(R.string.knotssymbol),
            ),
            ConversionUnit(
                context.getString(R.string.mach),
                1225.044,
                context.getString(R.string.ma),
            )
        )
    }
}
