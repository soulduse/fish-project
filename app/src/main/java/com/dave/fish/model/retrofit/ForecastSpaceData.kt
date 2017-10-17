package com.dave.fish.model.retrofit

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by soul on 2017. 10. 13..
 * @class 기상청 날씨 API 활용모델
 */
class ForecastSpaceData {

    var response: Response = Response()

    data class Response(
            val header: Header = Header(),
            val body: Body = Body()
    )

    data class Header(
            val resultCode: String = "",
            val resultMsg: String = ""
    )

    data class Body(
            val items: Items = Items(),
            val numOfRows: Int = 0,
            val pageNo: Int = 0,
            val totalCount: Int = 0
    )

    data class Items(
            val item: List<Item> = ArrayList()
    )

    data class Item(
            val baseDate: Date = Date(),
            val baseTime: String = "",
            val category: String = "",
            val fcstDate: Date = Date(),
            val fcstTime: String = "",
            val fcstValue: Double = 0.0,
            val nx: Int = 0,
            val ny: Int = 0
    )
}
