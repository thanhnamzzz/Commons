package common.libs.admob

import com.google.android.gms.ads.appopen.AppOpenAd

class CacheAppOpenAds(
	val idAds: String,
	var appOpenAds: AppOpenAd,
	var timeLoaded: Long,
	var isShowingAd: Boolean = false
) {
	fun markAsShowing() {
		isShowingAd = true
	}
}