package com.dave.fish.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.model.realm.TideWeeklyModel
import com.dave.fish.util.Global
import com.dave.fish.util.TideUtil
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_tide_detail.*

/**
 * Created by soul on 2017. 10. 3..
 */
class TideDetailActivity : AppCompatActivity() {

    private val realm : Realm = Realm.getDefaultInstance()
    private val mRealmController : RealmController = RealmController.instance
    private var tideWeeklyItem : TideWeeklyModel = TideWeeklyModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tide_detail)
        init()
    }

    private fun init(){
        initData()
        initLayout()
    }

    private fun initLayout(){
        initToolbar()

        val highList = TideUtil.getHighItemList()
        val lowList = TideUtil.getLowItemList()

        for(i in 0 .. highList.size){
            when(i){
                0 ->{
                    val tideTime = TideUtil.getTime(highList[i])
                    val tideHeight = TideUtil.getHeight(highList[i])
                    lvl1_time.text = tideTime
                    lvl1_height.text = tideHeight
                }

                1 ->{
                    val tideTime = TideUtil.getTime(highList[i])
                    val tideHeight = TideUtil.getHeight(highList[i])
                    lvl3_time.text = tideTime
                    lvl3_height.text = tideHeight
                }
            }
        }

        for(i in 0 .. lowList.size){
            when(i){
                0 ->{
                    val tideTime = TideUtil.getTime(lowList[i])
                    val tideHeight = TideUtil.getHeight(lowList[i])
                    lvl2_time.text = tideTime
                    lvl2_height.text = tideHeight
                }

                1 ->{
                    try{
                        val tideTime = TideUtil.getTime(lowList[i])
                        val tideHeight = TideUtil.getHeight(lowList[i])
                        lvl4_time.text = tideTime
                        lvl4_height.text = tideHeight
                    }catch (e:IndexOutOfBoundsException){
                        lvl4_time.text = resources.getText(R.string.no_data_time)
                        lvl4_height.text = resources.getText(R.string.no_data_height)
                        lvl4_down_img.setBackgroundColor(R.drawable.graph_no_data)
                    }
                }
            }
        }
    }

    private fun initData(){
        val selectedPostName = mRealmController.findSelectedSpinnerItem(realm)?.postName
        val selectedDate = intent.getStringExtra(Global.INTENT_DATE)
        val key = selectedPostName+"_"+selectedDate
        Log.w(TAG, "What is key --> $key")

        tideWeeklyItem = mRealmController.findTideWeekly(realm, key)
        Log.w(TAG, "tideWeeklyItem --> "+tideWeeklyItem.toString())
        TideUtil.setTide(tideWeeklyItem)
    }

    private fun initToolbar(){
        setSupportActionBar(toolbar)
        toolbar_title.text = tideWeeklyItem.obsPostName+" "+tideWeeklyItem.dateSun
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val TAG = TideDetailActivity::class.java.simpleName
    }
}