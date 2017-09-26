package com.dave.fish_project.model

/**
 * Created by soul on 2017. 8. 27..
 */
class WeatherAndWaveModel {

    var long : List<LongInfo> = ArrayList()
    var short : List<ShortInfo> = ArrayList()

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