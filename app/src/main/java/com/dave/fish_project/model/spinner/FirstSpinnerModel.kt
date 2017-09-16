package com.dave.fish_project.model.spinner

import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 16..
 */

open class FirstSpinnerModel : RealmObject(){
    var areaName :String = ""
    var secondSpinnerItems : SecondSpinnerModel ?= null
}