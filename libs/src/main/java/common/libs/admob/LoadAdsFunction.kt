package common.libs.admob

import android.content.Context
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

@WorkerThread
fun loadAdNative(
	context: Context,
	idAdsNative: String,
	onNativeAdLoaded: (NativeAd) -> Unit,
	onNativeAdLoadFail: ((LoadAdError) -> Unit)? = null,
	onNativeAdClicked: (() -> Unit)? = null
) {
	val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
	val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
	val adLoader = AdLoader.Builder(context, idAdsNative)
		.forNativeAd { nativeAd: NativeAd? ->
			if (nativeAd != null) {
				onNativeAdLoaded(nativeAd)
			}
		}.withAdListener(object : AdListener() {
			override fun onAdFailedToLoad(p0: LoadAdError) {
				super.onAdFailedToLoad(p0)
				onNativeAdLoadFail?.invoke(p0)
			}

			override fun onAdClicked() {
				super.onAdClicked()
				onNativeAdClicked?.invoke()
			}
		}).withNativeAdOptions(adOptions).build()
	adLoader.loadAd(AdRequest.Builder().build())
}

@MainThread
fun loadAdBanner(
	context: Context,
	idAdsBanner: String,
	adSize: AdSize,
	onBannerLoadFail: ((LoadAdError) -> Unit)? = null,
	onBannerLoaded: ((AdView) -> Unit)? = null,
	onBannerClicked: (() -> Unit)? = null
) {
	val adViewBanner = AdView(context)
	adViewBanner.apply {
		setAdSize(adSize)
		adUnitId = idAdsBanner
		loadAd(AdRequest.Builder().build())
		adListener = object : AdListener() {
			override fun onAdFailedToLoad(p0: LoadAdError) {
				super.onAdFailedToLoad(p0)
				onBannerLoadFail?.invoke(p0)
			}

			override fun onAdLoaded() {
				super.onAdLoaded()
				onBannerLoaded?.invoke(adViewBanner)
			}

			override fun onAdClicked() {
				super.onAdClicked()
				onBannerClicked?.invoke()
			}
		}
	}
}

@MainThread
fun loadAdCollapsibleBanner(
	context: Context,
	idAdsCollapsibleBanner: String,
	adSize: AdSize,
	onCollapsibleLoaded: ((AdView) -> Unit)? = null,
	onCollapsibleLoadFail: ((LoadAdError) -> Unit)? = null,
	onCollapsibleClicked: (() -> Unit)? = null,
) {
	val adCollapsibleBanner = AdView(context)
	adCollapsibleBanner.apply {
		setAdSize(adSize)
		adUnitId = idAdsCollapsibleBanner
		val extras = Bundle().apply {
			putString("collapsible", "bottom")
		}
		val adRequest =
			AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
		loadAd(adRequest)
		adListener = object : AdListener() {
			override fun onAdLoaded() {
				super.onAdLoaded()
				onCollapsibleLoaded?.invoke(adCollapsibleBanner)
			}

			override fun onAdFailedToLoad(p0: LoadAdError) {
				super.onAdFailedToLoad(p0)
				onCollapsibleLoadFail?.invoke(p0)
			}

			override fun onAdClicked() {
				super.onAdClicked()
				onCollapsibleClicked?.invoke()
			}
		}
	}
}

@MainThread
fun loadAdInterstitial(
	context: Context,
	idAdsInterstitial: String,
	onInterLoadFail: ((LoadAdError) -> Unit)? = null,
	onInterLoaded: ((InterstitialAd) -> Unit)? = null,
) {
	val adRequest = AdRequest.Builder().build()

	InterstitialAd.load(
		context,
		idAdsInterstitial,
		adRequest,
		object : InterstitialAdLoadCallback() {
			override fun onAdFailedToLoad(p0: LoadAdError) {
				super.onAdFailedToLoad(p0)
				onInterLoadFail?.invoke(p0)
			}

			override fun onAdLoaded(p0: InterstitialAd) {
				super.onAdLoaded(p0)
				onInterLoaded?.invoke(p0)
			}
		})
}

@MainThread
fun loadAdReward(
	context: Context,
	idAdReward: String,
	onRewardLoadFail: ((LoadAdError) -> Unit)? = null,
	onRewardLoaded: ((RewardedAd) -> Unit)? = null,
) {
	val adRequest = AdRequest.Builder().build()

	RewardedAd.load(
		context,
		idAdReward,
		adRequest,
		object : RewardedAdLoadCallback() {
			override fun onAdFailedToLoad(p0: LoadAdError) {
				super.onAdFailedToLoad(p0)
				onRewardLoadFail?.invoke(p0)
			}

			override fun onAdLoaded(p0: RewardedAd) {
				super.onAdLoaded(p0)
				onRewardLoaded?.invoke(p0)
			}
		})
}

@MainThread
fun loadAdRewardInterstitial(
	context: Context,
	idAdRewardInterstitial: String,
	onInterRewardLoadFail: ((LoadAdError) -> Unit)? = null,
	onInterRewardLoaded: ((RewardedInterstitialAd) -> Unit)? = null,
) {
	val adRequest = AdRequest.Builder().build()

	RewardedInterstitialAd.load(
		context,
		idAdRewardInterstitial,
		adRequest,
		object : RewardedInterstitialAdLoadCallback() {
			override fun onAdFailedToLoad(p0: LoadAdError) {
				super.onAdFailedToLoad(p0)
				onInterRewardLoadFail?.invoke(p0)
			}

			override fun onAdLoaded(p0: RewardedInterstitialAd) {
				super.onAdLoaded(p0)
				onInterRewardLoaded?.invoke(p0)
			}
		})
}