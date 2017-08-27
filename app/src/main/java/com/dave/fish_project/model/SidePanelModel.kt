package com.dave.fish_project.model

/**
 * Created by soul on 2017. 8. 27..
 */
class SidePanelModel {

    private var data : List<Data> ?= null

    data class Data(
            var dateSun : String,
            var dateMoon : String,
            var obsPostId : String,
            var title : String,
            var address : String,
            var obsLat : Double,
            var obsLon : Double,
            var lvl1 : String,
            var lvl2 : String,
            var lvl3 : String,
            var lvl4 : String,
            var sdName : String,
            var sdTime : String
    )
}