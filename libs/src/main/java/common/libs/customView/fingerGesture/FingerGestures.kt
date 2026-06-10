package common.libs.customView.fingerGesture

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import common.libs.customView.fingerGesture.GestureAnalyser.GestureType

/**
 * @author championswimmer
 * @version 0.2
 * @since 0.1 12/04/14
 *
 * 25/12/24
 * HOW TO USE:
 *
 * val fingerGestures = FingerGestures()
 * fingerGestures.setOnFingerGestureListener(object : FingerGestures.OnFingerGestureListener { ... })
 * view.setOnTouchListener(fingerGestures)
 */
class FingerGestures @JvmOverloads constructor(
	swipeSlopeIntolerance: Int = 3,
	doubleTapMaxDelayMillis: Int = 500,
	doubleTapMaxDownMillis: Int = 100
) : OnTouchListener {
	var consumeTouchEvents: Boolean = false

	private val tracking = BooleanArray(5) { false }
	private val ga = GestureAnalyser(
		swipeSlopeIntolerance,
		doubleTapMaxDelayMillis.toLong(),
		doubleTapMaxDownMillis.toLong()
	)
	private var onFingerGestureListener: OnFingerGestureListener? = null

	fun setOnFingerGestureListener(listener: OnFingerGestureListener?) {
		onFingerGestureListener = listener
	}

	override fun onTouch(view: View, ev: MotionEvent): Boolean {
		val action = ev.actionMasked
		val pointerCount = ev.pointerCount

		when (action) {
			MotionEvent.ACTION_DOWN -> {
				startTracking(0)
				ga.trackGesture(ev)
			}

			MotionEvent.ACTION_POINTER_DOWN -> {
				startTracking(pointerCount - 1)
				ga.trackGesture(ev)
			}

			MotionEvent.ACTION_UP -> {
				if (tracking[0]) {
					val gesture = ga.getGesture(ev)
					if (gesture.gestureFlag == 0) {
						view.performClick()
					} else {
						doCallBack(gesture)
					}
				}
				stopTracking(0)
				ga.unTrackGesture()
			}

			MotionEvent.ACTION_POINTER_UP -> {
				if (tracking[minOf(pointerCount - 1, tracking.size - 1)]) {
					doCallBack(ga.getGesture(ev))
				}
				stopTracking(pointerCount - 1)
				ga.unTrackGesture()
			}

			MotionEvent.ACTION_CANCEL -> {
				tracking.fill(false)
				ga.unTrackGesture()
			}

			MotionEvent.ACTION_MOVE -> {
				// Ongoing gesture analysis could be added here
			}
		}
		return consumeTouchEvents
	}

	private fun doCallBack(mGt: GestureType) {
		val listener = onFingerGestureListener ?: return
		val fingers = when (mGt.gestureFlag) {
			in 11..14 -> 1
			in 21..24, 25, 26 -> 2
			in 31..34, 35, 36 -> 3
			in 41..44, 45, 46 -> 4
			else -> 0
		}

		when (mGt.gestureFlag) {
			GestureAnalyser.SWIPE_1_UP, GestureAnalyser.SWIPE_2_UP, GestureAnalyser.SWIPE_3_UP, GestureAnalyser.SWIPE_4_UP ->
				listener.onSwipeUp(fingers, mGt.gestureDuration, mGt.gestureDistance)

			GestureAnalyser.SWIPE_1_DOWN, GestureAnalyser.SWIPE_2_DOWN, GestureAnalyser.SWIPE_3_DOWN, GestureAnalyser.SWIPE_4_DOWN ->
				listener.onSwipeDown(fingers, mGt.gestureDuration, mGt.gestureDistance)

			GestureAnalyser.SWIPE_1_LEFT, GestureAnalyser.SWIPE_2_LEFT, GestureAnalyser.SWIPE_3_LEFT, GestureAnalyser.SWIPE_4_LEFT ->
				listener.onSwipeLeft(fingers, mGt.gestureDuration, mGt.gestureDistance)

			GestureAnalyser.SWIPE_1_RIGHT, GestureAnalyser.SWIPE_2_RIGHT, GestureAnalyser.SWIPE_3_RIGHT, GestureAnalyser.SWIPE_4_RIGHT ->
				listener.onSwipeRight(fingers, mGt.gestureDuration, mGt.gestureDistance)

			GestureAnalyser.PINCH_2, GestureAnalyser.PINCH_3, GestureAnalyser.PINCH_4 ->
				listener.onPinch(fingers, mGt.gestureDuration, mGt.gestureDistance)

			GestureAnalyser.UN_PINCH_2, GestureAnalyser.UN_PINCH_3, GestureAnalyser.UN_PINCH_4 ->
				listener.onUnPinch(fingers, mGt.gestureDuration, mGt.gestureDistance)

			GestureAnalyser.DOUBLE_TAP_1 ->
				listener.onDoubleTap(1)
		}
	}

	private fun startTracking(nthPointer: Int) {
		for (i in 0..minOf(nthPointer, tracking.size - 1)) {
			tracking[i] = true
		}
	}

	private fun stopTracking(nthPointer: Int) {
		for (i in maxOf(0, nthPointer) until tracking.size) {
			tracking[i] = false
		}
	}

	interface OnFingerGestureListener {
		fun onSwipeUp(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean
		fun onSwipeDown(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean
		fun onSwipeLeft(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean
		fun onSwipeRight(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean
		fun onPinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean
		fun onUnPinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean
		fun onDoubleTap(fingers: Int): Boolean
	}
}