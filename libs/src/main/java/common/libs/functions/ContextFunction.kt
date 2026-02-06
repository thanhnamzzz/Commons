package common.libs.functions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.FileProvider
import java.io.File
import androidx.core.net.toUri

fun Context.hideKeyBoard(editText: EditText) {
	editText.clearFocus()
	val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
}

fun Context.showKeyBoard(editText: EditText) {
	editText.requestFocus()
	val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity.shareApp() {
	val intent = Intent(Intent.ACTION_SEND)
	intent.type = "text/plain"
	intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this cool app!")
	intent.putExtra(
		Intent.EXTRA_TEXT,
		"Download the app from: https://play.google.com/store/apps/details?id=${packageName}"
	)
	startActivity(Intent.createChooser(intent, "Share via"))
}

fun Activity.openPrivacyPolicy(link: String) {
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
		Log.d("Namzzz", "GlobalFunction: openPrivacyPolicy link is blank")
		return
	}
	val intent = Intent(
		Intent.ACTION_VIEW,
		link.toUri()
	)
	if (intent.resolveActivity(packageManager) != null) startActivity(intent)
	else Log.d("Namzzz", "GlobalFunction: openPrivacyPolicy null")
}

/** Khai báo provider ở manifest bắt buộc phải đặt `authorities="${applicationId}.provider"` */
fun Context.shareFile(file: File, fileDoesNotExists: () -> Unit) {
	if (file.exists()) {
		val uri: Uri =
			FileProvider.getUriForFile(this, "${packageName}.provider", file)
		val intent = Intent(Intent.ACTION_SEND).apply {
			data = uri
			putExtra(Intent.EXTRA_STREAM, uri)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
		}
		startActivity(Intent.createChooser(intent, "Share file using"))
	} else {
		fileDoesNotExists()
	}
}

fun Context.shareText(text: String) {
	val intent = Intent(Intent.ACTION_SEND).apply {
		type = "text/plain"
		putExtra(Intent.EXTRA_TEXT, text)
	}
	val chooser = Intent.createChooser(intent, "Share text via")
	startActivity(chooser)
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

fun Activity.feedbackOnEmail(
	mailDevelop: String = "store@gambi.global",
	subject: String = "Feedback for Team",
	body: String = "Dear Development Team,\n",
) {
	val intent = Intent(Intent.ACTION_SENDTO).apply {
//		putExtra(Intent.EXTRA_SUBJECT, subject)
//		putExtra(Intent.EXTRA_TEXT, body)
//		data = "mailto:${MainApp.EMAIL_DEVELOPER}".toUri()
		data =
			"mailto:${mailDevelop}?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}".toUri()
	}
	intent.resolveActivity(packageManager)?.let {
		startActivity(intent)
	} ?: run {
		openMarket()
	}
}