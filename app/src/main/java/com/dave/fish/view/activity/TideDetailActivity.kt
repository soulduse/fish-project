package com.dave.fish.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.model.realm.TideWeeklyModel
import com.dave.fish.network.RetrofitController
import com.dave.fish.util.Global
import com.dave.fish.util.TideUtil
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_tide_detail.*
import org.joda.time.DateTime

/**
 * Created by soul on 2017. 10. 3..
 */
class TideDetailActivity : AppCompatActivity() {
    private val realm: Realm = Realm.getDefaultInstance()

    private val mRealmController: RealmController = RealmController.instance
    private var tideWeeklyItem: TideWeeklyModel = TideWeeklyModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tide_detail)
        init()
    }

    private fun init() {
        initData()
        initLayout()
    }

    private fun initData() {
        val selectedSpinnerItem = mRealmController.findSelectedSpinnerItem(realm)
        val postName = selectedSpinnerItem?.postName
        val postId = selectedSpinnerItem?.let {
            mRealmController
                    .findByPostName(
                            realm,
                            selectedSpinnerItem?.doNm!!,
                            selectedSpinnerItem?.postName!!
                    )?.obsPostId
        }

        val selectedDate = intent.getStringExtra(Global.INTENT_DATE)
        val key = postName + "_" + selectedDate
        Log.w(TAG, "What is key --> $key")

        postId?.let {
            val weatherAndWave = RetrofitController.instance.getWeatherAndWave(postId, DateTime(selectedDate))
            weatherAndWave.subscribe { response->
                val longDataList = response.long
                val shortDataList = response.short

                if(longDataList.isNotEmpty()){
                    longDataList.first().apply {
                        tv_detail_wave.text = "${resources.getString(R.string.detail_am)} : ${amWave.replace(" ","")}\n"+
                        "${resources.getString(R.string.detail_pm)} : ${pmWave.replace(" ","")}"
                    }
                }
            }
        }

        tideWeeklyItem = mRealmController.findTideWeekly(realm, key)
        Log.w(TAG, "tideWeeklyItem --> " + tideWeeklyItem.toString())
        TideUtil.setTide(tideWeeklyItem)
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
        val TAG = TideDetailActivity::class.java.simpleName
    }
}
