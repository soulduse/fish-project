package com.dave.fish.network

import com.dave.fish.model.retrofit.ForecastSpaceData
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by soul on 2017. 10. 13..
 */
interface KmaApi {

    @GET("getChartData.do")
    fun getForecastSpaceData(): Observable<ForecastSpaceData>
}
