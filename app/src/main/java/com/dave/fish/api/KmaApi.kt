package com.dave.fish.api

import com.dave.fish.api.model.ForecastSpaceData
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by soul on 2017. 12. 31..
 */
interface KmaApi {
    @GET("ForecastSpaceData")
    fun getForecastSpaceData(
            @Query("ServiceKey") key: String,
            @Query("base_date") baseDate: String,
            @Query("base_time") baseTime: String,
            @Query("nx") nx: Int,
            @Query("ny") ny: Int,
            @Query("numOfRows") numOfRows: Int,
            @Query("pageNo") pageNo: Int,
            @Query("_type") type: String
    ): Deferred<ForecastSpaceData>
}