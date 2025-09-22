package common.libs.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import common.libs.extensions.isO26Plus
import common.libs.extensions.isR30Plus

fun Context.openAppSettings(resultLauncher: ActivityResultLauncher<Intent>) {
	val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
		data = Uri.fromParts("package", packageName, null)
	}
	resultLauncher.launch(intent)
}

fun Context.setAllFile(resultLauncher: ActivityResultLauncher<Intent>) {
	if (isR30Plus()) {
		val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
			data = "package:$packageName".toUri()
		}
		if (intent.resolveActivity(packageManager) != null)
			resultLauncher.launch(intent)
		else openAppSettings(resultLauncher)
	}
}

fun Context.openAppSettingsDrawOverOtherApp(resultLauncher: ActivityResultLauncher<Intent>) {
	val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
		data = "package:$packageName".toUri()
	}
	resultLauncher.launch(intent)
}

/** Quyền truy cập thông báo */
fun openAppSettingsNotificationAccess(resultLauncher: ActivityResultLauncher<Intent>) {
	val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
	resultLauncher.launch(intent)
}

/** Quyền thông báo */
fun Context.openAppSettingsNotification(resultLauncher: ActivityResultLauncher<Intent>) {
	if (isO26Plus()) {
		val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
			putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
		}
		resultLauncher.launch(intent)
	} else {
		openAppSettings(resultLauncher)
	}
}

fun openAppSettingsLocation(resultLauncher: ActivityResultLauncher<Intent>) {
	val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
	resultLauncher.launch(intent)
}

fun openAppSettingsWifi(resultLauncher: ActivityResultLauncher<Intent>) {
	val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
	resultLauncher.launch(intent)
}