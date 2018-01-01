package com.dave.fish.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by soul on 2017. 10. 4..
 */
open class TideWeeklyModel : RealmObject() {
    @PrimaryKey
    var key : String = ""
    var postId : String = ""
    var searchDate: Date = Date()
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
    var temp: String = ""
    var am: String = ""
    var weatherChar: String = ""
}