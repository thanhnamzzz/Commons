package common.libs.extensions

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/** Android 16 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.BAKLAVA)
fun isBaklava36Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
}

/** Android 15 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
fun isVanilla35Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
}

/** Android 14 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun isUpsideDownCake34Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

/** Android 13 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiramisu33Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

/** Android 12L */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S_V2)
fun isS32Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2
}

/** Android 12 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isS31Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

/** Android 11 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isR30Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

/** Android 10 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQ29Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

/** Android 9 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun isP28Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}

/** Android 8.1 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isO27Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
}

/** Android 8 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isO26Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}

/** Android 7.1 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
fun isN25Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
}

/** Android 7 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
fun isN24Plus(): Boolean {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
}