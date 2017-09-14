package com.dave.fish_project.db

import io.realm.Realm

/**
 * Created by soul on 2017. 9. 14..
 */
class RealmController {

    fun selectedSpinner(realm : Realm){

    }

    fun set(realm : Realm){

    }

    private object Holder { val INSTANCE = RealmController() }

    companion object {
        private val TAG = javaClass.simpleName
        val instance: RealmController by lazy { Holder.INSTANCE }
    }
}