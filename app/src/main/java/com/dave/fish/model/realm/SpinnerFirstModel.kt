package com.dave.fish.model.realm

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 16..
 */

open class SpinnerFirstModel : RealmObject(){
    var areaName :String = ""
    var secondSpinnerItems : RealmList<SpinnerSecondModel> = RealmList()
}