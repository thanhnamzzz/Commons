package common.libs.extensions

import android.content.Context
import android.os.Environment
import android.os.storage.StorageManager
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

const val PRIMARY_VOLUME_NAME = "external_primary"
fun Context.getAllVolumeNames(): MutableList<VolumeInfo> {
    val volumesInfo = mutableListOf<VolumeInfo>()
    val volumeNames = mutableListOf(PRIMARY_VOLUME_NAME)
    val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
    if (isN24Plus()) {
        getExternalFilesDirs(null)
            .mapNotNull { storageManager.getStorageVolume(it) }
            .filterNot { it.isPrimary }
            .mapNotNull { it.uuid?.lowercase(Locale.US) }
            .forEach {
                volumeNames.add(it)
            }
    } else {
        volumeNames.clear()
        getExternalFilesDirs(null).forEach { folder ->
            val volumeName = if (Environment.isExternalStorageEmulated(folder)) {
                PRIMARY_VOLUME_NAME
            } else {
                val parts = folder.absolutePath.split("/")
                if (parts.size >= 3) parts[2] else null
            }
            volumeName?.let {
                volumeNames.add(it)
            }
        }
    }
    volumeNames.forEach {
        val vI = if (it == PRIMARY_VOLUME_NAME) {
            VolumeInfo(it, Environment.getExternalStorageDirectory().absolutePath)
        } else {
            VolumeInfo(it, "/storage/$it")
        }
        volumesInfo.add(vI)
    }
    return volumesInfo
}

class VolumeInfo(
    var volumeName: String,
    var volumePath: String
) {
    override fun toString(): String {
        return "volumeName: $volumeName | volumePath: $volumePath"
    }
}

fun Long.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "${DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble()))} ${units[digitGroups]}"
}