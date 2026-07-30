package common.libs.admob

import com.google.android.gms.ads.rewarded.RewardedAd

class CacheRewardAds(
	var nameAd: String,
	val idRewardAd: String,
	var rewardAd: RewardedAd,
	var isShown: Boolean = false
) {
	fun isAdReady(): Boolean = !isShown

	fun markShown() {
		isShown = true
	}
}