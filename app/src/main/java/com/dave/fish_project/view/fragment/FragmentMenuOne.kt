package com.dave.fish_project.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish_project.R
import com.dave.fish_project.model.WeeklyModel
import com.dave.fish_project.network.RetrofitController

/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentMenuOne : Fragment(){

    var tideData : WeeklyModel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_menu_one, container, false)
    }

    private fun initData(){
        RetrofitController().getWeeklyData()
                .subscribe({
                    tideModel->
                    var weeklyDataList = tideModel.weeklyDataList
                    for(item : WeeklyModel.WeeklyData in weeklyDataList!!){

                    }
                    Log.d(TAG, "used api")
                }, {
                    erorr ->
                    Log.d(TAG, "Something wrong")
                })
    }


    companion object {
        private val TAG = FragmentMenuOne.javaClass.simpleName
    }
}