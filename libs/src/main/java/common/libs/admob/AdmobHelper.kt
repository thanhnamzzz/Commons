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
	private fun getPrefs(): SharedPreferences {
		return AppContext.getContext().getSharedPreferences(ADMOB_DATA, Context.MODE_PRIVATE)
	}

	fun getNumberClickAdsInDay(): Int {
		return getPrefs().getInt(NUMBER_CLICK_ADS_IN_DAY, 0)
	}

	fun setNumberClickAdsInDay() {
		getPrefs().edit {
			putInt(NUMBER_CLICK_ADS_IN_DAY, getNumberClickAdsInDay() + 1)
		}
	}

	fun checkCurrentDate() {
		val prefs = getPrefs()
		val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

		if (prefs.getString(CURRENT_DATE, "") != currentDate) {
			prefs.edit {
				putString(CURRENT_DATE, currentDate)
				putInt(NUMBER_CLICK_ADS_IN_DAY, 0)
			}
		}
	}

	private var maxClickAdsInDay = 0
	fun getMaxClickAdsInDay(): Int = maxClickAdsInDay
	fun setMaxClickAdsInDay(maxClickAdsInDay: Int) {
		this.maxClickAdsInDay = maxClickAdsInDay
	}

	fun isMaxClickAdsInDay(): Boolean {
		return getNumberClickAdsInDay() > maxClickAdsInDay
	}
}