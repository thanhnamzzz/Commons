package common.libs.functions

import androidx.annotation.IntRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Kiểm tra điều kiện định kỳ cho đến khi nó thỏa mãn.
 *
 * @param checkCondition Hàm lambda trả về Boolean: true nếu điều kiện thỏa mãn.
 * @param onConditionMet Hàm lambda sẽ được thực thi khi điều kiện thỏa mãn.
 * @param intervalMs Khoảng thời gian chờ giữa các lần kiểm tra (mặc định 1000ms = 1s).
 */
fun CoroutineScope.checkConditionLoop(
	checkCondition: () -> Boolean,
	onConditionMet: () -> Unit,
	intervalMs: Long = 1000L
) {
	launch(Dispatchers.IO) {
		while (!checkCondition()) {
			delay(intervalMs)
		}
		withContext(Dispatchers.Main) {
			onConditionMet()
		}
	}
}

/**
 * Phiên bản của [checkConditionLoop] trả về Job để có thể hủy thủ công.
 */
fun CoroutineScope.checkConditionLoopJob(
	checkCondition: () -> Boolean,
	onConditionMet: () -> Unit,
	intervalMs: Long = 1000L
): Job {
	return launch(Dispatchers.IO) {
		while (isActive && !checkCondition()) {
			delay(intervalMs)
		}
		if (isActive) {
			withContext(Dispatchers.Main) {
				onConditionMet()
			}
		}
	}
}

/**
 * Kiểm tra điều kiện định kỳ trong một khoảng thời gian tối đa.
 * @param checkCondition Hàm lambda trả về Boolean: true nếu điều kiện thỏa mãn.
 * @param onConditionMet Hàm lambda sẽ được thực thi sau khi vòng lặp dừng (dù là do đạt điều kiện hay hết giờ).
 * @param timeoutMs Thời gian tối đa cho phép kiểm tra (tính bằng mili giây). Không được là số âm.
 * @param intervalMs Khoảng thời gian chờ giữa các lần kiểm tra (mặc định 1000ms = 1s).
 */
fun CoroutineScope.checkConditionLoopWithTimeout(
	checkCondition: () -> Boolean,
	onConditionMet: (Boolean) -> Unit,
	@IntRange(from = 0) timeoutMs: Long,
	intervalMs: Long = 1000L
) {
	require(timeoutMs >= 0) { "timeoutMs must not be negative" }
	launch(Dispatchers.IO) {
		val result: Boolean = withTimeoutOrNull(timeoutMs) {
			while (!checkCondition()) {
				delay(intervalMs)
			}
			return@withTimeoutOrNull true
		} ?: false

		withContext(Dispatchers.Main) {
			onConditionMet(result)
		}
	}
}

/**
 * Phiên bản của [checkConditionLoopWithTimeout] trả về Job để có thể hủy thủ công.
 */
fun CoroutineScope.checkConditionLoopWithTimeoutJob(
	checkCondition: () -> Boolean,
	onConditionMet: (Boolean) -> Unit,
	@IntRange(from = 0) timeoutMs: Long,
	intervalMs: Long = 1000L
): Job {
	require(timeoutMs >= 0) { "timeoutMs must not be negative" }
	return launch(Dispatchers.IO) {
		val result: Boolean = withTimeoutOrNull(timeoutMs) {
			while (isActive && !checkCondition()) {
				delay(intervalMs)
			}
			isActive
		} ?: false

		if (isActive) {
			withContext(Dispatchers.Main) {
				onConditionMet(result)
			}
		}
	}
}