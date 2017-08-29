package com.dave.fish_project.network

import com.dave.fish_project.model.SidePanelModel
import com.dave.fish_project.model.WeeklyModel
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import io.reactivex.Observable

/**
 * Created by soul on 2017. 8. 21..
 */
interface TideApi {

    @GET("getChartData.do")
    fun getChartData(@Query("obsPostId")obsPostId:String, @Query("date") date:String): Observable<WeeklyModel>

    @POST("getWeeklyData.do")
    fun getWeeklyData(@Query("obsPostId") obsPostId: String, @Query("stDate") stDate:String): Observable<WeeklyModel>

    @POST("getSidePanelData.do")
    fun getSidePanelData(@Query("obsPostId") obsPostId: String) : Observable<SidePanelModel>
}