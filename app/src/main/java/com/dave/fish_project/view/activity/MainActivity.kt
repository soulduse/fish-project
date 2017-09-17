package com.dave.fish_project.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.dave.fish_project.R
import com.dave.fish_project.db.RealmController
import com.dave.fish_project.model.GisModel
import com.dave.fish_project.network.RetrofitController
import com.dave.fish_project.view.adapter.ViewPagerAdapter
import com.dave.fish_project.view.fragment.FragmentMenuOne
import com.dave.fish_project.view.fragment.FragmentMenuTwo
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    var mapData : Map<String, List<GisModel.Data>> ?= null

    private val tabIcons = intArrayOf(
            R.drawable.ic_date_range_white_24dp, R.drawable.ic_cloud_white_24dp, R.drawable.ic_toys_white_24dp)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        realm = Realm.getDefaultInstance()
        setupViewPager()
        tabs.setupWithViewPager(main_viewpager)
        setupTabIcons()
        setupSpnnier()
        getGisList()
    }

    private fun setupViewPager(){
        main_viewpager.adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(FragmentMenuOne())
            addFragment(FragmentMenuTwo())
        }
    }

    private fun setupTabIcons() {
        tabs.getTabAt(0)?.icon = resources.getDrawable(tabIcons[0])
        tabs.getTabAt(1)?.icon = resources.getDrawable(tabIcons[1])
        tabs.getTabAt(2)?.icon = resources.getDrawable(tabIcons[2])
    }

    var firstSpinnerListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {
            Log.d(TAG, "onNothingSelected $p0")
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            Log.d(TAG, "onItemSelected $p1, \n view data : $p1 \n int : $p2 \n long : $p3")
            Log.d(TAG, "Map data ==> ${mapData?.values}")

            var dddd : List<List<GisModel.Data>> = mapData?.values!!.distinct()
            var cccc = dddd[p2+1]
            var dataList : MutableList<String> = ArrayList()
            for(aaaa : GisModel.Data in cccc){
                dataList.add(aaaa.obsPostName)
            }

            val adapter = ArrayAdapter(
                    applicationContext, android.R.layout.simple_spinner_item, dataList)
            spinner_map.adapter = adapter
        }
    }

    var secondSpinnerListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {
            Log.w(TAG, "onNothingSelected $p0")
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            Log.w(TAG, "onItemSelected $p1, \n view data : $p1 \n int : $p2 \n long : $p3")
            Log.w(TAG, "second Spinner value ---> ${spinner_map.selectedItem.toString()}")

            mapData?.values?.forEach {
                e ->
                e.forEach {
                    result->

                    if(result.obsPostName == spinner_map.selectedItem.toString()){
                        Log.e(TAG, """
                            result.obsPostId --> ${result.obsPostId}
                            """)
                        RealmController.instance.setSpinnerItem(realm, result)
                    }
                }
            }
        }
    }

    private fun setupSpnnier(){
        spinner_loc.onItemSelectedListener = firstSpinnerListener
        spinner_map.onItemSelectedListener = secondSpinnerListener
    }

    private fun getGisList(){
        var spinnerDataList : List<String> = ArrayList()
        RetrofitController()
                .getGisData()
                .subscribe({
                    gisModel->

                    Log.d(TAG, "gisModel -- ${gisModel.data.toString()}")
                    var dataList = gisModel.data
                    var gisMap = dataList?.let {
                        dataList.groupBy {
                            it.doNm
                        }
                    }

                    mapData = gisMap

                    Log.d(TAG,"""
                            map data --> " +
                            size : ${mapData?.size}"
                            keys : ${mapData?.keys}"
                            values : ${mapData?.values}"
                            """)

                    Log.w(TAG, "map key is 부산광역시 --> ${mapData!!["부산광역시"]}")

                    var spinnerDataList : List<String> = gisMap?.keys?.toList()?.filter { d->d!=null }!!
                    Log.d(TAG, "list data ===> ${spinnerDataList.orEmpty().toString()}")
                    val adapter = ArrayAdapter(
                            applicationContext, android.R.layout.simple_spinner_item, spinnerDataList)
                    spinner_loc.adapter = adapter

                    var secondList = ArrayList<String>()

                    for(data : String in spinnerDataList!!){
                        for(gisModel :GisModel.Data in gisMap?.get(data)!!){
                            secondList.add(gisModel.obsPostName)
                        }
                    }
                },{
                    e ->
                    Log.e(TAG, "result API response ===> error ${e.localizedMessage}")
                })
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
    }
}
