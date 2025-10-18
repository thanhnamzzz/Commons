package common.libs.functions

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copy", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.getTextFromClipboard(): String {
    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    if (clipboard.hasPrimaryClip()
        && clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
    ) {
        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text
        return (text ?: "").toString()
    } else {
        return ""
    }
}