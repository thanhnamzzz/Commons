package common.commons.convert.models

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import common.commons.R
import common.commons.convert.converter.*

enum class ConverterCategory(
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int
) {
    LENGTH(R.string.length, R.drawable.category_length),
    AREA(R.string.area, R.drawable.category_area),
    VOLUME(R.string.volume, R.drawable.category_volume),
    MASS(R.string.mass, R.drawable.category_weight),
    TIME(R.string.time, R.drawable.category_timer),
    SPEED(R.string.speed, R.drawable.category_speed);

    fun getUnits(context: Context): List<ConversionUnit> {
        return when (this) {
            LENGTH -> LengthConverter(context).units
            AREA -> AreaConverter(context).units
            VOLUME -> VolumeConverter(context).units
            MASS -> MassConverter(context).units
            TIME -> TimeConverter(context).units
            SPEED -> SpeedConverter(context).units
        }
    }
}
