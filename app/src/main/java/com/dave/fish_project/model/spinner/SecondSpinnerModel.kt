package com.dave.fish_project.model.spinner

import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 15..
 */
open class SecondSpinnerModel : RealmObject() {
    var obsPostId : String = ""
    var obsLat : Double = 0.0
    var obsLon : Double = 0.0
    var obsPostName : String = ""
}
