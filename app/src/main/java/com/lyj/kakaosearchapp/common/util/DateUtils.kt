package com.lyj.kakaosearchapp.common.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Date 관련 유틸객체
 */
object DateUtils {
    private const val format = "yyyy-MM-DD'T'hh:mm:ss.000+zzz"
    private const val timeZone = "KST"

    private val formatter: SimpleDateFormat by lazy {
        SimpleDateFormat(format, Locale.KOREAN).apply {
            timeZone = TimeZone.getTimeZone(DateUtils.timeZone)
        }
    }
    /**
     * Date 문자열을 Date 와 EpochMillSeconds 로 변경하는 함수
     *
     * @param dateTime Date 문자열
     * @return EpochMillSeconds 과 Date 로 이루어진 Pair 객체
     */
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