package com.dave.fish_project.model

/**
 * Created by soul on 2017. 8. 27..
 */
class GisModel {

    private var data : List<Data> ?= null
    private var areaList : List<Area> ?= null
    private var gisDataDTO : List<Gis> ?= null

    data class Data(
            var obsPostId : String,
            var oldId : String,
            var obsPostName : String,
            var obsLat : Double,
            var obsLon : Double,
            var doNm : String,
            var address : String,
            var obsStartDate : String,
            var keyword : String,
            var useYn : String
    )

    data class Area(
            var doNm : String
    )

    data class Gis(
            var obsPostId : String,
            var oldId : String,
            var obsPostName : String,
            var obsLat : Double,
            var obsLon : Double,
            var doNm : String,
            var address : String,
            var obsStartDate : String,
            var keyword : String,
            var useYn : String,
            var searchValue : String,
            var date : String,
            var downYear : String,
            var fileType : String,
            var timeInterval : String,
            var moonCode : String,
            var hillowDate : String,
            var lvl1 : String,
            var lvl2 : String,
            var lvl3 : String
    )
}