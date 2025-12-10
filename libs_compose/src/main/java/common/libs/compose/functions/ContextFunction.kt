package common.libs.compose.functions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.net.toUri

fun Context.shareApp() {
	val intent = Intent(Intent.ACTION_SEND)
	intent.type = "text/plain"
	intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this cool app!")
	intent.putExtra(
		Intent.EXTRA_TEXT,
		"Download the app from: https://play.google.com/store/apps/details?id=${packageName}"
	)
	startActivity(Intent.createChooser(intent, "Share via"))
}

fun Context.openPrivacyPolicy(link: String) {
	//Need add queries action view in manifest
	/**		<queries> <!-- Add queries for open privacy policy -->
	 *			<intent>
	 *				<action android:name="android.intent.action.VIEW" />
	 *
	 *				<data android:scheme="http" />
	 *			</intent>
	 *		</queries>
	 */
	if (link.isBlank()) {
		Log.e("Namzzz", "GlobalFunction: openPrivacyPolicy link is blank")
		return
	}
	val intent = Intent(
		Intent.ACTION_VIEW,
		link.toUri()
	)
	if (intent.resolveActivity(packageManager) != null) startActivity(intent)
	else Log.d("Namzzz", "GlobalFunction: openPrivacyPolicy null")
}

fun Context.versionApp(): String {
	return packageManager.getPackageInfo(packageName, 0).versionName.toString()
}

fun Context.openMarket() {
	try {
		val intent =
			Intent(Intent.ACTION_VIEW, "market://details?id=${packageName}".toUri())
		startActivity(intent)
	} catch (_: ActivityNotFoundException) {
		val intent = Intent(
			Intent.ACTION_VIEW,
			"https://play.google.com/store/apps/details?id=${packageName}".toUri()
		)
		startActivity(intent)
	}
}

fun Context.openMoreApp(id: String) {
	if (id.isBlank()) {
		Log.d("Namzzz", "GlobalFunction: openMoreApp id is blank")
		return
	}
	val devUrl = "https://play.google.com/store/apps/developer?id=$id"
	val intent = Intent(Intent.ACTION_VIEW).apply {
		data = devUrl.toUri()
		setPackage("com.android.vending")
	}
	try {
		startActivity(intent)
	} catch (_: ActivityNotFoundException) {
		val webIntent = Intent(Intent.ACTION_VIEW, devUrl.toUri())
		startActivity(webIntent)
	}
}

fun Context.isConnectedInternet(): Boolean {
	val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	//từ Android 6 trở lên API23
	val network = connectivityManager.activeNetwork ?: return false
	val networkCapabilities =
		connectivityManager.getNetworkCapabilities(network) ?: return false
	return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
			networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
			networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}