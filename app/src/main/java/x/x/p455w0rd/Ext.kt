package x.x.p455w0rd

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date

// Time constants (milliseconds)
private const val HOUR_MILLIS = 60 * 60 * 1000L
private const val HALF_HOUR_MILLIS = HOUR_MILLIS / 2
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val MONTH_MILLIS = 30 * DAY_MILLIS
private const val YEAR_MILLIS = 365 * DAY_MILLIS

private val DEFAULT_DATE_FORMAT = SimpleDateFormat("yyyy.MM.dd    HH : mm")

/**
 * 获取简洁的时间表示（如"刚刚"、"1小时之前"等）
 */
fun Long.getConciseTime(context: Context?): String {
    if (context == null) return ""
    
    val nowInMillis = System.currentTimeMillis()
    val diff = nowInMillis - this
    
    return when {
        diff >= YEAR_MILLIS -> {
            val year = (diff / YEAR_MILLIS).toInt()
            context.getString(R.string.before_year, year)
        }
        diff >= MONTH_MILLIS -> {
            val month = (diff / MONTH_MILLIS).toInt()
            context.getString(R.string.before_month, month)
        }
        diff >= DAY_MILLIS -> {
            val day = (diff / DAY_MILLIS).toInt()
            context.getString(R.string.before_day, day)
        }
        diff >= HOUR_MILLIS -> {
            val hour = (diff / HOUR_MILLIS).toInt()
            context.getString(R.string.before_hour, hour)
        }
        diff >= HALF_HOUR_MILLIS -> {
            context.getString(R.string.before_half_hour)
        }
        else -> {
            context.getString(R.string.just_now)
        }
    }
}

/**
 * 将时间戳转换为字符串
 */
fun Long.formatTime(dateFormat: SimpleDateFormat = DEFAULT_DATE_FORMAT): String = dateFormat.format(Date(this))

/**
 * 获取当前时间戳（毫秒）
 */
fun getCurrentTimeInMillis(): Long = System.currentTimeMillis()

/**
 * 获取当前时间的字符串表示
 */
fun getCurrentTimeString(dateFormat: SimpleDateFormat = DEFAULT_DATE_FORMAT): String =
    getCurrentTimeInMillis().formatTime(dateFormat)
