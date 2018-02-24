package com.dave.fish

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.dave.fish.ui.GlideApp
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by soul on 2017. 9. 14..
 */
class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        context = this

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)

    }

    override fun onLowMemory() {
        super.onLowMemory()
        GlideApp.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlideApp.get(this).trimMemory(level)
    }

    companion object {
        var context: Context ?= null
            private set

        val DEBUG = BuildConfig.DEBUG

        fun getGlobalContext(): MyApplication = context as MyApplication
    }
}
