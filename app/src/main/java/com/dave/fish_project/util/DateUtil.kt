package com.dave.fish_project.util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

/**
 * Created by soul on 2017. 8. 23..
 */
class DateUtil {

    fun getCurrentDate(pattern:String) : String{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        val currentTime = DateTime(seoul)
        return currentTime.toString(pattern)
    }

    fun getCurrentDate() : DateTime{
        return DateTime()
    }

    fun getDataWithPattern(dateTime: DateTime, pattern: String) : String{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        return dateTime.withZone(seoul).toString(pattern)
    }

    private object Holder{
        val INSTANCE = DateUtil()
    }

    companion object {
        val instance : DateUtil by lazy{ Holder.INSTANCE }
        val DATE_PATTERN_YEAR_MONTH_DAY = "yyyyMMdd"
        val DATE_PATTERN_YEAR_MONTH_DAY_ADD_DASH = "yyyy-MM-dd"
        val DATE_PATTERN_ALL = "yyyy-MM-dd hh:mm:ss"
        val DATE_TIME_ZONE_SEOUL = "Asia/Seoul"
    }
}
