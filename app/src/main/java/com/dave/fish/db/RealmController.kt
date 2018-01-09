package com.dave.fish.db

import com.dave.fish.api.model.GisModel
import com.dave.fish.api.model.WeeklyModel
import com.dave.fish.db.model.SelectItemModel
import com.dave.fish.db.model.SpinnerFirstModel
import com.dave.fish.db.model.SpinnerSecondModel
import com.dave.fish.db.model.TideWeeklyModel
import io.realm.Realm
import io.realm.RealmResults
import org.joda.time.DateTime


/**
 * Created by soul on 2017. 9. 14..
 */
class RealmController {

    private var realmListener: RealmListener ?= null

    fun setSpinner(dataMap: Map<String, List<GisModel.Data>>) {
        val realm = Realm.getDefaultInstance()
        try{
            realm.executeTransactionAsync({ db ->
                val spinnerDataList: List<String> = dataMap.keys.toList().filter { d -> d != null && d != "황해남도" }.sorted()
                spinnerDataList.forEach { key ->
                    db.createObject(SpinnerFirstModel::class.java).apply {
                        areaName = key
                        val dataList = dataMap[key]
                        dataList?.forEach { (obsPostId, _, obsPostName, obsLat, obsLon) ->
                            val selectedSpinnerModel = db.createObject(SpinnerSecondModel::class.java)
                            selectedSpinnerModel.obsPostId = obsPostId
                            selectedSpinnerModel.obsPostName = obsPostName
                            selectedSpinnerModel.obsLat = obsLat
                            selectedSpinnerModel.obsLon = obsLon
                            secondSpinnerItems?.add(selectedSpinnerModel)
                        }
                    }
                }
            }, {
                // 트랜잭션이 성공하였습니다.
                realmListener?.onSpinnerSuccess()
            }, {
                // 트랜잭션이 실패했고 자동으로 취소되었습니다.
            })
        }finally {
            realm.close()
        }
    }

    fun getSelectedSpinnerItem(key: String): List<String>? {
        val realm = Realm.getDefaultInstance()
        try{
            return realm.where(SpinnerFirstModel::class.java)
                    .equalTo("areaName", key)
                    .findFirst().secondSpinnerItems?.map { it.obsPostName }
        }finally {
            realm.close()
        }
    }

    fun initSelectedSpinnerItem(key: String, postName: String, position1: Int, position2: Int){
        val realm = Realm.getDefaultInstance()
        try{
            realm.executeTransaction{ bgRealm ->
                val selectedItem = bgRealm.where(SelectItemModel::class.java).findFirst()
                if (null == selectedItem) {
                    bgRealm.createObject(SelectItemModel::class.java).apply {
                        doNm = key
                        this.postName = postName
                        this.firstPosition = position1
                        this.secondPosition = position2
                    }

                    bgRealm.insert(SelectItemModel().apply {
                        doNm = key
                        this.postName = postName
                        this.firstPosition = position1
                        this.secondPosition = position2
                        this.isTodayTide = true
                    })
                }
            }
        }finally {
            realm.close()
        }
    }

    fun setSelectedSpinnerItem(key: String,
                               postName: String,
                               position1: Int,
                               position2: Int,
                               isTodayTide : Boolean = false,
                               listener: ()->Unit = {}) {
        val realm = Realm.getDefaultInstance()
        try{
            realm.executeTransactionAsync({ bgRealm ->
                val selectedItem = bgRealm.where(SelectItemModel::class.java).equalTo("isTodayTide", isTodayTide).findFirst()
                selectedItem.run {
                    doNm = key
                    this.postName = postName
                    this.firstPosition = position1
                    this.secondPosition = position2
                }
            }, {
                // 트랜잭션이 성공하였습니다.
                realmListener?.onTransactionSuccess(listener)
            }) {
                // 트랜잭션이 실패했고 자동으로 취소되었습니다.
            }
        }finally {
            realm.close()
        }
    }

    fun findSelectedSecondModel(isTodayTide: Boolean = false): SpinnerSecondModel {
        val realm = Realm.getDefaultInstance()
        try{
            val selectedItem = realm.where(SelectItemModel::class.java)
                    .equalTo("isTodayTide", isTodayTide)
                    .findFirst()
            return findByPostName(selectedItem.doNm, selectedItem.postName)
        }finally {
            realm.close()
        }
    }

    fun findSelectedSpinnerItem(isTodayTide: Boolean = false): SelectItemModel {
        val realm = Realm.getDefaultInstance()
        try{
            return realm.where(SelectItemModel::class.java)
                    .equalTo("isTodayTide", isTodayTide)
                    .findFirst()
        }finally {
            realm.close()
        }
    }

    fun getSpinnerItems(): List<String>{
        val realm = Realm.getDefaultInstance()
        try{
            return realm.where(SpinnerFirstModel::class.java).findAll().map { result -> result.areaName }
        }finally {
            realm.close()
        }
    }


    private fun findByPostName(doNm: String, postName: String): SpinnerSecondModel {
        val realm = Realm.getDefaultInstance()
        try{
            return realm.where(SpinnerFirstModel::class.java)
                    .equalTo("areaName", doNm)
                    .findFirst()
                    .secondSpinnerItems.first { second -> second.obsPostName == postName }
        }finally {
            realm.close()
        }
    }

