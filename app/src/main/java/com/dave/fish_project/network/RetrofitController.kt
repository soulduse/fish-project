package com.dave.fish_project.network

import android.util.Log
import com.dave.fish_project.model.WeeklyModel
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

    fun getWeeklyData() : WeeklyModel?{
        var data : WeeklyModel?= null
        getRetrofit()
                .getWeeklyData("DT_0001", "20170829")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    tideModel->
                    data = tideModel
                    Log.d(TAG, "used api")
                }, {
                    erorr ->
                    Log.d(TAG, "Something wrong")
                    data = null
                })

        return data
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