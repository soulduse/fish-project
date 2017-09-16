package com.dave.fish_project.model.spinner

import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 16..
 */
open class SelectItemModel : RealmObject(){
    var firstSpinner: String = ""
    var secondSpinner : String = ""
}