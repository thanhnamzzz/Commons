package common.libs.compose.functions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
	launch(Dispatchers.Default) {

		while (!checkCondition()) {
			delay(intervalMs)
		}
		withContext(Dispatchers.Main) {
			onConditionMet()
		}
	}
}
/**
 * Kiểm tra điều kiện định kỳ trong một khoảng thời gian tối đa.
 * * @param checkCondition Hàm lambda trả về Boolean: true nếu điều kiện thỏa mãn.
 * @param onConditionMet Hàm lambda sẽ được thực thi sau khi vòng lặp dừng (dù là do đạt điều kiện hay hết giờ).
 * @param timeoutMs Thời gian tối đa cho phép kiểm tra (tính bằng mili giây).
 * @param intervalMs Khoảng thời gian chờ giữa các lần kiểm tra (mặc định 1000ms = 1s).
 */
fun CoroutineScope.checkConditionLoopWithTimeout(
	checkCondition: () -> Boolean,
	onConditionMet: (Boolean) -> Unit,
	timeoutMs: Long,
	intervalMs: Long = 1000L
) {
	launch(Dispatchers.Default) {
		val result: Boolean? = withTimeoutOrNull(timeoutMs) {
			while (!checkCondition()) {
				delay(intervalMs)
			}
			return@withTimeoutOrNull true

		} ?: false // Nếu withTimeoutOrNull hết giờ, trả về null. gán giá trị false cho biến result.

		withContext(Dispatchers.Main) {
			onConditionMet(result == true)
		}
	}
}