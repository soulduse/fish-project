package com.dave.fish.db.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by soul on 2017. 9. 16..
 */

open class SpinnerFirstModel : RealmObject(){
    @PrimaryKey
    var id: String = ""
    var areaName :String = ""
    var secondSpinnerItems : RealmList<SpinnerSecondModel> = RealmList()
}