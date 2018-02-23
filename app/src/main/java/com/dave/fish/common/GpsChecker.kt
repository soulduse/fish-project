package com.dave.fish.common

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import com.dave.fish.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton


/**
 * Created by soul on 2018. 2. 23..
 */
object GpsChecker {

    fun isGPSProviderEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun alertOnGps(context: Context) {
        context.alert {
            message = context.resources.getString(R.string.unable_use_gps)
            positiveButton(context.resources.getString(R.string.setting)) {
                val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(callGPSSettingIntent)
            }
            noButton { }
        }.show()
    }
}