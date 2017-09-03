package com.dave.fish_project.network

import com.dave.fish_project.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by soul on 2017. 8. 25..
 */
class RetrofitController {

    private fun getRetrofit(): TideApi {
        return RetrofitBase.INSTANCE.apiService
    }

    fun getChartData() : Observable<ChartModel>{
        return getRetrofit()
                .getChartData("DT_0001", "20170829")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getWeeklyData() : Observable<WeeklyModel>{
        return getRetrofit()
                .getWeeklyData("DT_0001", "20170829")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getSidePanelData() : Observable<SidePanelModel> {
        return getRetrofit()
                .getSidePanelData("DT_0001")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getWeatherAndWave() : Observable<WeatherAndWaveModel> {
        return getRetrofit()
                .getWeatherAndWave("DT_0001", "20170829")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getGisData() : Observable<GisModel>{
        return getRetrofit()
                .getGisData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    companion object {
        private val TAG = RetrofitController.javaClass.simpleName
    }
}