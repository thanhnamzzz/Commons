package common.libs.customView.fingerGesture

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import common.libs.customView.fingerGesture.GestureAnalyser.GestureType

/**
 * @author championswimmer
 * @version 0.2
 * @since 0.1 12/04/14
 *
 *
 * 25/12/24
 * HOW TO USE:
 *
 * Declare a FingerGestures object, call its setOnFingerGestureListener function, and define an interface to return the result.
 * val fingerGestures = FingerGestures()
 * fingerGestures.setOnFingerGestureListener(object : FingerGestures.OnFingerGestureListener{})
 *
 * Finally, pass the declared FingerGestures object to the setOnTouchListener event of the View that needs to handle the event.
 * view.setOnTouchListener(fingerGestures)
 *
 */
class FingerGestures : OnTouchListener {
	private var debug = false
	var consumeTouchEvents: Boolean = false

	private var tracking: BooleanArray = booleanArrayOf(false, false, false, false, false)
	private var ga: GestureAnalyser
	private var onFingerGestureListener: OnFingerGestureListener? = null

	constructor() {
		ga = GestureAnalyser()
	}

	constructor(
		swipeSlopeIntolerance: Int,
		doubleTapMaxDelayMillis: Int,
		doubleTapMaxDownMillis: Int
	) {
		ga = GestureAnalyser(swipeSlopeIntolerance, doubleTapMaxDelayMillis, doubleTapMaxDownMillis)
	}

	fun setDebug(debug: Boolean) {
		this.debug = debug
	}

	fun setOnFingerGestureListener(listener: OnFingerGestureListener?) {
		onFingerGestureListener = listener
	}


	override fun onTouch(view: View, ev: MotionEvent): Boolean {
		view.performClick()
		if (debug) Log.d(TAG, "onTouch ${ev.actionMasked}")
		when (ev.action and MotionEvent.ACTION_MASK) {
			MotionEvent.ACTION_DOWN -> {
				startTracking(0)
				ga.trackGesture(ev)
				return consumeTouchEvents
			}

			MotionEvent.ACTION_UP -> {
				if (tracking[0]) {
					doCallBack(ga.getGesture(ev))
				}
				stopTracking(0)
				ga.unTrackGesture()
				return consumeTouchEvents
			}

			MotionEvent.ACTION_POINTER_DOWN -> {
				if (debug) Log.d(TAG, "ACTION_POINTER_DOWN" + " " + "num" + ev.pointerCount)
				startTracking(ev.pointerCount - 1)
				ga.trackGesture(ev)
				return consumeTouchEvents
			}

			MotionEvent.ACTION_POINTER_UP -> {
				if (debug) Log.d(TAG, "ACTION_POINTER_UP" + " " + "num" + ev.pointerCount)
				if (tracking[1]) {
					doCallBack(ga.getGesture(ev))
				}
				stopTracking(ev.pointerCount - 1)
				ga.unTrackGesture()
				return consumeTouchEvents
			}

			MotionEvent.ACTION_CANCEL -> {
				return true
			}

			MotionEvent.ACTION_MOVE -> {
				return consumeTouchEvents
			}
		}
		return consumeTouchEvents
	}

