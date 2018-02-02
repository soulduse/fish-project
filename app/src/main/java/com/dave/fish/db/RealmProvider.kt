package com.dave.fish.db

import com.dave.fish.api.model.GisModel
import com.dave.fish.api.model.WeeklyModel
import com.dave.fish.db.model.SelectItemModel
import com.dave.fish.db.model.SpinnerFirstModel
import com.dave.fish.db.model.SpinnerSecondModel
import com.dave.fish.db.model.TideWeeklyModel
import com.dave.fish.util.DLog
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import org.joda.time.DateTime


/**
 * Created by soul on 2017. 9. 14..
 */
class RealmProvider {

    private var realmListener: RealmListener ?= null

    private var realmObject: Realm? = null

    private fun getRealm(): Realm {
        if (realmObject == null || realmObject!!.isClosed) {
            realmObject = Realm.getDefaultInstance()
        }
        return realmObject!!
    }

    fun setInitSpinner(dataMap: Map<String, List<GisModel.Data>>) {
        val firstTitles: List<String> = dataMap.keys.toList()
        // 첫번째 스피너 생성
        firstTitles.forEach { areaNameKey ->
            val firstObj = SpinnerFirstModel().apply {
                id = dataMap[areaNameKey]
                        ?.find { it.doNm == areaNameKey }
                        ?.obsPostId!!
                areaName = areaNameKey

                // 두번째 스피너 생성
                val secondModel = dataMap[areaNameKey]
                secondModel?.forEach {
                    val secondObj = SpinnerSecondModel().apply {
                        obsPostId =  it.obsPostId
                        obsPostName = it.obsPostName
                        obsLat = it.obsLat
                        obsLon = it.obsLon
                    }
                    secondSpinnerItems.add(secondObj)
                }
            }

            getRealm().use {
                it.executeTransactionAsync({
                    it.copyToRealmOrUpdate(firstObj)
                },{
                    // 트랜잭션이 성공하였습니다.
                    realmListener?.onSpinnerSuccess()
                },{
                    // 트랜잭션이 실패했고 자동으로 취소되었습니다.
                })
            }
        }
    }

    fun setSelectSpinner(key: String,
                         postName: String,
                         isTodayTide: Boolean = false,
                         listener: ()->Unit = {}){

        val selectItemModel = SelectItemModel().apply {
            this.key = 0
            this.doNm = key
            this.postName = postName
            this.isTodayTide = isTodayTide
        }

        getRealm().use {
            it.executeTransactionAsync({
                it.copyToRealmOrUpdate(selectItemModel)
                DLog.w("setSelectSpinner set")
            },{
                // 트랜잭션이 성공하였습니다.
                realmListener?.onTransactionSuccess(listener)
                DLog.w("setSelectSpinner success")
            },{
                DLog.w("setSelectSpinner fail cause --> ${it.message}")
            })
        }
    }

    fun findSelectedSecondModel(isTodayTide: Boolean = false): SpinnerSecondModel {
        val realm = getRealm()
        val selectedItem = realm.where(SelectItemModel::class.java)
                .equalTo("isTodayTide", isTodayTide)
                .findFirst()

        DLog.w("findSelectedSecondModel doNm : ${selectedItem.doNm}, postName: ${selectedItem.postName}")
        return findByPostName(selectedItem.doNm, selectedItem.postName)
    }

    fun getSelectedItem(isTodayTide: Boolean = false): SelectItemModel? {
        val realm = getRealm()
        return realm.where(SelectItemModel::class.java)
                .equalTo("isTodayTide", isTodayTide)
                .findFirst()
    }

    fun getSpinner(): List<SpinnerFirstModel>{
        val realm = getRealm()
        return realm.where(SpinnerFirstModel::class.java)
                .findAll().toList()
    }

    private fun findByPostName(doNm: String, postName: String): SpinnerSecondModel {
        val realm = getRealm()
        return realm.where(SpinnerFirstModel::class.java)
                .equalTo("areaName", doNm)
                .findFirst()
                .secondSpinnerItems.first { second -> second.obsPostName == postName }
    }

    private fun createTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String){
        getRealm().use {
            it.executeTransactionAsync { db ->
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
        }
    }

    private fun insertTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String){
        getRealm().use {
            it.executeTransactionAsync { db ->
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
        }
    }

    private fun updateTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String){
        getRealm().use {
            val tideWeeklyModel = findTideWeekly(weeklyData.obsPostName + "_" + weeklyData.searchDate)
            it.executeTransaction { db ->
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
        }
    }

    fun setTideWeekly(weeklyData: WeeklyModel.WeeklyData, postId: String) {
        getRealm().use {
            if (findSizeOfTideWeekly() > 0) {
                try{
                    updateTideWeekly(weeklyData, postId)
                }catch (e: Exception){
                    insertTideWeekly(weeklyData, postId)
                }
            } else {
                createTideWeekly(weeklyData, postId)
            }
        }
    }

    fun findTideMonth(postId: String, date: DateTime) : RealmResults<TideWeeklyModel>{
        val realm = getRealm()
        val from = date.dayOfMonth().withMinimumValue().toDate()
        val to = date.dayOfMonth().withMaximumValue().toDate()

        return realm.where(TideWeeklyModel::class.java)
                .between("searchDate"
                        , from
                        , to)
                .equalTo("postId", postId)
                .findAll()
    }

    fun findTideWeelyList() : RealmResults<TideWeeklyModel>{
        getRealm().use {
            return it.where(TideWeeklyModel::class.java)
                    .findAll()
                    .sort("key")
        }
    }

    fun findTideWeekly(key: String) : TideWeeklyModel {
        val realm = getRealm()
        return realm.where(TideWeeklyModel::class.java)
                .equalTo("key", key)
                .findFirst()
    }

    private fun findSizeOfTideWeekly(): Int {
        val realm = getRealm()
        return realm.where(TideWeeklyModel::class.java).findAll().size
    }

    fun setListener(realmListener: RealmListener?) {
        this.realmListener = realmListener
    }

    fun <T : RealmObject> copyData(e: T): RealmModel {
        val realm = getRealm()
        return realm.copyFromRealm(e)
    }

    fun <T : RealmObject> copyDataOrUpdate(e: T): RealmModel {
        val realm = getRealm()
        return realm.copyToRealmOrUpdate(e)
    }

    fun closeRealm() {
        val realm = getRealm()
        if (realm!!.isClosed)
            return

        realm.close()
    }

    fun clearAll() {
        getRealm().use { it.deleteAll() }
    }

    private object Holder {
        val INSTANCE = RealmProvider()
    }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmProvider by lazy { Holder.INSTANCE }
    }
}
