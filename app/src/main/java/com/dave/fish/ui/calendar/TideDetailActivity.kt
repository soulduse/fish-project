package com.dave.fish.ui.calendar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.api.ApiProvider
import com.dave.fish.api.Network
import com.dave.fish.api.NetworkCallback
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.TideWeeklyModel
import com.dave.fish.api.model.ForecastSpaceData
import com.dave.fish.api.model.WeatherAndWaveModel
import com.dave.fish.util.*
import com.dave.fish.util.DateUtil.DATE_PATTERN_YEAR_MONTH_DAY
import kotlinx.android.synthetic.main.activity_tide_detail.*
import org.joda.time.DateTime

/**
 * Created by soul on 2017. 10. 3..
 */
class TideDetailActivity : AppCompatActivity(){

    private val mRealmController: RealmProvider = RealmProvider.instance
    private var tideWeeklyItem: TideWeeklyModel = TideWeeklyModel()

    private var amWindSpeedMin = Double.MAX_VALUE
    private var amWindSpeedMax = Double.MIN_VALUE
    private var pmWindSpeedMin = Double.MAX_VALUE
    private var pmWindSpeedMax = Double.MIN_VALUE

    private var amWaveMin = Double.MAX_VALUE
    private var amWaveMax = Double.MIN_VALUE
    private var pmWaveMin = Double.MAX_VALUE
    private var pmWaveMax = Double.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tide_detail)
        initRealmData()
        initLayout()
    }

    private fun initRealmData() {
        val secondSpinnerItem = mRealmController.getSecondSpinnerItem()
        val postId = secondSpinnerItem?.obsPostId
        val postName = secondSpinnerItem?.obsPostName

        val selectedDate = intent.getStringExtra(Global.INTENT_DATE)
        val key = postName + "_" + selectedDate
        DLog.w("What is keyTide --> $key")

        postId.let {
            Network.request(ApiProvider.provideTideApi().getWeatherAndWave(
                    postId!!,
                    DateTime(selectedDate).toString(DATE_PATTERN_YEAR_MONTH_DAY)
            ), NetworkCallback<WeatherAndWaveModel>().apply {
                success = { weatherAndWave ->
                    val longDataList = weatherAndWave.long
                    if (longDataList.isNotEmpty()) {
                        longDataList.first().apply {
                            if (tv_detail_wave.text == resources.getString(R.string.no_data_wave)) {
                                tv_detail_wave.text = "${resources.getString(R.string.detail_am)} : ${amWave.replace(" ", "")}\n" +
                                        "${resources.getString(R.string.detail_pm)} : ${pmWave.replace(" ", "")}"
                            }
                        }
                    }
                }
            })
        }

        try{
            tideWeeklyItem = mRealmController.findTideWeekly(key)
            DLog.w("tideWeeklyItem --> " + tideWeeklyItem.toString())
            TideUtil.setTide(tideWeeklyItem)

            setWeatherInfoFromApi()
        }catch (e : IllegalStateException){
            Toast.makeText(applicationContext, "일시적으로 데이터를 읽어올수 없습니다. \n해당 페이지를 다시 접근후 시도해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    private fun initLayout() {
        initToolbar()
        initTide()
        initDetailViews()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar_title.text = tideWeeklyItem.obsPostName + " " + tideWeeklyItem.dateSun
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initTide() {
        val highList = TideUtil.getHighItemList()
        val lowList = TideUtil.getLowItemList()

        for (i in 0..highList.size) {
            when (i) {
                0 -> {
                    lvl1_time.text = TideUtil.getTime(highList[i])
                    lvl1_height.text = TideUtil.getHeight(highList[i])
                }

                1 -> {
                    try {
                        lvl3_time.text = TideUtil.getTime(highList[i])
                        lvl3_height.text = TideUtil.getHeight(highList[i])
                    } catch (e: IndexOutOfBoundsException) {
                        lvl3_time.text = resources.getText(R.string.no_data_time)
                        lvl3_height.text = resources.getText(R.string.no_data_height)
                    }
                }
            }
        }

        for (i in 0..lowList.size) {
            when (i) {
                0 -> {
                    lvl2_time.text = TideUtil.getTime(lowList[i])
                    lvl2_height.text = TideUtil.getHeight(lowList[i])
                }

                1 -> {
                    try {
                        lvl4_time.text = TideUtil.getTime(lowList[i])
                        lvl4_height.text = TideUtil.getHeight(lowList[i])
                    } catch (e: IndexOutOfBoundsException) {
                        lvl4_time.text = resources.getText(R.string.no_data_time)
                        lvl4_height.text = resources.getText(R.string.no_data_height)
                    }
                }
            }
        }
    }

    private fun initDetailViews() {
        tv_detail_date_moon.text = getDetailViewItem(tideWeeklyItem.dateMoon)
        tv_detail_weather.text = getDetailViewItem(tideWeeklyItem.weatherChar)
        tv_detail_temperature.text = getDetailViewItem(tideWeeklyItem.temp) + resources.getString(R.string.measure_temperature)
        tv_detail_wave.text = resources.getString(R.string.no_data_wave)
        tv_detail_etc.text = "${getDetailViewItem(tideWeeklyItem.moolNormal)} " +
                "/ ${getDetailViewItem(tideWeeklyItem.mool7)} " +
                "/ ${getDetailViewItem(tideWeeklyItem.mool8)}"
    }

    private fun setWeatherInfoFromApi() {
        val grid = ApiLocationUtill.instance.convertGridGps(ApiLocationUtill.TO_GRID, tideWeeklyItem.obsLat, tideWeeklyItem.obsLon)
        val nx = grid.x.toInt()
        val ny = grid.y.toInt()
        val searchDate = DateTime(tideWeeklyItem.searchDate).toString(DateUtil.DATE_PATTERN_YEAR_MONTH_DAY)

        Network.request(ApiProvider.provideKmaApi().getForecastSpaceData(
                resources.getString(R.string.kma_opemapi_key),
                searchDate,
                "0200",
                nx,
                ny,
                200,    // 한번에 총 출력할 데이터 량
                1,      // 한번에 총 출력할 페이지 수
                Global.ParserType.JSON.toString().toLowerCase()
        ), NetworkCallback<ForecastSpaceData>().apply {
            success = { forecast->
                DLog.w("api result --> ${forecast.response.toString()}")
                val resultCode = forecast.response.header.resultCode
                when (resultCode) {
                    SUCCESS -> {
                        val itemList = forecast.response.body.items.item
                        itemList.filter {
                            it.baseDate == it.fcstDate
                        }.forEach { item ->
                            initWave(item)
                            initWindSpeed(item)
                        }

                        tv_detail_wave.text = "${resources.getString(R.string.detail_am)} : $amWaveMin-$amWaveMax\n" +
                                "${resources.getString(R.string.detail_pm)} : $pmWaveMin-$pmWaveMax"

                        tv_detail_wind_speed.text = "${resources.getString(R.string.detail_am)} : $amWindSpeedMin-$amWindSpeedMax\n" +
                                "${resources.getString(R.string.detail_pm)} : $pmWindSpeedMin-$pmWindSpeedMax"
                    }
                    BEFORE_DATE ->{

                    }
                }
            }
            error = { error ->
                DLog.w("error code ---> $error")
            }
        })
    }

    private fun initWave(item: ForecastSpaceData.Item){
        if(WeatherCategory.WAV.toString() == item.category){
            if(isMorningTime(item.fcstTime)){
                if(amWaveMin > item.fcstValue){
                    amWaveMin = item.fcstValue
                }

                if(amWaveMax < item.fcstValue){
                    amWaveMax = item.fcstValue
                }
            }else{
                if(pmWaveMin > item.fcstValue){
                    pmWaveMin = item.fcstValue
                }

                if(pmWaveMax < item.fcstValue){
                    pmWaveMax = item.fcstValue
                }
            }
        }
    }

    private fun initWindSpeed(item: ForecastSpaceData.Item){
        if(WeatherCategory.WSD.toString() == item.category){
            if(isMorningTime(item.fcstTime)){
                if(amWindSpeedMin > item.fcstValue){
                    amWindSpeedMin = item.fcstValue
                }

                if(amWindSpeedMax < item.fcstValue){
                    amWindSpeedMax = item.fcstValue
                }
            }else{
                if(pmWindSpeedMin > item.fcstValue){
                    pmWindSpeedMin = item.fcstValue
                }

                if(pmWindSpeedMax < item.fcstValue){
                    pmWindSpeedMax = item.fcstValue
                }
            }
        }
    }

    private fun isMorningTime(time : String) : Boolean = parseTime(time) <= 12

    private fun parseTime(time : String) : Int = time.take(2).toInt()

    private fun getDetailViewItem(detail: String?): String {
        if (isEmptyDetailItem(detail)) {
            return "--"
        }

        return detail!!
    }

    private fun isEmptyDetailItem(detail: String?): Boolean {
        return when {
            detail.isNullOrEmpty() -> return true
            detail == "/" -> return true
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val SUCCESS = "0000"
        val BEFORE_DATE = "99"
    }
}
