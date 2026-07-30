package common.libs.admob

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import common.libs.R
import common.libs.databinding.LayoutLoadingAdsBinding
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

val GlobalBundle: Bundle by lazy { Bundle() }
private val isMobileAdsInitializeCalled = AtomicBoolean(false)
fun Activity.initMobileAds(
	googleConsent: GoogleMobileAdsConsentManager,
	onDone: (() -> Unit)? = null
) {
	if (googleConsent.canRequestAds) {
		initMobileAdsSdk(this, onDone)
	} else {
		googleConsent.gatherConsent(this) { error ->
			error?.let {
				initMobileAdsSdk(this, onDone)
			}
			if (googleConsent.canRequestAds) {
				initMobileAdsSdk(this, onDone)
			} else {
				onDone?.invoke()
			}
		}
	}
}

private fun initMobileAdsSdk(activity: Activity, callback: (() -> Unit)? = null) {
	if (isMobileAdsInitializeCalled.getAndSet(true)) {
		callback?.invoke()
		return
	}

	CoroutineScope(Dispatchers.IO).launch {
		MobileAds.initialize(activity) {
			activity.runOnUiThread {
				callback?.invoke()
			}
		}
	}
}

fun binLifecycle(context: Context): Lifecycle? {
	val lifecycleOwner = getActivityLifecycleOwner(context)
	return lifecycleOwner?.lifecycle
}

private fun getActivityLifecycleOwner(context: Context): LifecycleOwner? {
	return when (context) {
		is LifecycleOwner -> context // Trường hợp Activity là LifecycleOwner
		else -> null
	}
}

fun View.getBannerSizeWithScreen(activity: Activity): AdSize {
	val displayMetrics = resources.displayMetrics
	val adWidthPixels =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
			windowMetrics.bounds.width()
		} else {
			displayMetrics.widthPixels
		}
	val density = displayMetrics.density
	val adWidth = (adWidthPixels / density).toInt()
	return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
}

fun Activity.getBannerSizeWithScreen(): AdSize {
	val displayMetrics = resources.displayMetrics
	val adWidthPixels =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
			windowMetrics.bounds.width()
		} else {
			displayMetrics.widthPixels
		}
	val density = displayMetrics.density
	val adWidth = (adWidthPixels / density).toInt()
	return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}

//fun checkShowAds(): Boolean {
//	val admob = AdmobManager
//	return admob.getMaxClickAdsInDay() == 0
//			|| (admob.getMaxClickAdsInDay() != 0 && !admob.isMaxClickAdsInDay())
//}

fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
	if (isActive) resume(value)
}

fun isAppOpenAvailable(cache: CacheAppOpenAds): Boolean {
	return wasLoadTimeLessThanNHoursAgo(cache.timeLoaded)
}
fun isAppOpenAvailable(timeLoaded: Long): Boolean {
	return wasLoadTimeLessThanNHoursAgo(timeLoaded)
}

private fun wasLoadTimeLessThanNHoursAgo(timeLoaded: Long): Boolean {
	val dateDifference: Long = Date().time - timeLoaded
	val numMilliSecondsPerHour: Long = 3600000 * 4
	return dateDifference < numMilliSecondsPerHour
}

fun getDialogLoading(context: Context): Dialog {
	val binding = LayoutLoadingAdsBinding.inflate(LayoutInflater.from(context))
	val dialog = Dialog(context)
	dialog.setContentView(binding.root)
	dialog.setCancelable(false)
	dialog.window?.apply {
		setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT
		)
		setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
		val windowAttribute = attributes
		windowAttribute.gravity = Gravity.CENTER
		attributes = windowAttribute
	}
	return dialog
}

fun showLoadingOverlay(activity: Activity): View {
	val root = activity.findViewById<ViewGroup>(android.R.id.content)
	val v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_ads, root, false)
	v.isClickable = false
	root.addView(v)
	return v
}

fun hideLoadingOverlay(view : View) {
	(view.parent as? ViewGroup)?.removeView(view)
}