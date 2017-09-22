package com.dave.fish_project.db

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

    fun setSpinner(realm : Realm, dataMap : Map<String, List<GisModel.Data>>){
        realm.executeTransactionAsync {
            db->
            var spinnerDataList : List<String> = dataMap?.keys?.toList()?.filter { d-> d!=null && d!="황해남도" }!!.sorted()
            spinnerDataList.forEach { key->
                db.createObject(FirstSpinnerModel::class.java).apply {
                    areaName = key
                    var dataList = dataMap[key]
                    dataList?.forEach { (obsPostId, _, obsPostName, obsLat, obsLon) ->
                        var selectedSpinnerModel = db.createObject(SecondSpinnerModel::class.java)
                        selectedSpinnerModel.obsPostId = obsPostId
                        selectedSpinnerModel.obsPostName = obsPostName
                        selectedSpinnerModel.obsLat = obsLat
                        selectedSpinnerModel.obsLon = obsLon
                        secondSpinnerItems?.add(selectedSpinnerModel)
                    }
                }
            }
        }
    }

    fun getSelectedSpinnerItem(realm: Realm, key : String): List<String>? {
        return realm.where(FirstSpinnerModel::class.java)
                .equalTo("areaName", key)
                .findFirst().secondSpinnerItems?.map { it.obsPostName }
    }

    fun getPostId(realm : Realm, postName : String): String?{
        return realm.where(SecondSpinnerModel::class.java)
                .equalTo("obsPostName", postName)
                .findFirst().obsPostId
    }

    fun setSelectedSpinnerItem(realm : Realm, key: String, postName: String){
        realm.executeTransactionAsync {
            var selectedItem = it.where(SelectItemModel::class.java).findFirst()
            if(null == selectedItem){
                it.createObject(SelectItemModel::class.java).apply {
                    firstSpinner = key
                    secondSpinner = postName
                }
            }else{
                selectedItem.apply {
                    firstSpinner = key
                    secondSpinner = postName
                }
            }
            Log.d(TAG, "selectedItem --> $selectedItem")
        }
    }

    fun getSelectedSpinnerItem(realm: Realm) : SelectItemModel{
        return realm.where(SelectItemModel::class.java)
                .findFirst()
    }

    fun getSpinnerItems(realm : Realm) : List<String>{
        return realm.where(FirstSpinnerModel::class.java).findAll().map { result->result.areaName }
    }

    private object Holder { val INSTANCE = RealmController() }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmController by lazy { Holder.INSTANCE }
    }
}