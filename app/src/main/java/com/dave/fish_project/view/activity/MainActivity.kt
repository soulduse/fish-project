package com.dave.fish_project.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.dave.fish_project.R
import com.dave.fish_project.model.GisModel
import com.dave.fish_project.network.RetrofitController
import com.dave.fish_project.view.adapter.ViewPagerAdapter
import com.dave.fish_project.view.fragment.FragmentMenuOne
import com.dave.fish_project.view.fragment.FragmentMenuTwo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var spinnerDataList : List<String> ?= null
    var mapData : Map<String, List<GisModel.Data>> ?= null

    private val tabIcons = intArrayOf(
            R.drawable.ic_date_range_white_24dp, R.drawable.ic_cloud_white_24dp, R.drawable.ic_toys_white_24dp)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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

    var spinnerListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {
            Log.d(TAG, "onNothingSelected $p0")
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            Log.d(TAG, "onItemSelected $p1, \n view data : $p1 \n int : $p2 \n long : $p3")
            Log.d(TAG, "Map data ==> ${mapData?.values}")

            var dddd : List<List<GisModel.Data>> = mapData?.values!!.distinct()
            Log.d(TAG, dddd.toString())
//
            var cccc = dddd[p2+1]
            var dataList : MutableList<String> = ArrayList()
            for(aaaa : GisModel.Data in cccc){
                dataList.add(aaaa.obsPostName)
            }
            Log.d(TAG, "result second ==> ${dataList.toString()}")

            val adapter = ArrayAdapter(
                    applicationContext, android.R.layout.simple_spinner_item, dataList)
            spinner_map.adapter = adapter
        }
    }

    private fun setupSpnnier(){
//        var spinnerAdapter : ArrayAdapter<Map<String, List<GisModel.Data>>> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countryList)

        spinner_loc.onItemSelectedListener = spinnerListener
//        spinner_map.onItemSelectedListener = spinnerListener
//
//        var spinnerMapAdapter = ArrayAdapter.createFromResource(this, R.array.map_array, android.R.layout.simple_spinner_item)
//        spinnerMapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner_map.adapter = spinnerMapAdapter
    }

    private fun getGisList(){

        var spinnerAList = ArrayList<String>()
        RetrofitController()
                .getGisData()
                .subscribe({
                    gisModel->
                    var dataList = gisModel.data
                    var gisMap = dataList?.let {
                        dataList.groupBy {
                            it.doNm
                        }
                    }

                    mapData = gisMap

                    Log.e(TAG, """
                        result API response
                            ㄴsize : ${gisMap?.size}
                            ㄴkey : ${gisMap?.keys}
                            ㄴvalue : ${gisMap?.values}
                            ㄴentries : ${gisMap?.entries}
                        """)
                    spinnerDataList = gisMap?.keys?.distinct()?.filter { d->d!=null }
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

    /*
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
     */

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
    }
}
