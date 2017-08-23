package com.dave.fish_project.network

import com.dave.fish_project.model.TideModel
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable
import java.util.*

/**
 * Created by soul on 2017. 8. 21..
 */
interface TideApi {

    @GET("getChartData.do")
    fun getTrends(@Query("obsPostId")obsPostId:String, @Query("date") date:Date): Observable<TideModel>

//    @POST("getWeeklyData.do")
//    fun getWeeklyData(@Query("stDate") stDate:Date, @Query("obsPostId") obsPostId: String): Observable<T>
}