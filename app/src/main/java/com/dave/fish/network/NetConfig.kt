package com.dave.fish.network

import com.dave.fish.model.retrofit.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by soul on 2017. 11. 28..
 */
object NetConfig {

    const val TIDE_URL = "http://www.khoa.go.kr/swtc/"
    const val FORECAST_URL = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/"

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

    interface TideApi {
        @GET("getChartData.do")
        fun getChartData(@Query("obsPostId")obsPostId:String, @Query("date") date:String): Observable<ChartModel>

        @POST("getWeeklyData.do")
        fun getWeeklyData(@Query("obsPostId") obsPostId: String, @Query("stDate") stDate:String): Observable<WeeklyModel>

        @POST("getSidePanelData.do")
        fun getSidePanelData(@Query("obsPostId") obsPostId: String) : Observable<SidePanelModel>

        @POST("getWeatherAndWave.do")
        fun getWeatherAndWave(@Query("obsPostId") obsPostId: String, @Query("stDate") stDate:String) : Observable<WeatherAndWaveModel>

        @POST("gisDataList.do")
        fun getGisData() : Observable<GisModel>
    }
}