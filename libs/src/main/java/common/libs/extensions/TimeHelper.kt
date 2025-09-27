package common.libs.extensions

import androidx.annotation.IntRange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ## 📅 Các pattern định dạng Date/Time Giờ/Phút/Giây
 *
 * ### Ngày / Tháng / Năm
 *
 * | Pattern   | Ý nghĩa                      | Ví dụ             |
 * |-----------|------------------------------|-------------------|
 * | `y`       | Năm                          | `2025`            |
 * | `yy`      | Năm 2 chữ số                 | `25`              |
 * | `yyyy`    | Năm đầy đủ                   | `2025`            |
 * | `M`       | Tháng (1–12)                 | `9`               |
 * | `MM`      | Tháng 2 chữ số               | `09`              |
 * | `MMM`     | Tháng viết tắt               | `Sep`             |
 * | `MMMM`    | Tháng đầy đủ                 | `September`       |
 * | `d`       | Ngày trong tháng             | `5`               |
 * | `dd`      | Ngày 2 chữ số                | `05`              |
 * | `E`/`EEE` | Thứ viết tắt                 | `Sat`             |
 * | `EEEE`    | Thứ đầy đủ                   | `Saturday`        |
 *
 * ### Giờ / Phút / Giây
 *
 * | Pattern   | Ý nghĩa                      | Ví dụ             |
 * |-----------|------------------------------|-------------------|
 * | `H`       | Giờ (0–23)                   | `0`, `23`         |
 * | `HH`      | Giờ 2 chữ số (0–23)          | `00`, `23`        |
 * | `h`       | Giờ (1–12)                   | `1`, `12`         |
 * | `hh`      | Giờ 2 chữ số (1–12)          | `01`, `12`        |
 * | `k`       | Giờ (1–24)                   | `1`, `24`         |
 * | `kk`      | Giờ 2 chữ số (1–24)          | `01`, `24`        |
 * | `m`       | Phút                         | `5`               |
 * | `mm`      | Phút 2 chữ số                | `05`              |
 * | `s`       | Giây                         | `30`              |
 * | `ss`      | Giây 2 chữ số                | `30`              |
 * | `S`       | Milliseconds (1 chữ số)      | `1`               |
 * | `SSS`     | Milliseconds (3 chữ số)      | `001`, `999`      |
 *
 * ### Timezone
 *
 * | Pattern   | Ý nghĩa                      | Ví dụ             |
 * |-----------|------------------------------|-------------------|
 * | `Z`       | GMT offset                   | `+0700`           |
 * | `ZZZZ`    | GMT offset                   | `+07:00`          |
 * | `z`       | Tên múi giờ ngắn             | `GMT`, `PST`      |
 * | `zzzz`    | Tên múi giờ đầy đủ           | `Pacific Standard Time` |
 * | `X`       | ISO 8601 offset              | `+07`, `+07:00`   |
 * | `XX`      | ISO 8601 offset              | `+0700` |
 * | `XXX`     | ISO 8601 offset              | `+07:00`, `+0700` |
 *
 *  ## 📌 Ví dụ kết hợp định dạng Date/Time
 *
 *  ```
 *  dd/MM/yyyy                  → 27/09/2025
 *  yyyy-MM-dd                  → 2025-09-27
 *  EEE, dd MMM yyyy            → Sat, 27 Sep 2025
 *  EEEE, d MMMM yyyy           → Saturday, 27 September 2025
 *  HH:mm:ss                    → 14:35:20
 *  hh:mm a                     → 02:35 PM
 *  yyyy-MM-dd HH:mm:ss.SSSXXX → 2025-09-27 14:35:20.123+07:00
 *  ```
 */
private val calendarNull: Calendar? = null

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

fun String.formatDate(
    year: Int,
    @IntRange(1, 12) month: Int,
    @IntRange(1, 31) day: Int,
): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
    }

    return calendar.dateConvert(this)
}

fun PatternDate.formatDate(
    year: Int,
    @IntRange(1, 12) month: Int,
    @IntRange(1, 31) day: Int,
): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
    }

    return calendar.dateConvert(this.formatDate)
}

private fun Calendar.dateConvert(pattern: String): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(this.time)
}

/**
 * ## ⏰ Các pattern giờ/phút/giây hay dùng
 *
 * | Pattern        | Kết quả ví dụ (14h35m20s) | Ý nghĩa                           |
 * |----------------|----------------------------|-----------------------------------|
 * | `HH:mm`        | 14:35                      | Giờ:phút (24h)                    |
 * | `HH:mm:ss`     | 14:35:20                   | Giờ:phút:giây (24h)               |
 * | `hh:mm a`      | 02:35 PM                   | Giờ:phút (12h + AM/PM)            |
 * | `hh:mm:ss a`   | 02:35:20 PM                | Giờ:phút:giây (12h + AM/PM)       |
 * | `H:mm:ss`      | 14:35:20                   | Giờ:phút:giây (24h, không padding)|
 * | `k:mm:ss`      | 14:35:20                   | Giờ 1–24 thay vì 0–23             |
 * | `mm:ss`        | 35:20                      | Phút:giây (hay dùng trong media)  |
 * | `ss.SSS`       | 20.123                     | Giây + milli giây                 |
 * | `HH:mm:ss.SSS` | 14:35:20.123               | Giờ:phút:giây.millisecond         |
 */
enum class PatternTime(val format: String) {
    HH_mm("HH:mm"),
    HH_mm_ss("HH:mm:ss"),
    hh_mm_a("hh:mm a"),
    hh_mm_ss_a("hh:mm:ss a"),
    mm_ss("mm:ss"),
    HH_mm_ss_SSS("HH:mm:ss.SSS"),
}

fun String.formatTime(
    @IntRange(0, 23) hour: Int,
    @IntRange(0, 59) minute: Int,
    @IntRange(0, 59) second: Int = 0,
): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour) // 0–23
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, second)
    }
    return calendar.timeConvert(this)
}

fun PatternTime.formatTime(
    @IntRange(0, 23) hour: Int,
    @IntRange(0, 59) minute: Int,
    @IntRange(0, 59) second: Int = 0,
): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, second)
    }
    return calendar.timeConvert(this.format)
}

private fun Calendar.timeConvert(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this.time)
}

fun String.currentDateTimeWithFormat(convertForDate: Boolean = true): String {
    val calendar = Calendar.getInstance().apply { time = Date() }
    return if (convertForDate) calendar.dateConvert(this)
    else calendar.timeConvert(this)
}

fun PatternTime.currentDateTimeWithFormat(): String {
    val calendar = Calendar.getInstance().apply { time = Date() }
    return calendar.timeConvert(this.format)
}

fun PatternDate.currentDateTimeWithFormat(): String {
    val calendar = Calendar.getInstance().apply { time = Date() }
    return calendar.dateConvert(this.formatDate)
}