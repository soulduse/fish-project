package com.dave.fish.model.spinner

import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 16..
 */
open class SelectItemModel : RealmObject(){
    var doNm: String = ""
    var firstPosition = 0
    var postName: String = ""
    var secondPosition = 0

}