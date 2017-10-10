package com.dave.fish.network

import com.dave.fish.model.retrofit.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

/**
 * Created by soul on 2017. 8. 25..
 */
class RetrofitController {

    private fun getRetrofit(): TideApi {
        return RetrofitBase.INSTANCE.apiService
    }

    fun getChartData() : Observable<ChartModel>{
        return getRetrofit()
                .getChartData("DT_0001", DateTime().toString(DATE_FORMAT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getWeeklyData(postId:String, dateTime: DateTime) : Observable<WeeklyModel>{
        return getRetrofit()
                .getWeeklyData(postId, dateTime.toString(DATE_FORMAT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getSidePanelData() : Observable<SidePanelModel> {
        return getRetrofit()
                .getSidePanelData("DT_0001")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getWeatherAndWave(postId:String, dateTime: DateTime) : Observable<WeatherAndWaveModel> {
        return getRetrofit()
                .getWeatherAndWave(postId, dateTime.toString(DATE_FORMAT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getGisData() : Observable<GisModel>{
        return getRetrofit()
                .getGisData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    private object Holder { val INSTANCE = RetrofitController() }

    companion object {
        val instance : RetrofitController by lazy { Holder.INSTANCE }
        private val TAG = RetrofitController.javaClass.simpleName
        private val DATE_FORMAT = "yyyyMMdd"
    }
}