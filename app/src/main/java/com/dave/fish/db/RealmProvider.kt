package com.dave.fish.db

import com.dave.fish.api.model.GisModel
import com.dave.fish.common.Constants
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

    fun setSelectSpinner(areaName: String,
                         postName: String,
                         keyTide: Int = Constants.KEY_TIDE_MAIN_SPINNER,
                         listener: ()->Unit = {}){

        val selectItemModel = SelectItemModel().apply {
            this.keyTide = keyTide
            this.doNm = areaName
            this.postName = postName
        }

        getRealm().use {
            it.executeTransactionAsync({
                it.copyToRealmOrUpdate(selectItemModel)
            },{
                // 트랜잭션이 성공하였습니다.
                realmListener?.onTransactionSuccess(listener)
            },{
                DLog.w("setSelectSpinner fail cause --> ${it.message}")
            })
        }
    }

    /**
     * @return - 전체 스피너 데이터 반환.
     */
    fun getSpinner(): List<SpinnerFirstModel>{
        val realm = getRealm()
        return realm.where(SpinnerFirstModel::class.java)
                .findAll().toList()
    }

    /**
     * @param keyTide 어디서 사용하는 Spinner인지 구분
     * @return - 마지막에 선택했던 Spinner 값 반환.
     *
     * Constants.KEY_TIDE_MAIN_SPINNER - 메인에서 사용
     * Constants.KEY_TIDE_SIDE_SPINNER - 사이드에서 사용
     */
    fun getSelectedItem(keyTide: Int = Constants.KEY_TIDE_MAIN_SPINNER): SelectItemModel{
        val realm = getRealm()
        return realm.where(SelectItemModel::class.java)
                .equalTo("keyTide", keyTide)
                .findAll()
                .toList()
                .first()
    }

    fun getSecondSpinnerItem(keyTide: Int = Constants.KEY_TIDE_MAIN_SPINNER): SpinnerSecondModel? {
        val selectedItem = getSelectedItem(keyTide)

        selectedItem.let { selected->
            return getSpinner()
                    .filter { it.areaName ==  selected.doNm }
                    .map { it.secondSpinnerItems }
                    .first()
                    .toList()
                    .find { it.obsPostName ==  selected.postName }
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

    fun <T> writeData(item: T){
        getRealm().use({
            it.beginTransaction()
            it.copyToRealmOrUpdate(item as RealmObject)
            it.commitTransaction()
        })
    }

    fun <T : RealmObject> copyData(e: T): RealmModel {
        val realm = getRealm()
        return realm.copyFromRealm(e)
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
