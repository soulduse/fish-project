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

    private val retrofitBase = RetrofitBase.INSTANCE

    private fun getTideRetrofit(): TideApi {
        retrofitBase.initRetrofit(RetrofitBase.Api.Tide)
        return retrofitBase.tideServiceApi!!
    }

    private fun getKmaRetrofit(): KmaApi {
        retrofitBase.initRetrofit(RetrofitBase.Api.Kma)
        return retrofitBase.kmaServiceApi!!
    }

    fun getChartData() : Observable<ChartModel>{
        return getTideRetrofit()
                .getChartData("DT_0001", DateTime().toString(DATE_FORMAT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getWeeklyData(postId:String, dateTime: DateTime) : Observable<WeeklyModel>{
        return getTideRetrofit()
                .getWeeklyData(postId, dateTime.toString(DATE_FORMAT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getSidePanelData() : Observable<SidePanelModel> {
        return getTideRetrofit()
                .getSidePanelData("DT_0001")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getWeatherAndWave(postId:String, dateTime: DateTime) : Observable<WeatherAndWaveModel> {
        return getTideRetrofit()
                .getWeatherAndWave(postId, dateTime.toString(DATE_FORMAT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getGisData() : Observable<GisModel>{
        return getTideRetrofit()
                .getGisData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getForecastSpaceData() : Observable<ForecastSpaceData>{
        return getKmaRetrofit()
                .getForecastSpaceData()
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