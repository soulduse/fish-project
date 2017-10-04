package com.dave.fish.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by soul on 2017. 10. 4..
 */
open class TideWeeklyModel : RealmObject() {
    @PrimaryKey
    var key : String = ""
    var searchDate: String = ""
    var obsPostName: String = ""
    var obsLon: Double = 0.0
    var obsLat: Double = 0.0
    var lvl1: String = ""
    var lvl2: String = ""
    var lvl3: String = ""
    var lvl4: String = ""
    var flgView: String = ""
    var dateSun: String = ""
    var dateMoon: String = ""
    var moolNormal: String = ""
    var mool7: String = ""
    var mool8: String = ""
    var weatherChar: String = ""
    var am: String = ""
    var temp: String = ""
}