    private fun createTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String){
        val realm = Realm.getDefaultInstance()
        try{
            realm.executeTransactionAsync { db ->
                db.createObject(TideWeeklyModel::class.java, weeklyData.obsPostName + "_" + weeklyData.searchDate).apply {
                    this.postId = postId
                    this.am = weeklyData.am
                    this.dateMoon = weeklyData.dateMoon
                    this.dateSun = weeklyData.dateSun
                    this.flgView = weeklyData.flgView
                    this.lvl1 = weeklyData.lvl1
                    this.lvl2 = weeklyData.lvl2
                    this.lvl3 = weeklyData.lvl3
                    this.lvl4 = weeklyData.lvl4
                    this.mool7 = weeklyData.mool7
                    this.mool8 = weeklyData.mool8
                    this.moolNormal = weeklyData.moolNormal
                    this.obsPostName = weeklyData.obsPostName
                    this.obsLat = weeklyData.obsLat
                    this.obsLon = weeklyData.obsLon
                    this.searchDate = DateTime(weeklyData.searchDate).toDate()
                    this.temp = weeklyData.temp
                    this.weatherChar = weeklyData.weatherChar
                }
            }
        }finally {
            realm.close()
        }
    }

    private fun insertTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String){
        val realm = Realm.getDefaultInstance()
        try{
            realm.executeTransactionAsync { db ->
                val tideWeeklyModel = TideWeeklyModel()
                with(tideWeeklyModel){
                    key = weeklyData.obsPostName + "_" + weeklyData.searchDate
                    this.postId = postId
                    dateMoon = weeklyData.dateMoon
                    dateSun = weeklyData.dateSun
                    flgView = weeklyData.flgView
                    lvl1 = weeklyData.lvl1
                    lvl2 = weeklyData.lvl2
                    lvl3 = weeklyData.lvl3
                    lvl4 = weeklyData.lvl4
                    mool7 = weeklyData.mool7
                    mool8 = weeklyData.mool8
                    moolNormal = weeklyData.moolNormal
                    obsPostName = weeklyData.obsPostName
                    obsLat = weeklyData.obsLat
                    obsLon = weeklyData.obsLon
                    searchDate = DateTime(weeklyData.searchDate).toDate()
                    temp = weeklyData.temp
                    this?.am = weeklyData?.am
                    this?.weatherChar = weeklyData?.weatherChar
                }
                db.insertOrUpdate(tideWeeklyModel)
            }
        }finally {
            realm.close()
        }
    }

    private fun updateTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String){
        val realm = Realm.getDefaultInstance()
        try{
            val tideWeeklyModel = findTideWeekly(weeklyData.obsPostName + "_" + weeklyData.searchDate)
            realm.executeTransaction { db ->
                with(tideWeeklyModel){
                    this.postId = postId
                    dateMoon = weeklyData.dateMoon
                    dateSun = weeklyData.dateSun
                    flgView = weeklyData.flgView
                    lvl1 = weeklyData.lvl1
                    lvl2 = weeklyData.lvl2
                    lvl3 = weeklyData.lvl3
                    lvl4 = weeklyData.lvl4
                    mool7 = weeklyData.mool7
                    mool8 = weeklyData.mool8
                    moolNormal = weeklyData.moolNormal
                    obsPostName = weeklyData.obsPostName
                    obsLat = weeklyData.obsLat
                    obsLon = weeklyData.obsLon
                    searchDate = DateTime(weeklyData.searchDate).toDate()
                    temp = weeklyData.temp
                    this?.am = weeklyData?.am
                    this?.weatherChar = weeklyData?.weatherChar
                }
            }
        }finally {
            realm.close()
        }
    }

    fun setTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String) {
        val realm = Realm.getDefaultInstance()
        try{
            if (findSizeOfTideWeekly() > 0) {
                try{
                    updateTideWeekly(weeklyData, postId)
                }catch (e: Exception){
                    insertTideWeekly(weeklyData, postId)
                }
            } else {
                createTideWeekly(weeklyData, postId)
            }
        }finally {
            realm.close()
        }
    }

    fun findTideMonth(postId: String, date: DateTime) : RealmResults<TideWeeklyModel>{
        val realm = Realm.getDefaultInstance()
        try{
            val from = date.dayOfMonth().withMinimumValue().toDate()
            val to = date.dayOfMonth().withMaximumValue().toDate()

            return realm.where(TideWeeklyModel::class.java)
                    .between("searchDate"
                            , from
                            , to)
                    .equalTo("postId", postId)
                    .findAll()
        }finally {
            realm.close()
        }
    }

    fun findTideWeelyList() : RealmResults<TideWeeklyModel>{
        val realm = Realm.getDefaultInstance()
        try{
            return realm.where(TideWeeklyModel::class.java)
                    .findAll()
                    .sort("key")
        }finally {
            realm.close()
        }
    }

    fun findTideWeekly(key: String) : TideWeeklyModel {
        val realm = Realm.getDefaultInstance()
        try{
            return realm.where(TideWeeklyModel::class.java)
                    .equalTo("key", key)
                    .findFirst()
        }finally {
            realm.close()
        }
    }

    fun findSizeOfTideWeekly(): Int {
        val realm = Realm.getDefaultInstance()
        try{
            return realm.where(TideWeeklyModel::class.java).findAll().size
        }finally {
            realm.close()
        }
    }

    fun setListener(realmListener: RealmListener?) {
        this.realmListener = realmListener
    }

    private object Holder {
        val INSTANCE = RealmController()
    }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmController by lazy { Holder.INSTANCE }
    }
}
