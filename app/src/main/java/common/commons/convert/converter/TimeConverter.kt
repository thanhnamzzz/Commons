package common.commons.convert.converter

import android.content.Context
import common.commons.R
import common.commons.convert.models.ConversionUnit

class TimeConverter(context: Context) {
    var units = listOf<ConversionUnit>()

    init {
        units = listOf(
            ConversionUnit(
                context.getString(R.string.nanosecond),
                0.000000001,
                context.getString(R.string.ns),
            ),
            ConversionUnit(
                context.getString(R.string.microsecond),
                0.000001,
                context.getString(R.string.microsecondsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.millisecond),
                0.001,
                context.getString(R.string.millisecondsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.second),
                1.0,
                context.getString(R.string.secondsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.minute),
                60.0,
                context.getString(R.string.minutesymbol),
            ),
            ConversionUnit(
                context.getString(R.string.hour),
                3600.0,
                context.getString(R.string.hoursymbol),
            ),
            ConversionUnit(
                context.getString(R.string.day),
                86400.0,
                context.getString(R.string.daysymbol),
            ),
            ConversionUnit(
                context.getString(R.string.week),
                604800.0,
                context.getString(R.string.weeksymbol),
            ),
            ConversionUnit(
                context.getString(R.string.year),
                31557600.0, // 365.25 ngày
                context.getString(R.string.yearsymbol),
            ),
            ConversionUnit(
                context.getString(R.string.decade),
                315576000.0,
                context.getString(R.string.dec),
            ),
            ConversionUnit(
                context.getString(R.string.century),
                3155760000.0,
                context.getString(R.string.cen),
            )
        )
    }
}
