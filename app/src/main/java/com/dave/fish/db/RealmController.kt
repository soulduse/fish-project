package com.dave.fish.db

import android.util.Log
import com.dave.fish.model.retrofit.GisModel
import com.dave.fish.model.realm.SpinnerFirstModel
import com.dave.fish.model.realm.SpinnerSecondModel
import com.dave.fish.model.realm.SelectItemModel
import com.dave.fish.model.realm.TideWeeklyModel
import com.dave.fish.model.retrofit.WeeklyModel
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults


/**
 * Created by soul on 2017. 9. 14..
 */
class RealmController {

    private lateinit var realmListener: RealmListener

    fun setSpinner(realm: Realm, dataMap: Map<String, List<GisModel.Data>>) {
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
            realmListener.onSpinnerSuccess()
        }, {
            // 트랜잭션이 실패했고 자동으로 취소되었습니다.
        })
    }

    fun getSelectedSpinnerItem(realm: Realm, key: String): List<String>? {
        return realm.where(SpinnerFirstModel::class.java)
                .equalTo("areaName", key)
                .findFirst().secondSpinnerItems?.map { it.obsPostName }
    }

    fun setSelectedSpinnerItem(realm: Realm, key: String, postName: String, position1: Int, position2: Int) {
        realm.executeTransactionAsync({ bgRealm ->
            var selectedItem = bgRealm.where(SelectItemModel::class.java).findFirst()
            if (null == selectedItem) {
                bgRealm.createObject(SelectItemModel::class.java).apply {
                    doNm = key
                    this.postName = postName
                    this.firstPosition = position1
                    this.secondPosition = position2
                }
            } else {
                selectedItem.run {
                    doNm = key
                    this.postName = postName
                    this.firstPosition = position1
                    this.secondPosition = position2
                }
            }
            Log.d(TAG, "selectedItem --> $selectedItem")
        }, {
            // 트랜잭션이 성공하였습니다.
            realmListener.onTransactionSuccess()
        }) {
            // 트랜잭션이 실패했고 자동으로 취소되었습니다.
        }
    }

    fun findSelectedSpinnerItem(realm: Realm): SelectItemModel? {
        return realm.where(SelectItemModel::class.java)
                ?.findFirst()
    }

    fun getSpinnerItems(realm: Realm): List<String> {
        return realm.where(SpinnerFirstModel::class.java).findAll().map { result -> result.areaName }
    }

    fun findByPostName(realm: Realm, doNm: String, postName: String): SpinnerSecondModel? {
        return realm.where(SpinnerFirstModel::class.java)
                .equalTo("areaName", doNm)
                .findFirst()
                .secondSpinnerItems?.first { second -> second.obsPostName == postName }
    }

    fun setTideWeekly(realm: Realm, weeklyData: WeeklyModel.WeeklyData) {
        if (findSizeOfTideWeekly(realm) > 0) {
            realm.executeTransactionAsync { db ->
                val tideWeeklyModel = TideWeeklyModel()
                tideWeeklyModel.key = weeklyData.obsPostName + "_" + weeklyData.searchDate
                tideWeeklyModel.dateMoon = weeklyData.dateMoon
                tideWeeklyModel.dateSun = weeklyData.dateSun
                tideWeeklyModel.flgView = weeklyData.flgView
                tideWeeklyModel.lvl1 = weeklyData.lvl1
                tideWeeklyModel.lvl2 = weeklyData.lvl2
                tideWeeklyModel.lvl3 = weeklyData.lvl3
                tideWeeklyModel.lvl4 = weeklyData.lvl4
                tideWeeklyModel.mool7 = weeklyData.mool7
                tideWeeklyModel.mool8 = weeklyData.mool8
                tideWeeklyModel.moolNormal = weeklyData.moolNormal
                tideWeeklyModel.obsPostName = weeklyData.obsPostName
                tideWeeklyModel.obsLat = weeklyData.obsLat
                tideWeeklyModel.obsLon = weeklyData.obsLon
                tideWeeklyModel.searchDate = weeklyData.searchDate
                tideWeeklyModel.temp = weeklyData.temp
                tideWeeklyModel?.am = weeklyData?.am
                tideWeeklyModel?.weatherChar = weeklyData?.weatherChar
                db.insertOrUpdate(tideWeeklyModel)
            }
        } else {
            realm.executeTransactionAsync { db ->
                db.createObject(TideWeeklyModel::class.java, weeklyData.obsPostName + "_" + weeklyData.searchDate).apply {
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
                    this.searchDate = weeklyData.searchDate
                    this.temp = weeklyData.temp
                    this.weatherChar = weeklyData.weatherChar
                }
            }
        }
    }

    fun findTideWeelyList(realm: Realm) : RealmResults<TideWeeklyModel>{
        return realm.where(TideWeeklyModel::class.java)
                .findAll()
                .sort("key")
    }

    fun findTideWeekly(realm: Realm, key: String) : TideWeeklyModel{
        return realm.where(TideWeeklyModel::class.java)
                .equalTo("key", key)
                .findFirst()
    }

    fun findSizeOfTideWeekly(realm: Realm): Int {
        return realm.where(TideWeeklyModel::class.java).findAll().size
    }

    fun setListener(realmListener: RealmListener) {
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