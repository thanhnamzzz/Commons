package common.commons.fingerGesture

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import common.commons.databinding.ActivityFingerGestureBinding
import common.libs.SimpleActivity
import common.libs.customView.fingerGesture.FingerGestures
import common.libs.extensions.toastMess
import common.libs.views.TypeToast

class FingerGestureActivity : SimpleActivity<ActivityFingerGestureBinding>(
	ActivityFingerGestureBinding::inflate
), FingerGestures.OnFingerGestureListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

		setupFingerGestures()
	}

	private fun setupFingerGestures() {
		val fingerGestures = FingerGestures().apply {
			consumeTouchEvents = true
		}
		fingerGestures.setOnFingerGestureListener(this)
		binding.gestureBox.setOnTouchListener(fingerGestures)
	}

	override fun onSwipeUp(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
		updateLog("Swipe Up ($fingers fingers)")
		return true
	}

	override fun onSwipeDown(
		fingers: Int,
		gestureDuration: Long,
		gestureDistance: Double
	): Boolean {
		updateLog("Swipe Down ($fingers fingers)")
		return true
	}

	override fun onSwipeLeft(
		fingers: Int,
		gestureDuration: Long,
		gestureDistance: Double
	): Boolean {
		updateLog("Swipe Left ($fingers fingers)")
		return true
	}

	override fun onSwipeRight(
		fingers: Int,
		gestureDuration: Long,
		gestureDistance: Double
	): Boolean {
		updateLog("Swipe Right ($fingers fingers)")
		return true
	}

	override fun onPinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
		updateLog("Pinch ($fingers fingers)")
		return true
	}

	override fun onUnPinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
		updateLog("UnPinch ($fingers fingers)")
		return true
	}

	override fun onDoubleTap(fingers: Int): Boolean {
		updateLog("Double Tap ($fingers fingers)")
		toastMess("Double Tap!", TypeToast.SUCCESS)
		return true
	}

	private fun updateLog(message: String) {
		binding.tvGestureLog.text = "Detected: $message"
	}
}