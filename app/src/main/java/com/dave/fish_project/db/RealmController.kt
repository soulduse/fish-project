package com.dave.fish_project.db

import com.dave.fish_project.model.GisModel
import com.dave.fish_project.model.spinner.FirstSpinnerModel
import com.dave.fish_project.model.spinner.SecondSpinnerModel
import io.realm.Realm
import io.realm.RealmResults

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
                    dataList?.forEach { result->
                        var selectedSpinnerModel = db.createObject(SecondSpinnerModel::class.java)
                        selectedSpinnerModel.obsPostId = result.obsPostId
                        selectedSpinnerModel.obsPostName = result.obsPostName
                        selectedSpinnerModel.obsLat = result.obsLat
                        selectedSpinnerModel.obsLon = result.obsLon
                        secondSpinnerItems?.add(selectedSpinnerModel)
                    }
                }
            }
        }
    }

    fun getSpinnerItems(realm : Realm) : RealmResults<FirstSpinnerModel>{
        return realm.where(FirstSpinnerModel::class.java).findAll()
    }

    private object Holder { val INSTANCE = RealmController() }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmController by lazy { Holder.INSTANCE }
    }
}