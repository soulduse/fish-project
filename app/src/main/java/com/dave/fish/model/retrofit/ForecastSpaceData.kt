package com.dave.fish.model.retrofit

import java.util.*

/**
 * Created by soul on 2017. 10. 13..
 * @class 기상청 날씨 API 활용모델
 */
class ForecastSpaceData {

    var response : Response ?= null

    data class Response(
            val header: Header,
            val body: Body
    )

    data class Header(
            val resultCode: String,
            val resultMsg: String
    )

    data class Body(
            val items: Items,
            val numOfRows: Int,
            val pageNo: Int,
            val totalCount: Int
    )

    data class Items(
            val item : List<Item>
    )

    data class Item(
            val baseDate: Date,
            val baseTime: String,
            val category: String,
            val fcstDate: Date,
            val fcstTime: String,
            val fcstValue: Double,
            val nx: Int,
            val ny: Int
    )
}
