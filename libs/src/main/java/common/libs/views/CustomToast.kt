package common.libs.views

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
	duration: Long = Duration.SHORT
) {
	if (isFinishing || isDestroyed) return
	val rootView = findViewById<ViewGroup>(android.R.id.content) ?: return

	currentToastView?.get()?.let { oldView ->
		(oldView.parent as? ViewGroup)?.removeView(oldView)
		toastHandler.removeCallbacksAndMessages(null)
	}

	val inflater = LayoutInflater.from(this)
	val toastView = try {
		inflater.inflate(R.layout.layout_my_toast, rootView, false)
	} catch (_: Exception) {
		return
	}

	val layoutT = toastView.findViewById<LinearLayout>(R.id.layout_toast)
	val messageT = toastView.findViewById<TextView>(R.id.message_toast)
	val imageT = toastView.findViewById<ImageView>(R.id.image_toast)

	when (typeToast) {
		TypeToast.SUCCESS -> {
			layoutT.setBackgroundResource(R.drawable.bg_toast_success)
			imageT.setImageResource(R.drawable.icon_check)
		}
		TypeToast.ERROR -> {
			layoutT.setBackgroundResource(R.drawable.bg_toast_error)
			imageT.setImageResource(R.drawable.icon_close)
		}
		TypeToast.WARNING -> {
			layoutT.setBackgroundResource(R.drawable.bg_toast_warning)
			imageT.setImageResource(R.drawable.icon_warning)
		}
		TypeToast.NONE -> {
			layoutT.setBackgroundResource(R.drawable.bg_toast_none)
			imageT.gone()
			layoutT.setPadding(80, 30, 80, 30)
		}
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

	val fadeIn = AlphaAnimation(0f, 1f).apply {
		this.duration = 300
		fillAfter = true
	}
	toastView.startAnimation(fadeIn)

	toastHandler.postDelayed({
		if (!isFinishing && !isDestroyed && toastView.parent != null) {
			val fadeOut = AlphaAnimation(1f, 0f).apply {
				this.duration = 300
				fillAfter = true
				setAnimationListener(object : Animation.AnimationListener {
					override fun onAnimationEnd(animation: Animation?) {
						(toastView.parent as? ViewGroup)?.removeView(toastView)
						if (currentToastView?.get() == toastView) {
							currentToastView = null
						}
					}
					override fun onAnimationStart(animation: Animation?) {}
					override fun onAnimationRepeat(animation: Animation?) {}
				})
			}
			toastView.startAnimation(fadeOut)
		}
	}, duration)
}

fun Activity.showSuccess(message: String, duration: Long = Duration.SHORT) {
	showCustomOverlayToast(TypeToast.SUCCESS, message, duration)
}

fun Activity.showError(message: String, duration: Long = Duration.SHORT) {
	showCustomOverlayToast(TypeToast.ERROR, message, duration)
}

fun Activity.showWarning(message: String, duration: Long = Duration.SHORT) {
	showCustomOverlayToast(TypeToast.WARNING, message, duration)
}

fun Activity.showNone(message: String, duration: Long = Duration.SHORT) {
	showCustomOverlayToast(TypeToast.NONE, message, duration)
}
