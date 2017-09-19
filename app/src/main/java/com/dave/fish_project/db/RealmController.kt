package com.dave.fish_project.db

import com.dave.fish_project.model.GisModel
import com.dave.fish_project.model.spinner.FirstSpinnerModel
import com.dave.fish_project.model.spinner.SecondSpinnerModel
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

    fun getSpinnerItems(realm : Realm) : List<String>{
        return realm.where(FirstSpinnerModel::class.java).findAll().map { result->result.areaName }
    }

    private object Holder { val INSTANCE = RealmController() }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmController by lazy { Holder.INSTANCE }
    }
}