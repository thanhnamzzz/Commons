package common.libs.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Date format
 *
 * "dd/MM/yyyy"
 *
 *"yyyy/MM/dd"
 *
 *"yyyy/dd/MM"
 *
 *"d/M/yy"
 *
 *"yy/M/d"
 *
 *"yy/d/M"
 *
 *"dd MMMM yyyy"
 *
 *"MMM dd,yyyy"
 *
 *"MMMM dd,yyyy"
 *
 *"E, MMM dd,yyyy"
 *
 *"E, dd MMMM yyyy"
 *
 *"E, dd/MM/yy"
 *
 *"E, dd/MM/yyyy"
 *
 *"EEEE, MMM dd,yyyy"
 *
 *"EEEE, dd MMMM yyyy"
 *
 *"EEEE, dd/MM/yy"
 *
 *"EEEE, dd/MM/yyyy"
 */
fun getToday(patternDate: PatternDate): String {
	val currentDate = Date()
	val dateFormat = SimpleDateFormat(patternDate.formatDate, Locale.US)
	return dateFormat.format(currentDate)
}

enum class PatternDate(val formatDate: String) {
	dd_MM_yyyy("dd/MM/yyyy"),
	yyyy_MM_dd("yyyy/MM/dd"),
	yyyy_dd_MM("yyyy/dd/MM"),
	d_M_yy("d/M/yy"),
	yy_M_d("yy/M/d"),
	yy_d_M("yy/d/M"),
	ddMMMMyyyy("dd MMMM yyyy"),
	MMMdd_yyyy("MMM dd,yyyy"),
	MMMMdd_yyyy("MMMM dd,yyyy"),
	E_MMMdd_yyyy("E, MMM dd,yyyy"),
	E_ddMMMMyyyy("E, dd MMMM yyyy"),
	E_dd_MM_yy("E, dd/MM/yy"),
	E_dd_MM_yyyy("E, dd/MM/yyyy"),
	EEEE_MMMdd_yyyy("EEEE, MMM dd,yyyy"),
	EEEE_ddMMMMyyyy("EEEE, dd MMMM yyyy"),
	EEEE_dd_MM_yy("EEEE, dd/MM/yy"),
	EEEE_dd_MM_yyyy("EEEE, dd/MM/yyyy"),
}

fun getTodayAndHour(pattern: String): String {
	val currentDate = Date()
	val dateFormat = SimpleDateFormat(pattern, Locale.US)
	return dateFormat.format(currentDate)
}