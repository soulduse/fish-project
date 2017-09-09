package com.dave.fish_project.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by soul on 2017. 8. 21..
 */
class WeeklyModel {
    //getWeeklyData.do?stDate=20170913&obsPostId=DT_0001

    var getWeeklyData : WeeklyData ?= null

    @SerializedName("weeklyDataDTO")
    var weeklyDataDTO :WeeklyDataDTO ?= null

    @SerializedName("weeklyData")
    var weeklyDataList :List<WeeklyData> ?= null

    data class WeeklyDataDTO(
            var obsPostId : String,
            var stDate : String,
            var enDate : String,
            var year : String,
            var addDays : String,
            var lunarCode : String
    )

    data class WeeklyData(
            var searchDate:String,
            var obsPostName:String,
            var obsLat:String,
            var obsLon:String,
            var lvl1:String,
            var lvl2:String,
            var lvl3:String,
            var lvl4:String,
            var flgView:String,
            var dateSun:String,
            var dateMoon:String,
            var moonState:String,
            var moolNormal:String,
            var mool7:String,
            var mool8:String,
            var upTime:String,
            var downTime:String,
            var temp:String
    )
}
