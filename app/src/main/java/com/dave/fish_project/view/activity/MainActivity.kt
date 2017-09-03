package com.dave.fish_project.view.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.dave.fish_project.R
import com.dave.fish_project.model.GisModel
import com.dave.fish_project.network.RetrofitController
import com.dave.fish_project.view.adapter.ViewPagerAdapter
import com.dave.fish_project.view.fragment.FragmentMenuOne
import com.dave.fish_project.view.fragment.FragmentMenuTwo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

    private fun setupSpnnier(){
        var spinnerLocAdapter = ArrayAdapter.createFromResource(this, R.array.loc_array, android.R.layout.simple_spinner_item)
        spinnerLocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_loc.adapter = spinnerLocAdapter

        var spinnerMapAdapter = ArrayAdapter.createFromResource(this, R.array.map_array, android.R.layout.simple_spinner_item)
        spinnerMapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_map.adapter = spinnerMapAdapter

    }

    private fun getGisList(){
        RetrofitController()
                .getGisData()
                .subscribe({
                    gisModel->
                    var dataList = gisModel.data
                    var areaList = gisModel.areaList

                    dataList?.let {
                        for (data : GisModel.Data in dataList){
                            data.doNm
                        }
                    }



                },{
                    e ->

                }
                )
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
