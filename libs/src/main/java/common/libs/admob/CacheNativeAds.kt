package common.libs.admob

import com.google.android.gms.ads.nativead.NativeAd

class CacheNativeAds(
	var nameAd: String,
	val idNativeAd: String,
	var nativeAd: NativeAd,
	var isShown: Boolean = false
) {
	fun isAdReady(): Boolean = !isShown

	fun markShown() {
		isShown = true
	}
}