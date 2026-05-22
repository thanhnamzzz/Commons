package common.libs.views

enum class TypeToast {
	SUCCESS,
	ERROR,
	WARNING,
	NONE
}

enum class ToastStyle {
	VERTICAL,
	HORIZONTAL
}

enum class ToastTheme {
	SOFT,
	SOLID
}

object Duration {
	const val SHORT = 1800L
	const val LONG = 3500L
}