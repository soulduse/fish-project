package com.dave.fish.network

import com.dave.fish.model.retrofit.ForecastSpaceData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by soul on 2017. 10. 13..
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
    ): Observable<ForecastSpaceData>
}
