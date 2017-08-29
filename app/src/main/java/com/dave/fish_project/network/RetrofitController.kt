package com.dave.fish_project.network

import android.util.Log
import com.dave.fish_project.model.SidePanelModel
import com.dave.fish_project.model.WeeklyModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by soul on 2017. 8. 25..
 */
class RetrofitController {

    fun getRetrofit(): TideApi {
        return RetrofitBase.INSTANCE.apiService
    }

    fun getChartData() {

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

    fun getWeatherAndWave() {

    }

    fun gisDataList() {

    }

    companion object {
        private val TAG = RetrofitController.javaClass.simpleName
    }
}