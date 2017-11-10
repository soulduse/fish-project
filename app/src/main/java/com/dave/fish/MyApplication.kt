package com.dave.fish

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager





/**
 * Created by soul on 2017. 9. 14..
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DEBUG = isDebuggable(this)

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)

        context = this
    }

    /**
     * get Debug Mode
     *
     * @param context
     * @return
     */
    private fun isDebuggable(context: Context): Boolean {
        var debuggable = false

        val pm = context.packageManager
        try {
            val appinfo = pm.getApplicationInfo(context.packageName, 0)
            debuggable = 0 != appinfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        } catch (e: PackageManager.NameNotFoundException) {
            /* debuggable variable will remain false */
        }

        return debuggable
    }

    companion object {
        var context: Context? = null
            private set

        var DEBUG = false
    }
}
