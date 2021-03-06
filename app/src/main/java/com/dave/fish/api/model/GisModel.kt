package com.dave.fish.api.model

/**
 * Created by soul on 2017. 8. 27..
 */
class GisModel {

    val data: List<Data> = ArrayList()
    val areaList: List<Area> = ArrayList()
    val gisDataDTO: Gis = Gis()

    data class Data(
            val obsPostId: String = "",
            val oldId: String = "",
            val obsPostName: String = "",
            val obsLat: Double = 0.0,
            val obsLon: Double = 0.0,
            val doNm: String = "",
            val address: String = "",
            val obsStartDate: String = "",
            val keyword: String = "",
            val useYn: String = ""
    )

    data class Area(
            val doNm: String = ""
    )

    data class Gis(
            val obsPostId: String = "",
            val oldId: String = "",
            val obsPostName: String = "",
            val obsLat: Double = 0.0,
            val obsLon: Double = 0.0,
            val doNm: String = "",
            val address: String = "",
            val obsStartDate: String = "",
            val keyword: String = "",
            val useYn: String = "",
            val searchValue: String = "",
            val date: String = "",
            val downYear: String = "",
            val fileType: String = "",
            val timeInterval: String = "",
            val moonCode: String = "",
            val hillowDate: String = "",
            val lvl1: String = "",
            val lvl2: String = "",
            val lvl3: String = ""
    )
}