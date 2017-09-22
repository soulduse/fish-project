package com.dave.fish_project.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.dave.fish_project.R
import com.dave.fish_project.db.RealmController
import com.dave.fish_project.network.RetrofitController
import com.dave.fish_project.view.adapter.ViewPagerAdapter
import com.dave.fish_project.view.fragment.FragmentMenuOne
import com.dave.fish_project.view.fragment.FragmentMenuTwo
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    private val tabIcons = intArrayOf(
            R.drawable.ic_date_range_white_24dp, R.drawable.ic_cloud_white_24dp, R.drawable.ic_toys_white_24dp)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        realm = Realm.getDefaultInstance()

        initViewPager()
        initTab()
        initSpinner()

    }

    private fun initTab() {
        tabs.setupWithViewPager(main_viewpager)
        setupTabIcons()
    }

    private fun initSpinner() {
        if (isEmptyRealmSpinner()) {
            initRealmForSpinner()
        } else {
            setSpinnerAdapter(spinner_loc, RealmController.instance.getSpinnerItems(realm))
        }
        spinner_loc.onItemSelectedListener = firstSpinnerListener
        spinner_map.onItemSelectedListener = secondSpinnerListener
    }

    private fun setSpinnerAdapter(spinner : Spinner, items : List<String>){
        spinner.adapter = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_spinner_item,
                items
        )
    }


    private fun initViewPager() {
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

    var firstSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            setSpinnerAdapter(spinner_map, RealmController.instance.getSelectedSpinnerItem(realm, spinner_loc.selectedItem.toString())!!)
            var secondPosition = RealmController.instance.getSelectedSpinnerItem(realm)
            if(null == secondPosition){
                secondPosition = 0
            }
            spinner_map.setSelection(secondPosition)
        }
    }

    var secondSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            var idValue = RealmController.instance.getPostId(realm, spinner_map.selectedItem.toString())
            Log.w(TAG, "second values are? ---> spinner : ${spinner_map.selectedItem}, id : $idValue")
            RealmController
                    .instance
                    .setSelectedSpinnerItem(
                            realm,
                            spinner_loc.selectedItem.toString(),
                            spinner_map.selectedItem.toString(),
                            p2
                    )
        }
    }

    private fun isEmptyRealmSpinner(): Boolean {
        if (RealmController.instance.getSpinnerItems(realm).isEmpty()) {
            return true
        }

        return false
    }

    private fun initRealmForSpinner() {
        RetrofitController()
                .getGisData()
                .subscribe({ gisModel ->
                    var dataList = gisModel.data
                    var gisMap = dataList?.let {
                        dataList.groupBy {
                            it.doNm
                        }
                    }

                    Log.d(TAG, """
                            map data -->
                            size : ${gisMap?.size}"
                            keys : ${gisMap?.keys}"
                            values : ${gisMap?.values}"
                            """)
                    RealmController.instance.setSpinner(realm, gisMap!!)
                    setSpinnerAdapter(spinner_loc, RealmController.instance.getSpinnerItems(realm))
                }, { e ->
                    Log.e(TAG, "result API response ===> error ${e.localizedMessage}")
                })
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
    }
}
