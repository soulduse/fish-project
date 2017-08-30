package com.dave.fish_project.network

import com.dave.fish_project.model.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import io.reactivex.Observable

/**
 * Created by soul on 2017. 8. 21..
 */
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