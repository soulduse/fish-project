package com.dave.fish.model.spinner

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 16..
 */

open class FirstSpinnerModel : RealmObject(){
    var areaName :String = ""
    var secondSpinnerItems : RealmList<SecondSpinnerModel> ?= RealmList()
}