package com.dave.fish_project.db

import com.dave.fish_project.model.GisModel
import com.dave.fish_project.model.spinner.SecondSpinnerModel
import io.realm.Realm

/**
 * Created by soul on 2017. 9. 14..
 */
class RealmController {



    fun setSpinnerItem(realm : Realm, data:GisModel.Data){
        realm.executeTransactionAsync {
            db->
            var selectedSpinnerModel = db.createObject(SecondSpinnerModel::class.java)
            selectedSpinnerModel.obsPostId = data.obsPostId
            selectedSpinnerModel.obsPostName = data.obsPostName
            selectedSpinnerModel.obsLat = data.obsLat
            selectedSpinnerModel.obsLon = data.obsLon
        }
    }

    private object Holder { val INSTANCE = RealmController() }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmController by lazy { Holder.INSTANCE }
    }
}