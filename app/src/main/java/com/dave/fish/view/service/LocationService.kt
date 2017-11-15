package com.dave.fish.view.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.dave.fish.util.DLog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import java.text.DateFormat
import java.util.*
import com.google.android.gms.location.LocationRequest
import android.R.attr.priority
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException



/**
 * Created by soul on 2017. 10. 29..
 */
class LocationService : Service() {

    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private lateinit var mSettingsClient : SettingsClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest : LocationSettingsRequest
    private lateinit var mGoogleApiClient : GoogleApiClient
    private lateinit var mLocation: Location

    private var requestingLocationUpdates = false
    private var lastUpdateTime = ""
    private var textLog = ""
    private var priority = 0
    // Binder given to clients
    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder(){
        fun getService() : LocationService{
            return this@LocationService
        }
    }

    override fun onBind(intent: Intent?): IBinder? = mBinder

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w(TAG, "service onStartCommand")
        Toast.makeText(applicationContext, "서비스 시작됨", Toast.LENGTH_LONG).show()
        startLocationUpdates()
        return START_NOT_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mLocation = locationResult.lastLocation
                lastUpdateTime = DateFormat.getTimeInstance().format(Date())
                updateLocationUI()
            }
        }
    }

    private fun updateLocationUI() {
        // getLastLocation()
        if (mLocation != null) {
            textLog = """
                ---------- UpdateLocation ----------
                Latitude=${mLocation.latitude}
                Longitude=${mLocation.longitude}
                Accuracy= ${mLocation.accuracy}
                Altitude= ${mLocation.altitude}
                Speed= ${mLocation.speed}
                Bearing= ${mLocation.bearing}
                Time= $lastUpdateTime """.trimMargin()
            DLog.w(textLog)
            Toast.makeText(this, textLog, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        priority = 0

        when (priority) {
            0 -> mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            1 -> mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            2 -> mLocationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
            else -> mLocationRequest.priority = LocationRequest.PRIORITY_NO_POWER
        }

        mLocationRequest.run {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            smallestDisplacement = SMALLEST_DISPLACEMENT
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).run {
            addOnSuccessListener {
                // **** need check permission and then update location ****
                DLog.d("""
                [ task success ]
                isLocationPresent - ${it.locationSettingsStates.isLocationPresent}
                isLocationUsable - ${it.locationSettingsStates.isLocationUsable}
                isNetworkLocationPresent - ${it.locationSettingsStates.isNetworkLocationPresent}
                isNetworkLocationUsable - ${it.locationSettingsStates.isNetworkLocationUsable}
                isBlePresent - ${it.locationSettingsStates.isBlePresent}
                isBleUsable - ${it.locationSettingsStates.isBleUsable}
                isGpsUsable - ${it.locationSettingsStates.isGpsUsable}
                isGpsPresent - ${it.locationSettingsStates.isGpsPresent}
                """)

                mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
            }

            addOnFailureListener { e->
                DLog.e("""
                task error --> $e,
                statusCode --> $e
                message --> ${e.message}
                statusMsg --> ${e.suppressed}
                cause --> ${e.cause}
                """)
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i("debug", "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                        try {
                            val rae = e as ResolvableApiException
                            DLog.e(rae.localizedMessage)
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i("debug", "PendingIntent unable to execute request.")
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        DLog.e(errorMessage)
                        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
                        requestingLocationUpdates = false
                    }
                }
            }
        }
    }

    companion object {
        val TAG = LocationService::class.java.simpleName
        private val INTERVAL : Long = 1000 //1 minute
        private val FASTEST_INTERVAL : Long = 1000 // 1 minute
        private val SMALLEST_DISPLACEMENT = 0.25f //quarter of a meter
    }
}
