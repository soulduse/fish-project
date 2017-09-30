package com.dave.fish

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.Test
import kotlin.test.*

/**
 * Created by soul on 2017. 8. 24..
 */
class JodaTimeTest {

    @Test
    fun changeDate(){
        var minDayOfMonth =  DateTime().dayOfMonth().withMinimumValue()
        println(minDayOfMonth.toString("yyyy.MM.dd"))
        println(minDayOfMonth.plusDays(7).toString("yyyy.MM.dd"))
        println(minDayOfMonth.plusDays(14).toString("yyyy.MM.dd"))
        println(minDayOfMonth.plusDays(21).toString("yyyy.MM.dd"))
    }

    @Test
    fun shouldGetAfterOneMinute() {
        val seoul = DateTimeZone.forID("Asia/Seoul")
        val theTime = DateTime(1961, 8, 9, 23, 59, seoul)
        val pattern = "yyyy.MM.dd HH:mm"
        assertEquals(theTime.toString(pattern), "1961.08.09 23:593", "Success Test")

        val after1Minute = theTime.plusMinutes(1)
        assertEquals(after1Minute.toString(pattern), "1961.08.10 00:303", "Success Test")
    }

    @Test
    fun dateTest(){
        val seoul = DateTimeZone.forID("Asia/Seoul")
        val jakarta = DateTimeZone.forID("Asia/Jakarta")
        val currentTime = DateTime(jakarta)
        val pattern = "yyyy.MM.dd HH:mm:ss"
        assertEquals(currentTime.toString(pattern), "dd", "What happend?")
    }

    @Test
    fun dateZoneTest(){
        val pattern = "yyyy.MM.dd HH:mm:ss"
        val seoul = DateTimeZone.forID("Asia/Seoul")
        val jakarta = DateTimeZone.forID("Asia/Jakarta")
        var date1 = DateTime(seoul)
        var date2 = DateTime(jakarta)

        date1 = date1.withZone(jakarta)

        assertEquals(date1.toString(pattern), date2.toString(pattern), "What happend?")
    }
}
