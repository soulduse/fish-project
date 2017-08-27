package com.dave.fish_project.network

import android.util.Log
import com.dave.fish_project.model.TideModel
import io.reactivex.android.schedulers.AndroidSchedulers
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by soul on 2017. 8. 25..
 */
class RetrofitController {

    fun getRetrofit(): TideApi {
        return RetrofitBase.INSTANCE.apiService
    }

    fun getChartData() {
//        var chartObservable : Observable<TideModel> = getRetrofit()
//                .getChartData("DT_0001", "20170825")
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//
//        chartObservable.subscribe {
//            onNext->
//                Log.d(TAG, "comming here")
//        }
    }

    fun getWeeklyData() {

    }

    fun getSidePanelData() {

    }

    fun getWeatherAndWave() {

    }

    fun gisDataList() {

    }

    companion object {
        private val TAG = RetrofitController.javaClass.simpleName
    }
}