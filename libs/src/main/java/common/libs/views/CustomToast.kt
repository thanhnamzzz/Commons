package common.libs.views

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import common.libs.R
import common.libs.extensions.gone
import java.lang.ref.WeakReference

/**
 * Biến toàn cục để quản lý việc ẩn Toast cũ khi Toast mới hiện lên.
 * Sử dụng WeakReference để tránh rò rỉ bộ nhớ (Memory Leak).
 */
private var currentToastView: WeakReference<View>? = null
private val toastHandler = Handler(Looper.getMainLooper())

/**
 * Hàm core để hiển thị Custom Overlay Toast
 */
private fun Activity.showCustomOverlayToast(
	typeToast: TypeToast,
	message: String,
	duration: Long = Duration.SHORT,
	style: ToastStyle = ToastStyle.VERTICAL,
	theme: ToastTheme = ToastTheme.SOFT,
) {
	if (isFinishing || isDestroyed) return
	val rootView = findViewById<ViewGroup>(android.R.id.content) ?: return

	currentToastView?.get()?.let { oldView ->
		(oldView.parent as? ViewGroup)?.removeView(oldView)
		toastHandler.removeCallbacksAndMessages(null)
	}

	val inflater = LayoutInflater.from(this)
	val layoutRes =
		if (style == ToastStyle.HORIZONTAL) R.layout.layout_toast_horizontal else R.layout.layout_toast_vertical
	val toastView = try {
		inflater.inflate(layoutRes, rootView, false)
	} catch (_: Exception) {
		return
	}

	val layoutT = toastView.findViewById<LinearLayout>(R.id.layout_toast)
	val messageT = toastView.findViewById<TextView>(R.id.message_toast)
	val imageT = toastView.findViewById<ImageView>(R.id.image_toast)

	val (bgColorRes, textColorRes, iconRes) = when (typeToast) {
		TypeToast.SUCCESS -> Triple(
			if (theme == ToastTheme.SOLID) R.color.toast_success_solid else R.color.toast_success_bg,
			if (theme == ToastTheme.SOLID) R.color.white else R.color.toast_success_text,
			R.drawable.icon_check
		)

		TypeToast.ERROR -> Triple(
			if (theme == ToastTheme.SOLID) R.color.toast_error_solid else R.color.toast_error_bg,
			if (theme == ToastTheme.SOLID) R.color.white else R.color.toast_error_text,
			R.drawable.icon_close
		)

		TypeToast.WARNING -> Triple(
			if (theme == ToastTheme.SOLID) R.color.toast_warning_solid else R.color.toast_warning_bg,
			if (theme == ToastTheme.SOLID) R.color.white else R.color.toast_warning_text,
			R.drawable.icon_warning
		)

		TypeToast.NONE -> Triple(
			if (theme == ToastTheme.SOLID) R.color.toast_info_solid else R.color.toast_info_bg,
			if (theme == ToastTheme.SOLID) R.color.white else R.color.toast_info_text,
			null
		)
	}

	val bgDrawable = ContextCompat.getDrawable(this, R.drawable.bg_toast_none)?.mutate()
	bgDrawable?.setTint(ContextCompat.getColor(this, bgColorRes))
	layoutT.background = bgDrawable

	val textColor = ContextCompat.getColor(this, textColorRes)
	messageT.setTextColor(textColor)

	if (iconRes != null) {
		imageT.setImageResource(iconRes)
		imageT.setColorFilter(textColor)
	} else {
		imageT.gone()
		val hPadding = (24 * resources.displayMetrics.density).toInt()
		val vPadding = (14 * resources.displayMetrics.density).toInt()
		layoutT.setPadding(hPadding, vPadding, hPadding, vPadding)
	}

	messageT.text = message

	val params = FrameLayout.LayoutParams(
		FrameLayout.LayoutParams.WRAP_CONTENT,
		FrameLayout.LayoutParams.WRAP_CONTENT
	).apply {
		gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
		bottomMargin = (resources.displayMetrics.density * 64).toInt()
	}

	rootView.addView(toastView, params)
	currentToastView = WeakReference(toastView)

	// Hiệu ứng Fade In
	toastView.alpha = 0f
	toastView.animate()
		.alpha(1f)
		.setDuration(300)
		.start()

	toastHandler.postDelayed({
		if (!isFinishing && !isDestroyed && toastView.parent != null) {
			toastView.animate()
				.alpha(0f)
				.setDuration(300)
				.withEndAction {
					(toastView.parent as? ViewGroup)?.removeView(toastView)
					if (currentToastView?.get() == toastView) {
						currentToastView = null
					}
				}
				.start()
		}
	}, duration)
}

/**
 * Các hàm tiện ích để gọi nhanh
 */
fun Activity.showSuccess(
	message: String,
	duration: Long = Duration.SHORT,
	style: ToastStyle = ToastStyle.VERTICAL,
	theme: ToastTheme = ToastTheme.SOFT,
) {
	showCustomOverlayToast(TypeToast.SUCCESS, message, duration, style, theme)
}

fun Activity.showError(
	message: String,
	duration: Long = Duration.SHORT,
	style: ToastStyle = ToastStyle.VERTICAL,
	theme: ToastTheme = ToastTheme.SOFT,
) {
	showCustomOverlayToast(TypeToast.ERROR, message, duration, style, theme)
}

fun Activity.showWarning(
	message: String,
	duration: Long = Duration.SHORT,
	style: ToastStyle = ToastStyle.VERTICAL,
	theme: ToastTheme = ToastTheme.SOFT,
) {
	showCustomOverlayToast(TypeToast.WARNING, message, duration, style, theme)
}

fun Activity.showNone(
	message: String,
	duration: Long = Duration.SHORT,
	style: ToastStyle = ToastStyle.VERTICAL,
	theme: ToastTheme = ToastTheme.SOFT,
) {
	showCustomOverlayToast(TypeToast.NONE, message, duration, style, theme)
}
