package com.dave.fish_project.model.spinner

import io.realm.RealmObject

/**
 * Created by soul on 2017. 9. 15..
 */
class SelectedItem : RealmObject() {
    val obsPostId : String = ""
    val obsLat : Double = 0.0
    val obsLon : Double = 0.0
    val obsPostName : String = ""
    val isSelected = false
}
