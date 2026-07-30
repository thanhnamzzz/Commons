package common.libs.admob

import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

class CacheRewardInterstitialAds(
	var nameAd: String,
	val idRewardAd: String,
	var rewardAd: RewardedInterstitialAd,
	var isShown: Boolean = false
) {
	fun isAdReady(): Boolean = !isShown

	fun markShown() {
		isShown = true
	}
}