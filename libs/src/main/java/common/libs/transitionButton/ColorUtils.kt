package common.libs.transitionButton

import androidx.core.graphics.toColorInt

object ColorUtils {
	fun parse(color: String): Int {
		var color = color
		when (color.length) {
			4 -> {
				color = color.replace(
					"#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])".toRegex(),
					"#$1$1$2$2$3$3"
				)
				color = color.replace(
					"#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])".toRegex(),
					"#$2$2$3$3$4$4"
				)
			}

			5 -> color = color.replace(
				"#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])".toRegex(),
				"#$2$2$3$3$4$4"
			)
		}

		return color.toColorInt()
	}
}