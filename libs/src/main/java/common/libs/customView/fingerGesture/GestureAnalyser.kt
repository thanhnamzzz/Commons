package common.libs.customView.fingerGesture

import android.os.SystemClock
import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Internal API class to analyse the recorded gestures.
 *
 * @author championswimmer
 * @version 0.2
 * @since 0.1 12/04/14
 */
class GestureAnalyser @JvmOverloads constructor(
	swipeSlopeIntolerance: Int = 3,
	doubleTapMaxDelayMillis: Int = 500,
	doubleTapMaxDownMillis: Int = 100
) {
	private val initialX = DoubleArray(5)
	private val initialY = DoubleArray(5)
	private val finalX = DoubleArray(5)
	private val finalY = DoubleArray(5)
	private val currentX = DoubleArray(5)
	private val currentY = DoubleArray(5)
	private val delX = DoubleArray(5)
	private val delY = DoubleArray(5)

	private var numFingers = 0
	private var initialT: Long = 0
	private var finalT: Long = 0
	private var currentT: Long = 0

	private var prevInitialT: Long = 0
	private var prevFinalT: Long = 0

	private var swipeSlopeIntolerance = 3

	private val doubleTapMaxDelayMillis: Long
	private val doubleTapMaxDownMillis: Long

	init {
		this.swipeSlopeIntolerance = swipeSlopeIntolerance
		this.doubleTapMaxDownMillis = doubleTapMaxDownMillis.toLong()
		this.doubleTapMaxDelayMillis = doubleTapMaxDelayMillis.toLong()
	}

	fun trackGesture(ev: MotionEvent) {
		val n = ev.pointerCount
		for (i in 0 until n) {
			initialX[i] = ev.getX(i).toDouble()
			initialY[i] = ev.getY(i).toDouble()
		}
		numFingers = n
		initialT = SystemClock.uptimeMillis()
	}

	fun unTrackGesture() {
		numFingers = 0
		prevFinalT = SystemClock.uptimeMillis()
		prevInitialT = initialT
	}

	fun getGesture(ev: MotionEvent): GestureType {
		var averageDistance = 0.0
		for (i in 0 until numFingers) {
			finalX[i] = ev.getX(i).toDouble()
			finalY[i] = ev.getY(i).toDouble()
			delX[i] = finalX[i] - initialX[i]
			delY[i] = finalY[i] - initialY[i]

			averageDistance += sqrt(
				(finalX[i] - initialX[i]).pow(2.0) + (finalY[i] - initialY[i]).pow(
					2.0
				)
			)
		}
		averageDistance /= numFingers.toDouble()

		finalT = SystemClock.uptimeMillis()
		val gt = GestureType()
		gt.gestureFlag = calcGesture()
		gt.gestureDuration = finalT - initialT
		gt.gestureDistance = averageDistance
		return gt
	}

	fun getOngoingGesture(ev: MotionEvent): Int {
		for (i in 0 until numFingers) {
			currentX[i] = ev.getX(i).toDouble()
			currentY[i] = ev.getY(i).toDouble()
			delX[i] = finalX[i] - initialX[i]
			delY[i] = finalY[i] - initialY[i]
		}
		currentT = SystemClock.uptimeMillis()
		return calcGesture()
	}

	private fun calcGesture(): Int {
		if (isDoubleTap) {
			return DOUBLE_TAP_1
		}

		if (numFingers == 1) {
			if ((-(delY[0])) > (swipeSlopeIntolerance * (abs(delX[0])))) {
				return SWIPE_1_UP
			}

			if (((delY[0])) > (swipeSlopeIntolerance * (abs(delX[0])))) {
				return SWIPE_1_DOWN
			}

			if ((-(delX[0])) > (swipeSlopeIntolerance * (abs(delY[0])))) {
				return SWIPE_1_LEFT
			}

			if (((delX[0])) > (swipeSlopeIntolerance * (abs(delY[0])))) {
				return SWIPE_1_RIGHT
			}
		}
		if (numFingers == 2) {
			if (((-delY[0]) > (swipeSlopeIntolerance * abs(delX[0]))) && ((-delY[1]) > (swipeSlopeIntolerance * abs(
					delX[1]
				)))
			) {
				return SWIPE_2_UP
			}
			if (((delY[0]) > (swipeSlopeIntolerance * abs(delX[0]))) && ((delY[1]) > (swipeSlopeIntolerance * abs(
					delX[1]
				)))
			) {
				return SWIPE_2_DOWN
			}
			if (((-delX[0]) > (swipeSlopeIntolerance * abs(delY[0]))) && ((-delX[1]) > (swipeSlopeIntolerance * abs(
					delY[1]
				)))
			) {
				return SWIPE_2_LEFT
			}
			if (((delX[0]) > (swipeSlopeIntolerance * abs(delY[0]))) && ((delX[1]) > (swipeSlopeIntolerance * abs(
					delY[1]
				)))
			) {
				return SWIPE_2_RIGHT
			}
			if (finalFingerDist(0, 1) > 2 * (initialFingerDist(0, 1))) {
				return UN_PINCH_2
			}
			if (finalFingerDist(0, 1) < 0.5 * (initialFingerDist(0, 1))) {
				return PINCH_2
			}
		}
		if (numFingers == 3) {
			if (((-delY[0]) > (swipeSlopeIntolerance * abs(delX[0])))
				&& ((-delY[1]) > (swipeSlopeIntolerance * abs(delX[1])))
				&& ((-delY[2]) > (swipeSlopeIntolerance * abs(delX[2])))
			) {
				return SWIPE_3_UP
			}
			if (((delY[0]) > (swipeSlopeIntolerance * abs(delX[0])))
				&& ((delY[1]) > (swipeSlopeIntolerance * abs(delX[1])))
				&& ((delY[2]) > (swipeSlopeIntolerance * abs(delX[2])))
			) {
				return SWIPE_3_DOWN
			}
			if (((-delX[0]) > (swipeSlopeIntolerance * abs(delY[0])))
				&& ((-delX[1]) > (swipeSlopeIntolerance * abs(delY[1])))
				&& ((-delX[2]) > (swipeSlopeIntolerance * abs(delY[2])))
			) {
				return SWIPE_3_LEFT
			}
			if (((delX[0]) > (swipeSlopeIntolerance * abs(delY[0])))
				&& ((delX[1]) > (swipeSlopeIntolerance * abs(delY[1])))
				&& ((delX[2]) > (swipeSlopeIntolerance * abs(delY[2])))
			) {
				return SWIPE_3_RIGHT
			}

			if ((finalFingerDist(0, 1) > 1.75 * (initialFingerDist(0, 1)))
				&& (finalFingerDist(1, 2) > 1.75 * (initialFingerDist(1, 2)))
				&& (finalFingerDist(2, 0) > 1.75 * (initialFingerDist(2, 0)))
			) {
				return UN_PINCH_3
			}
			if ((finalFingerDist(0, 1) < 0.66 * (initialFingerDist(0, 1)))
				&& (finalFingerDist(1, 2) < 0.66 * (initialFingerDist(1, 2)))
				&& (finalFingerDist(2, 0) < 0.66 * (initialFingerDist(2, 0)))
			) {
				return PINCH_3
			}
		}
		if (numFingers == 4) {
			if (((-delY[0]) > (swipeSlopeIntolerance * abs(delX[0])))
				&& ((-delY[1]) > (swipeSlopeIntolerance * abs(delX[1])))
				&& ((-delY[2]) > (swipeSlopeIntolerance * abs(delX[2])))
				&& ((-delY[3]) > (swipeSlopeIntolerance * abs(delX[3])))
			) {
				return SWIPE_4_UP
			}
			if (((delY[0]) > (swipeSlopeIntolerance * abs(delX[0])))
				&& ((delY[1]) > (swipeSlopeIntolerance * abs(delX[1])))
				&& ((delY[2]) > (swipeSlopeIntolerance * abs(delX[2])))
				&& ((delY[3]) > (swipeSlopeIntolerance * abs(delX[3])))
			) {
				return SWIPE_4_DOWN
			}
			if (((-delX[0]) > (swipeSlopeIntolerance * abs(delY[0])))
				&& ((-delX[1]) > (swipeSlopeIntolerance * abs(delY[1])))
				&& ((-delX[2]) > (swipeSlopeIntolerance * abs(delY[2])))
				&& ((-delX[3]) > (swipeSlopeIntolerance * abs(delY[3])))
			) {
				return SWIPE_4_LEFT
			}
			if (((delX[0]) > (swipeSlopeIntolerance * abs(delY[0])))
				&& ((delX[1]) > (swipeSlopeIntolerance * abs(delY[1])))
				&& ((delX[2]) > (swipeSlopeIntolerance * abs(delY[2])))
				&& ((delX[3]) > (swipeSlopeIntolerance * abs(delY[3])))
			) {
				return SWIPE_4_RIGHT
			}
			if ((finalFingerDist(0, 1) > 1.5 * (initialFingerDist(0, 1)))
				&& (finalFingerDist(1, 2) > 1.5 * (initialFingerDist(1, 2)))
				&& (finalFingerDist(2, 3) > 1.5 * (initialFingerDist(2, 3)))
				&& (finalFingerDist(3, 0) > 1.5 * (initialFingerDist(3, 0)))
			) {
				return UN_PINCH_4
			}
			if ((finalFingerDist(0, 1) < 0.8 * (initialFingerDist(0, 1)))
				&& (finalFingerDist(1, 2) < 0.8 * (initialFingerDist(1, 2)))
				&& (finalFingerDist(2, 3) < 0.8 * (initialFingerDist(2, 3)))
				&& (finalFingerDist(3, 0) < 0.8 * (initialFingerDist(3, 0)))
			) {
				return PINCH_4
			}
		}
		return 0
	}

	private fun initialFingerDist(fingerNum1: Int, fingerNum2: Int): Double {
		return sqrt(
			(initialX[fingerNum1] - initialX[fingerNum2]).pow(2.0) + (initialY[fingerNum1] - initialY[fingerNum2]).pow(
				2.0
			)
		)
	}

	private fun finalFingerDist(fingerNum1: Int, fingerNum2: Int): Double {
		return sqrt(
			(finalX[fingerNum1] - finalX[fingerNum2]).pow(2.0) + (finalY[fingerNum1] - finalY[fingerNum2]).pow(
				2.0
			)
		)
	}

	val isDoubleTap: Boolean
		get() = initialT - prevFinalT < doubleTapMaxDelayMillis && finalT - initialT < doubleTapMaxDownMillis && prevFinalT - prevInitialT < doubleTapMaxDownMillis

	inner class GestureType {
		var gestureFlag: Int = 0
		var gestureDuration: Long = 0

		var gestureDistance: Double = 0.0
	}


	companion object {
		// Finished gestures flags
		const val SWIPE_1_UP: Int = 11
		const val SWIPE_1_DOWN: Int = 12
		const val SWIPE_1_LEFT: Int = 13
		const val SWIPE_1_RIGHT: Int = 14
		const val SWIPE_2_UP: Int = 21
		const val SWIPE_2_DOWN: Int = 22
		const val SWIPE_2_LEFT: Int = 23
		const val SWIPE_2_RIGHT: Int = 24
		const val SWIPE_3_UP: Int = 31
		const val SWIPE_3_DOWN: Int = 32
		const val SWIPE_3_LEFT: Int = 33
		const val SWIPE_3_RIGHT: Int = 34
		const val SWIPE_4_UP: Int = 41
		const val SWIPE_4_DOWN: Int = 42
		const val SWIPE_4_LEFT: Int = 43
		const val SWIPE_4_RIGHT: Int = 44
		const val PINCH_2: Int = 25
		const val UN_PINCH_2: Int = 26
		const val PINCH_3: Int = 35
		const val UN_PINCH_3: Int = 36
		const val PINCH_4: Int = 45
		const val UN_PINCH_4: Int = 46
		const val DOUBLE_TAP_1: Int = 107
	}
}
