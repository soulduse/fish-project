package com.dave.fish.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by soul on 2017. 9. 16..
 *
 * keyTide  : 0 - main
 *          : 1 - today
 */
open class SelectItemModel : RealmObject(){
    @PrimaryKey
    var keyTide: Int = 0
    var postName: String = ""
    var doNm: String = ""
}