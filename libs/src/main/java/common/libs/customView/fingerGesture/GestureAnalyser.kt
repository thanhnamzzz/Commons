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
	private var swipeSlopeIntolerance: Int = 3,
	private val doubleTapMaxDelayMillis: Long = 500L,
	private val doubleTapMaxDownMillis: Long = 100L
) {
	private val initialX = DoubleArray(MAX_FINGERS)
	private val initialY = DoubleArray(MAX_FINGERS)
	private val finalX = DoubleArray(MAX_FINGERS)
	private val finalY = DoubleArray(MAX_FINGERS)
	private val currentX = DoubleArray(MAX_FINGERS)
	private val currentY = DoubleArray(MAX_FINGERS)
	private val delX = DoubleArray(MAX_FINGERS)
	private val delY = DoubleArray(MAX_FINGERS)

	private var numFingers = 0
	private var initialT: Long = 0
	private var finalT: Long = 0
	private var currentT: Long = 0

	private var prevInitialT: Long = 0
	private var prevFinalT: Long = 0

	fun trackGesture(ev: MotionEvent) {
		val n = minOf(ev.pointerCount, MAX_FINGERS)
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
		val n = minOf(ev.pointerCount, numFingers)

		for (i in 0 until n) {
			finalX[i] = ev.getX(i).toDouble()
			finalY[i] = ev.getY(i).toDouble()
			delX[i] = finalX[i] - initialX[i]
			delY[i] = finalY[i] - initialY[i]

			averageDistance += sqrt(delX[i].pow(2.0) + delY[i].pow(2.0))
		}

		if (n > 0) averageDistance /= n.toDouble()

		finalT = SystemClock.uptimeMillis()
		return GestureType().apply {
			gestureFlag = calcGesture()
			gestureDuration = finalT - initialT
			gestureDistance = averageDistance
		}
	}

	fun getOngoingGesture(ev: MotionEvent): Int {
		val n = minOf(ev.pointerCount, numFingers)
		for (i in 0 until n) {
			currentX[i] = ev.getX(i).toDouble()
			currentY[i] = ev.getY(i).toDouble()
			delX[i] = currentX[i] - initialX[i]
			delY[i] = currentY[i] - initialY[i]
		}
		currentT = SystemClock.uptimeMillis()
		return calcGesture()
	}

	private fun calcGesture(): Int {
		if (isDoubleTap) {
			return DOUBLE_TAP_1
		}

		return when (numFingers) {
			1 -> calcSingleFingerGesture()
			2 -> calcTwoFingerGesture()
			3 -> calcThreeFingerGesture()
			4 -> calcFourFingerGesture()
			else -> 0
		}
	}

	private fun calcSingleFingerGesture(): Int {
		val dx = delX[0]
		val dy = delY[0]
		val absDx = abs(dx)
		val absDy = abs(dy)

		return when {
			-dy > swipeSlopeIntolerance * absDx -> SWIPE_1_UP
			dy > swipeSlopeIntolerance * absDx -> SWIPE_1_DOWN
			-dx > swipeSlopeIntolerance * absDy -> SWIPE_1_LEFT
			dx > swipeSlopeIntolerance * absDy -> SWIPE_1_RIGHT
			else -> 0
		}
	}

	private fun calcTwoFingerGesture(): Int {
		val dx0 = delX[0];
		val dy0 = delY[0]
		val dx1 = delX[1];
		val dy1 = delY[1]

		val isUp =
			-dy0 > swipeSlopeIntolerance * abs(dx0) && -dy1 > swipeSlopeIntolerance * abs(dx1)
		val isDown =
			dy0 > swipeSlopeIntolerance * abs(dx0) && dy1 > swipeSlopeIntolerance * abs(dx1)
		val isLeft =
			-dx0 > swipeSlopeIntolerance * abs(dy0) && -dx1 > swipeSlopeIntolerance * abs(dy1)
		val isRight =
			dx0 > swipeSlopeIntolerance * abs(dy0) && dx1 > swipeSlopeIntolerance * abs(dy1)

		return when {
			isUp -> SWIPE_2_UP
			isDown -> SWIPE_2_DOWN
			isLeft -> SWIPE_2_LEFT
			isRight -> SWIPE_2_RIGHT
			else -> {
				val currentDist = finalFingerDist(0, 1)
				val initialDist = initialFingerDist(0, 1)
				when {
					currentDist > PINCH_THRESHOLD_2_UN * initialDist -> UN_PINCH_2
					currentDist < PINCH_THRESHOLD_2_PINCH * initialDist -> PINCH_2
					else -> 0
				}
			}
		}
	}

	private fun calcThreeFingerGesture(): Int {
		val isUp = (0..2).all { -delY[it] > swipeSlopeIntolerance * abs(delX[it]) }
		val isDown = (0..2).all { delY[it] > swipeSlopeIntolerance * abs(delX[it]) }
		val isLeft = (0..2).all { -delX[it] > swipeSlopeIntolerance * abs(delY[it]) }
		val isRight = (0..2).all { delX[it] > swipeSlopeIntolerance * abs(delY[it]) }

		return when {
			isUp -> SWIPE_3_UP
			isDown -> SWIPE_3_DOWN
			isLeft -> SWIPE_3_LEFT
			isRight -> SWIPE_3_RIGHT
			else -> {
				val d01 = finalFingerDist(0, 1) > 1.75 * initialFingerDist(0, 1)
				val d12 = finalFingerDist(1, 2) > 1.75 * initialFingerDist(1, 2)
				val d20 = finalFingerDist(2, 0) > 1.75 * initialFingerDist(2, 0)
				if (d01 && d12 && d20) return UN_PINCH_3

				val p01 = finalFingerDist(0, 1) < 0.66 * initialFingerDist(0, 1)
				val p12 = finalFingerDist(1, 2) < 0.66 * initialFingerDist(1, 2)
				val p20 = finalFingerDist(2, 0) < 0.66 * initialFingerDist(2, 0)
				if (p01 && p12 && p20) return PINCH_3
				0
			}
		}
	}

	private fun calcFourFingerGesture(): Int {
		val isUp = (0..3).all { -delY[it] > swipeSlopeIntolerance * abs(delX[it]) }
		val isDown = (0..3).all { delY[it] > swipeSlopeIntolerance * abs(delX[it]) }
		val isLeft = (0..3).all { -delX[it] > swipeSlopeIntolerance * abs(delY[it]) }
		val isRight = (0..3).all { delX[it] > swipeSlopeIntolerance * abs(delY[it]) }

		return when {
			isUp -> SWIPE_4_UP
			isDown -> SWIPE_4_DOWN
			isLeft -> SWIPE_4_LEFT
			isRight -> SWIPE_4_RIGHT
			else -> {
				val unpinch = (0..3).all {
					finalFingerDist(it, (it + 1) % 4) > 1.5 * initialFingerDist(
						it,
						(it + 1) % 4
					)
				}
				if (unpinch) return UN_PINCH_4
				val pinch = (0..3).all {
					finalFingerDist(it, (it + 1) % 4) < 0.8 * initialFingerDist(
						it,
						(it + 1) % 4
					)
				}
				if (pinch) return PINCH_4
				0
			}
		}
	}

	private fun initialFingerDist(f1: Int, f2: Int): Double =
		sqrt((initialX[f1] - initialX[f2]).pow(2.0) + (initialY[f1] - initialY[f2]).pow(2.0))

	private fun finalFingerDist(f1: Int, f2: Int): Double =
		sqrt((finalX[f1] - finalX[f2]).pow(2.0) + (finalY[f1] - finalY[f2]).pow(2.0))

	val isDoubleTap: Boolean
		get() = (initialT - prevFinalT) < doubleTapMaxDelayMillis &&
				(finalT - initialT) < doubleTapMaxDownMillis &&
				(prevFinalT - prevInitialT) < doubleTapMaxDownMillis

	inner class GestureType {
		var gestureFlag: Int = 0
		var gestureDuration: Long = 0
		var gestureDistance: Double = 0.0
	}

	companion object {
		private const val MAX_FINGERS = 5
		private const val PINCH_THRESHOLD_2_UN = 2.0
		private const val PINCH_THRESHOLD_2_PINCH = 0.5

		const val SWIPE_1_UP = 11;
		const val SWIPE_1_DOWN = 12
		const val SWIPE_1_LEFT = 13;
		const val SWIPE_1_RIGHT = 14
		const val SWIPE_2_UP = 21;
		const val SWIPE_2_DOWN = 22
		const val SWIPE_2_LEFT = 23;
		const val SWIPE_2_RIGHT = 24
		const val SWIPE_3_UP = 31;
		const val SWIPE_3_DOWN = 32
		const val SWIPE_3_LEFT = 33;
		const val SWIPE_3_RIGHT = 34
		const val SWIPE_4_UP = 41;
		const val SWIPE_4_DOWN = 42
		const val SWIPE_4_LEFT = 43;
		const val SWIPE_4_RIGHT = 44
		const val PINCH_2 = 25;
		const val UN_PINCH_2 = 26
		const val PINCH_3 = 35;
		const val UN_PINCH_3 = 36
		const val PINCH_4 = 45;
		const val UN_PINCH_4 = 46
		const val DOUBLE_TAP_1 = 107
	}
}