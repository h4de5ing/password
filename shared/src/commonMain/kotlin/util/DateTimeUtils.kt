package com.password.shared.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val HOUR_MILLIS = 60 * 60 * 1000L
private const val HALF_HOUR_MILLIS = HOUR_MILLIS / 2
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val MONTH_MILLIS = 30 * DAY_MILLIS
private const val YEAR_MILLIS = 365 * DAY_MILLIS

/**
 * 获取简洁的时间表示（如"刚刚"、"1小时之前"等）
 */
fun Long.getConciseTime(): String {
    val nowInMillis = System.currentTimeMillis()
    val diff = nowInMillis - this

    return when {
        diff >= YEAR_MILLIS -> {
            val year = (diff / YEAR_MILLIS).toInt()
            "${year}年前"
        }

        diff >= MONTH_MILLIS -> {
            val month = (diff / MONTH_MILLIS).toInt()
            "${month}个月前"
        }

        diff >= DAY_MILLIS -> {
            val day = (diff / DAY_MILLIS).toInt()
            "${day}天前"
        }

        diff >= HOUR_MILLIS -> {
            val hour = (diff / HOUR_MILLIS).toInt()
            "${hour}小时前"
        }

        diff >= HALF_HOUR_MILLIS -> {
            "半小时前"
        }

        else -> {
            "刚刚"
        }
    }
}

/**
 * 获取当前时间戳（毫秒）
 */
fun getCurrentTimeInMillis(): Long = System.currentTimeMillis()
