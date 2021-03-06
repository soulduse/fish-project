package com.dave.fish.api.model

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by soul on 2017. 8. 27..
 */
class ChartModel {

    var currentTide : List<CurrentTide> = ArrayList()
    var tenMinTide : List<TenMinTide> = ArrayList()
    var highLowTide : List<HighLowTide> = ArrayList()
    var springRange : List<SpringRange> = ArrayList()
    var sunRiseSet : List<SunRiseSet> = ArrayList()

    data class CurrentTide(
            var time : Date,
            var tideHeight : Int,
            var obsLat : Double,
            var obsLon : Double
    )

    data class TenMinTide(
            var time : Date,
            var tideHeight : Int
    )

    data class HighLowTide(
            var hillowDate : Date,
            var time1 : Date,
            var tideLevel1 : String,
            var time2 : Date,
            var tideLevel2 : String,
            var time3 : Date,
            var tideLevel3 : String,
            var time4 : Date,
            var tideLevel4 : String
    )

    data class SpringRange(
            var springRange : Float
    )

    data class SunRiseSet(
            var sunRiseDate: Date,
            var upTime: Date,
            var downTime: Date
    )

}