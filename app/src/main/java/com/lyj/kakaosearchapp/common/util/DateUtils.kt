package com.lyj.kakaosearchapp.common.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val format = "yyyy-MM-DD'T'hh:mm:ss.000+zzz"
    private const val timeZone = "KST"

    private val formatter: SimpleDateFormat by lazy {
        SimpleDateFormat(format, Locale.KOREAN).apply {
            timeZone = TimeZone.getTimeZone(DateUtils.timeZone)
        }
    }

    fun parse(dateTime: String?): Pair<Long?, Date?> {
        return try {
            if (dateTime != null) {
                val date = formatter.parse(dateTime) ?: return null to null
                date.time to date
            } else {
                null to null
            }
        } catch (e: Exception) {
            null to null
        }
    }
}