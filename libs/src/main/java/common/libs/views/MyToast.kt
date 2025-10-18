package common.libs.views

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntRange
import common.libs.R
import common.libs.extensions.gone

private fun Activity.initToast(
    typeToast: TypeToast,
    message: String,
    @IntRange(from = 0, to = 1) duration: Int,
): Toast {
    val toast = Toast(this)
    val layoutInflater = layoutInflater
    val view: View = layoutInflater.inflate(
        R.layout.layout_my_toast,
        findViewById(R.id.layout_toast)
    )
    val layoutT = view.findViewById<LinearLayout>(R.id.layout_toast)
    val messageT = view.findViewById<TextView>(R.id.message_toast)
    val imageT = view.findViewById<ImageView>(R.id.image_toast)
    when (typeToast) {
        TypeToast.TOAST_SUCCESS -> {
            layoutT.setBackgroundResource(R.drawable.bg_toast_success)
            imageT.setImageResource(R.drawable.icon_check)
        }

        TypeToast.TOAST_ERROR -> {
            layoutT.setBackgroundResource(R.drawable.bg_toast_error)
            imageT.setImageResource(R.drawable.icon_close)
        }

        TypeToast.TOAST_WARNING -> {
            layoutT.setBackgroundResource(R.drawable.bg_toast_warning)
            imageT.setImageResource(R.drawable.icon_warning)
        }

        TypeToast.TOAST_NONE -> {
            layoutT.setBackgroundResource(R.drawable.bg_toast_none)
            imageT.gone()
            layoutT.setPadding(80, 30, 80, 30)
        }
    }
    messageT.text = message
    @Suppress("DEPRECATION")
    toast.view = view
    toast.setGravity(Gravity.BOTTOM, 0, 100)
    toast.duration = when (duration) {
        0 -> Toast.LENGTH_SHORT
        else -> Toast.LENGTH_LONG
    }
    return toast
}

fun Activity.showToastSuccess(
    message: String,
    @IntRange(from = 0, to = 1) duration: Int,
) {
    val toast = initToast(TypeToast.TOAST_SUCCESS, message, duration)
    toast.show()
}

fun Activity.showToastError(
    message: String,
    @IntRange(from = 0, to = 1) duration: Int,
) {
    val toast = initToast(TypeToast.TOAST_ERROR, message, duration)
    toast.show()
}

fun Activity.showToastWarning(
    message: String,
    @IntRange(from = 0, to = 1) duration: Int,
) {
    val toast = initToast(TypeToast.TOAST_WARNING, message, duration)
    toast.show()
}

fun Activity.showToastNone(
    message: String,
    @IntRange(from = 0, to = 1) duration: Int,
) {
    val toast = initToast(TypeToast.TOAST_NONE, message, duration)
    toast.show()
}

enum class TypeToast {
    TOAST_SUCCESS,
    TOAST_ERROR,
    TOAST_WARNING,
    TOAST_NONE
}