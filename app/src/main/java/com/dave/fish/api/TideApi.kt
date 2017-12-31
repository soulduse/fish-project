package com.dave.fish.api

import com.dave.fish.api.model.*
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by soul on 2017. 12. 31..
 */
interface TideApi {
    @GET("getChartData.do")
    fun getChartData(
            @Query("obsPostId") obsPostId: String,
            @Query("date") date: String
    ): Deferred<ChartModel>

    @POST("getWeeklyData.do")
    fun getWeeklyData(
            @Query("obsPostId") obsPostId: String,
            @Query("stDate") stDate: String
    ): Deferred<WeeklyModel>

    @POST("sidePanelData.do")
    fun getSidePanelData(
            @Query("obsPostId") obsPostId: String
    ): Deferred<SidePanelModel>

    @POST("getWeatherAndWave.do")
    fun getWeatherAndWave(
            @Query("obsPostId") obsPostId: String,
            @Query("stDate") stDate: String
    ): Deferred<WeatherAndWaveModel>

    @POST("gisDataList.do")
    fun getGisData(): Deferred<GisModel>
}
