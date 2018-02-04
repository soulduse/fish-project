package com.dave.fish.util

import com.dave.fish.api.model.WeeklyModel
import com.dave.fish.db.model.TideWeeklyModel
import com.google.gson.Gson

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

    // TODO 중복 처리를 하나로 합칠 수 있지 않을까?
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

    private fun getTideWeeklyFromJson(tideWeekly: Any):TideWeeklyModel{
        val gson = Gson()
        val tideWeekly = gson.toJson(tideWeekly)
        return gson.fromJson<TideWeeklyModel>(tideWeekly, TideWeeklyModel::class.java)
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

    fun getTime(lvl : String) : String = lvl.split(SPLITE).first()

    fun getHeight(lvl : String) : String = lvl.split(SPLITE).last()

    fun getLowItemList() : MutableList<String> = lvlLowItemList

    fun getHighItemList() : MutableList<String> = lvlHighItemList
}
