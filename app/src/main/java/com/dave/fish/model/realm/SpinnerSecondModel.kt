package com.dave.fish.model.realm

import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 15..
 */
open class SpinnerSecondModel : RealmObject() {
    var obsPostId : String = ""
    var obsLat : Double = 0.0
    var obsLon : Double = 0.0
    var obsPostName : String = ""
}
