package common.libs.admob

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

object AdmobHelper {
	private const val ADMOB_DATA = "admob_data"
	private const val NUMBER_CLICK_ADS_IN_DAY = "number_click_ads_in_day"
	private const val CURRENT_DATE = "current_date"
	private fun getPrefs(context: Context): SharedPreferences {
		return context.getSharedPreferences(ADMOB_DATA, Context.MODE_PRIVATE)
	}

	fun getNumberClickAdsInDay(context: Context): Int {
		return getPrefs(context).getInt(NUMBER_CLICK_ADS_IN_DAY, 0)
	}

	fun setNumberClickAdsInDay(context: Context) {
		getPrefs(context).edit {
			putInt(NUMBER_CLICK_ADS_IN_DAY, getNumberClickAdsInDay(context) + 1)
		}
	}

	fun checkCurrentDate(context: Context) {
		val prefs = getPrefs(context)
		val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

		if (prefs.getString(CURRENT_DATE, "") != currentDate) {
			prefs.edit {
				putString(CURRENT_DATE, currentDate)
				putInt(NUMBER_CLICK_ADS_IN_DAY, 0)
			}
		}
	}
}