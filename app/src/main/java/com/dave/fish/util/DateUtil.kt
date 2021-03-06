package com.dave.fish.util

import com.dave.fish.MyApplication
import com.dave.fish.R
import org.joda.time.DateTime

import org.joda.time.DateTimeZone
import org.joda.time.Period
import java.util.*

/**
 * Created by soul on 2017. 8. 23..
 */
object DateUtil {

    val DATE_PATTERN_YEAR_MONTH_DAY = "yyyyMMdd"
    val DATE_PATTERN_YEAR_MONTH_DAY_ADD_DASH = "yyyy-MM-dd"
    val DATE_PATTERN_YEAR_MONTH = "yyyy-MM"
    val DATE_PATTERN_HOUR_MINUTE = "hh:mm"
    val DATE_PATTERN_TIME_OF_DATE = "mmss"
    val DATE_PATTERN_ALL = "yyyy-MM-dd hh:mm:ss"
    val DATE_TIME_ZONE_SEOUL = "Asia/Seoul"

    fun getSubtractMin(startAt: Date?, endAt: Date): String{
        if(startAt == null){
            return MyApplication.context?.getString(R.string.no_time_found)!!
        }
        val period = Period(startAt.time, endAt.time)
        return period.minutes.toString()
    }

    fun getDate(date: Date?): String {
        if(date == null){
            return MyApplication.context?.getString(R.string.no_time_found)!!
        }

        return DateTime(date).toString("yy-MM-dd hh:mm")
    }

    fun getCurrentDate(pattern:String) : String{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        val currentTime = DateTime(seoul)
        return currentTime.toString(pattern)
    }

    fun getDate(millis: Long): String{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        val dateTime = DateTime(millis, seoul)
        return dateTime.toString(DATE_PATTERN_HOUR_MINUTE)
    }

    fun getCurrentDate() : DateTime{
        return DateTime()
    }

    fun equalsDateWithCurrentDate(compareDate: DateTime) : Boolean{
        val seoul = DateTimeZone.forID("Asia/Seoul")
        val currentDate = DateTime(seoul)
        return currentDate.toString(DATE_PATTERN_YEAR_MONTH_DAY_ADD_DASH) == compareDate.toString(DATE_PATTERN_YEAR_MONTH_DAY_ADD_DASH)
    }

    /**
     * @method - 현재 년,월과 비교 날짜가 같을때 값 반환
     * @why - 위의 조건에 해당하는 날짜의 데이터는 유동적으로 변경되는 값이기 때문에 ex)날씨 매번 데이터 갱신이 필요하다.
     * @param compareDate - 비교할 날짜
     */
    fun isBiggerThanCurrentDate(compareDate: DateTime) : Boolean{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        val currentDate = DateTime(seoul)
        return currentDate.toString(DATE_PATTERN_YEAR_MONTH) == compareDate.toString(DATE_PATTERN_YEAR_MONTH)
    }

    fun isBeforeToday(compareDate: DateTime) : Boolean{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        val currentDate = DateTime(seoul)
        return compareDate.isBefore(currentDate)
    }

    fun isThisMonth(date : DateTime) : Boolean{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        val currentDate = DateTime(seoul)
        return currentDate.toString(DATE_PATTERN_YEAR_MONTH) == date.toString(DATE_PATTERN_YEAR_MONTH)
    }

    fun getDataWithPattern(dateTime: DateTime, pattern: String) : String{
        val seoul = DateTimeZone.forID(DATE_TIME_ZONE_SEOUL)
        return dateTime.withZone(seoul).toString(pattern)
    }

}
