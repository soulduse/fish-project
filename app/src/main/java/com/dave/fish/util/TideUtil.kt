package com.dave.fish.util

import com.dave.fish.model.realm.TideWeeklyModel
import com.dave.fish.model.retrofit.WeeklyModel
import java.util.*

/**
 * Created by soul on 2017. 10. 4..
 */
object TideUtil{

    private val LOW = "저"
    private val HIGH = "고"
    private val EMPTY = "-"
    private val SPLITE = "/"
    private var lvlLowItemList: MutableList<String> = mutableListOf()
    private var lvlHighItemList: MutableList<String> = mutableListOf()

    fun setTide(tideWeekly: Any){
        clearTideList()

        when (tideWeekly) {
            is TideWeeklyModel -> {
                makeTideList(tideWeekly.lvl1)
                makeTideList(tideWeekly.lvl2)
                makeTideList(tideWeekly.lvl3)
                makeTideList(tideWeekly.lvl4)
            }
            is WeeklyModel.WeeklyData -> {
                makeTideList(tideWeekly.lvl1)
                makeTideList(tideWeekly.lvl2)
                makeTideList(tideWeekly.lvl3)
                makeTideList(tideWeekly.lvl4)
            }
        }
    }

    private fun clearTideList(){
        if(lvlLowItemList.isNotEmpty()){
            lvlLowItemList.clear()
        }

        if(lvlHighItemList.isNotEmpty()){
            lvlHighItemList.clear()
        }
    }

    private fun makeTideList(lvl: String){
        when {
            LOW in lvl -> {
                lvlLowItemList.add(lvl)
            }
            HIGH in lvl -> {
                lvlHighItemList.add(lvl)
            }
            EMPTY in lvl -> {

            }
        }
    }

    fun getTime(lvl : String) : String{
        return lvl.split(SPLITE).first()
    }

    fun getHeight(lvl : String) : String{
        return lvl.split(SPLITE).last()
    }

    fun getLowItemList() : MutableList<String>{
        return lvlLowItemList
    }

    fun getHighItemList() : MutableList<String>{
        return lvlHighItemList
    }
}