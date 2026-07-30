package common.libs.admob

import com.google.android.gms.ads.interstitial.InterstitialAd

class CacheInterstitialAds(
	var nameAd: String,
	val idAd: String,
	var interstitialAd: InterstitialAd,
	var isShown: Boolean = false
) {
	fun isAdReady(): Boolean = !isShown

	fun markShown() {
		isShown = true
	}
}