	private fun doCallBack(mGt: GestureType) {
		when (mGt.gestureFlag) {
			GestureAnalyser.Companion.SWIPE_1_UP -> onFingerGestureListener!!.onSwipeUp(
				1,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_1_DOWN -> onFingerGestureListener!!.onSwipeDown(
				1,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_1_LEFT -> onFingerGestureListener!!.onSwipeLeft(
				1,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_1_RIGHT -> onFingerGestureListener!!.onSwipeRight(
				1,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_2_UP -> onFingerGestureListener!!.onSwipeUp(
				2,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_2_DOWN -> onFingerGestureListener!!.onSwipeDown(
				2,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_2_LEFT -> onFingerGestureListener!!.onSwipeLeft(
				2,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_2_RIGHT -> onFingerGestureListener!!.onSwipeRight(
				2,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.PINCH_2 -> onFingerGestureListener!!.onPinch(
				2,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.UN_PINCH_2 -> onFingerGestureListener!!.onUnPinch(
				2,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_3_UP -> onFingerGestureListener!!.onSwipeUp(
				3,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_3_DOWN -> onFingerGestureListener!!.onSwipeDown(
				3,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_3_LEFT -> onFingerGestureListener!!.onSwipeLeft(
				3,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_3_RIGHT -> onFingerGestureListener!!.onSwipeRight(
				3,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.PINCH_3 -> onFingerGestureListener!!.onPinch(
				3,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.UN_PINCH_3 -> onFingerGestureListener!!.onUnPinch(
				3,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_4_UP -> onFingerGestureListener!!.onSwipeUp(
				4,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_4_DOWN -> onFingerGestureListener!!.onSwipeDown(
				4,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_4_LEFT -> onFingerGestureListener!!.onSwipeLeft(
				4,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.SWIPE_4_RIGHT -> onFingerGestureListener!!.onSwipeRight(
				4,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.PINCH_4 -> onFingerGestureListener!!.onPinch(
				4,
				mGt.gestureDuration,
				mGt.gestureDistance
			)

			GestureAnalyser.Companion.UN_PINCH_4 -> {
				onFingerGestureListener!!.onUnPinch(4, mGt.gestureDuration, mGt.gestureDistance)
				onFingerGestureListener!!.onDoubleTap(1)
			}

			GestureAnalyser.Companion.DOUBLE_TAP_1 -> onFingerGestureListener!!.onDoubleTap(1)
		}
	}

	private fun startTracking(nthPointer: Int) {
		for (i in 0..nthPointer) {
			tracking[i] = true
		}
	}

	private fun stopTracking(nthPointer: Int) {
		for (i in nthPointer until tracking.size) {
			tracking[i] = false
		}
	}


	/**
	 * Interface definition for the callback to be invoked when 2-finger gestures are performed
	 */
	interface OnFingerGestureListener {
		/**
		 * Called when user swipes **up** with two fingers
		 *
		 * @param fingers         number of fingers involved in this gesture
		 * @param gestureDuration duration in milliSeconds
		 * @return
		 */
		fun onSwipeUp(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean

		/**
		 * Called when user swipes **down** with two fingers
		 *
		 * @param fingers         number of fingers involved in this gesture
		 * @param gestureDuration duration in milliSeconds
		 * @return
		 */
		fun onSwipeDown(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean

		/**
		 * Called when user swipes **left** with two fingers
		 *
		 * @param fingers         number of fingers involved in this gesture
		 * @param gestureDuration duration in milliSeconds
		 * @return
		 */
		fun onSwipeLeft(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean

		/**
		 * Called when user swipes **right** with two fingers
		 *
		 * @param fingers         number of fingers involved in this gesture
		 * @param gestureDuration duration in milliSeconds
		 * @return
		 */
		fun onSwipeRight(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean

		/**
		 * Called when user **pinches** with two fingers (bring together)
		 *
		 * @param fingers         number of fingers involved in this gesture
		 * @param gestureDuration duration in milliSeconds
		 * @return
		 */
		fun onPinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean

		/**
		 * Called when user **un-pinches** with two fingers (take apart)
		 *
		 * @param fingers         number of fingers involved in this gesture
		 * @param gestureDuration duration in milliSeconds
		 * @return
		 */
		fun onUnPinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean

		fun onDoubleTap(fingers: Int): Boolean
	}

	companion object {
		// Will see if these need to be used. For now just returning duration in milliS
		const val GESTURE_SPEED_SLOW: Long = 1500
		const val GESTURE_SPEED_MEDIUM: Long = 1000
		const val GESTURE_SPEED_FAST: Long = 500

		private const val TAG = "Namzzz"
	}
}
