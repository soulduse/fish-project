package com.dave.fish.model.realm

import io.realm.RealmObject
import java.util.*

/**
 * Created by soul on 2017. 10. 17..
 */
open class WeatherModel : RealmObject(){
    var temp: String = ""
    var wave: String = ""
    var windSpeed : String = ""
    var date : Date = Date()
}
