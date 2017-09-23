package com.dave.fish_project.db

import android.support.v4.view.PagerAdapter
import android.util.Log
import com.dave.fish_project.model.GisModel
import com.dave.fish_project.model.spinner.FirstSpinnerModel
import com.dave.fish_project.model.spinner.SecondSpinnerModel
import com.dave.fish_project.model.spinner.SelectItemModel
import io.realm.Realm



/**
 * Created by soul on 2017. 9. 14..
 */
class RealmController {

    private lateinit var realmListener: RealmListener

    fun setSpinner(realm : Realm, dataMap : Map<String, List<GisModel.Data>>){
        realm.executeTransactionAsync({db->
            val spinnerDataList : List<String> = dataMap.keys.toList().filter { d -> d!=null && d != "황해남도" }.sorted()
            spinnerDataList.forEach { key->
                db.createObject(FirstSpinnerModel::class.java).apply {
                    areaName = key
                    val dataList = dataMap[key]
                    dataList?.forEach { (obsPostId, _, obsPostName, obsLat, obsLon) ->
                        val selectedSpinnerModel = db.createObject(SecondSpinnerModel::class.java)
                        selectedSpinnerModel.obsPostId = obsPostId
                        selectedSpinnerModel.obsPostName = obsPostName
                        selectedSpinnerModel.obsLat = obsLat
                        selectedSpinnerModel.obsLon = obsLon
                        secondSpinnerItems?.add(selectedSpinnerModel)
                    }
                }
            }
        },{
            // 트랜잭션이 성공하였습니다.
            realmListener.onSpinnerSuccess()
        },{
            // 트랜잭션이 실패했고 자동으로 취소되었습니다.
        })
    }

    fun getSelectedSpinnerItem(realm: Realm, key : String): List<String>? {
        return realm.where(FirstSpinnerModel::class.java)
                .equalTo("areaName", key)
                .findFirst().secondSpinnerItems?.map { it.obsPostName }
    }

    fun setSelectedSpinnerItem(realm : Realm, key: String, postName: String, position1 : Int, position2 : Int){
        realm.executeTransactionAsync({ bgRealm ->
            var selectedItem = bgRealm.where(SelectItemModel::class.java).findFirst()
            if(null == selectedItem){
                bgRealm.createObject(SelectItemModel::class.java).apply {
                    doNm = key
                    this.postName = postName
                    this.firstPosition = position1
                    this.secondPosition = position2
                }
            }else{
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

    fun findSelectedSpinnerItem(realm : Realm) : SelectItemModel?{
        return realm.where(SelectItemModel::class.java)
                ?.findFirst()
    }

    fun getSpinnerItems(realm : Realm) : List<String>{
        return realm.where(FirstSpinnerModel::class.java).findAll().map { result->result.areaName }
    }

    fun findByPostName(realm : Realm, doNm : String, postName: String) : SecondSpinnerModel?{
        return realm.where(FirstSpinnerModel::class.java)
                .equalTo("areaName", doNm)
                .findFirst()
                .secondSpinnerItems?.first{ second -> second.obsPostName == postName}
    }

    fun setListener(realmListener: RealmListener) {
        this.realmListener = realmListener
    }

    private object Holder { val INSTANCE = RealmController() }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmController by lazy { Holder.INSTANCE }
    }


}