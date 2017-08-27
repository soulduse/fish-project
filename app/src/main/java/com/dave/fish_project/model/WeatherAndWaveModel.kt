package com.dave.fish_project.model

/**
 * Created by soul on 2017. 8. 27..
 */
class WeatherAndWaveModel {

    var long : List<LongInfo> ?= null
    var short : List<ShortInfo> ?= null

    data class LongInfo(
            var am: String,
            var pm: String,
            var temp: String,
            var amWave: String,
            var pmWave: String
    )

    data class ShortInfo(
            var fcstDate: String,
            var fcstHour: String,
            var weather: String,
            var temp: String,
            var amWave: String,
            var pmWave: String
    )
}