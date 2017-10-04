package com.dave.fish.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.model.realm.SelectItemModel
import com.dave.fish.network.RetrofitController
import com.dave.fish.view.adapter.ViewPagerAdapter
import com.dave.fish.view.fragment.FragmentMenuOne
import com.dave.fish.view.fragment.FragmentMenuTwo
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private lateinit var mRealmController : RealmController
    private var firstSpinnerPosition = 0
    private var selectedSpinner : SelectItemModel?= null
    private val tabIcons = intArrayOf(
            R.drawable.ic_date_range_white_24dp, R.drawable.ic_cloud_white_24dp, R.drawable.ic_toys_white_24dp)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initRealm()
        initTab()
        initSpinner()
        initViewPager()
    }

    private fun initRealm() {
        realm = Realm.getDefaultInstance()
        mRealmController = RealmController.instance
        mRealmController.setListener(realmListener)
    }

    private fun initTab() {
        tabs.setupWithViewPager(main_viewpager)
        setupTabIcons()
    }

    private fun initSpinner() {
        if (isEmptyRealmSpinner()) {
            initRealmForSpinner()
        } else {
            setSpinnerAdapter(spinner_loc, mRealmController.getSpinnerItems(realm))
        }

        spinner_loc.onItemSelectedListener = spinnerListener
        spinner_map.onItemSelectedListener = spinnerListener

        selectedSpinner = mRealmController.findSelectedSpinnerItem(realm)
        selectedSpinner?.let {
            spinner_loc.setSelection(selectedSpinner?.firstPosition!!)
        }
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

    var spinnerListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val spinner = parent as Spinner

            when(spinner){
                spinner_loc ->{
                    setSpinnerAdapter(spinner_map, mRealmController.getSelectedSpinnerItem(realm, spinner_loc.selectedItem.toString())!!)
                    firstSpinnerPosition = pos
                    selectedSpinner?.let {
                        if(selectedSpinner?.doNm == spinner_loc.selectedItem.toString()){
                            spinner_map.setSelection(selectedSpinner?.secondPosition!!)
                        }else{
                            spinner_map.setSelection(0)
                        }
                    }
                }

                spinner_map ->{
                    mRealmController
                            .setSelectedSpinnerItem(
                                    realm,
                                    spinner_loc.selectedItem.toString(),
                                    spinner_map.selectedItem.toString(),
                                    firstSpinnerPosition,
                                    pos
                            )
                }
            }
        }
    }

    private fun isEmptyRealmSpinner(): Boolean {
        if (mRealmController.getSpinnerItems(realm).isEmpty()) {
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
                    mRealmController.setSpinner(realm, gisMap!!)

                }, { e ->
                    Log.e(TAG, "result API response ===> error ${e.localizedMessage}")
                })
    }

    private val realmListener = object : RealmListener{
        override fun onSpinnerSuccess() {
            setSpinnerAdapter(spinner_loc, RealmController.instance.getSpinnerItems(realm))
        }

        override fun onTransactionSuccess() {
            if(!firstExecute){
                main_viewpager.adapter.notifyDataSetChanged()
            }
            firstExecute = false
        }
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
        private var firstExecute = true
    }
}
