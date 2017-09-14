package com.dave.fish_project

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration



/**
 * Created by soul on 2017. 9. 14..
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }
}