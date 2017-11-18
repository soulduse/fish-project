package com.dave.fish.network

import com.dave.fish.MyApplication
import com.dave.fish.R
import com.dave.fish.model.retrofit.*
import com.dave.fish.util.DateUtil
import com.dave.fish.util.Global
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

/**
 * Created by soul on 2017. 8. 25..
 */
class RetrofitController {

    private val retrofitBase = BaseRetrofit.instance

    private fun getTideRetrofit(): TideApi {
        return retrofitBase.getTideRetrofit()
    }

    private fun getKmaRetrofit(): KmaApi {
        return retrofitBase.getKmaRetrofit()
    }

    fun getChartData() : Observable<ChartModel>{
        return getTideRetrofit()
                .getChartData("DT_0001", DateTime().toString(DateUtil.DATE_PATTERN_YEAR_MONTH_DAY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getWeeklyData(postId:String, dateTime: DateTime) : Observable<WeeklyModel>{
        return getTideRetrofit()
                .getWeeklyData(postId, dateTime.toString(DateUtil.DATE_PATTERN_YEAR_MONTH_DAY))
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
                .getWeatherAndWave(postId, dateTime.toString(DateUtil.DATE_PATTERN_YEAR_MONTH_DAY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getGisData() : Observable<GisModel>{
        return getTideRetrofit()
                .getGisData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun getForecastSpaceData(baseDate: String,
                             nx: Int,
                             ny: Int) : Observable<ForecastSpaceData>{
        return getKmaRetrofit()
                .getForecastSpaceData(
                        MyApplication.context?.resources?.getString(R.string.kma_opemapi_key)!!,
                        baseDate,
                        "0200",
                        nx,
                        ny,
                        200,    // 한번에 총 출력할 데이터 량
                        1,      // 한번에 총 출력할 페이지 수
                        Global.ParserType.JSON.toString().toLowerCase())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    private object Holder { val INSTANCE = RetrofitController() }

    companion object {
        val instance : RetrofitController by lazy { Holder.INSTANCE }
        private val TAG = RetrofitController.javaClass.simpleName
    }
}