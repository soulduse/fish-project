package com.dave.fish.view.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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


/**
 * Created by soul on 2017. 10. 29..
 */
class LocationService : Service() {

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mGoogleApiClient : GoogleApiClient
    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    var mCurrentLocation: Location? = null

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        Log.w(TAG, "service onCreate")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val lat = it.latitude
                val lon = it.longitude


            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w(TAG, "service onStartCommand")
        mFusedLocationClient.requestLocationUpdates()
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, locationListener)
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, locationListener)
        Toast.makeText(applicationContext, "서비스 시작됨", Toast.LENGTH_LONG).show()
        locationSetting()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
        }
    }

    private fun createLocationRequest() : LocationRequest{
        val mLocationRequest = LocationRequest()
        mLocationRequest.run {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            smallestDisplacement = SMALLEST_DISPLACEMENT //added
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        return mLocationRequest
    }

    private fun isGooglePlayServicesAvailable() : Boolean{
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        return code == ConnectionResult.SUCCESS
    }

    private fun setGoogleApi(){
        mGoogleApiClient = GoogleApiClient.Builder(applicationContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build()
    }


    private fun locationSetting(){
        val builder = LocationSettingsRequest
                .Builder()
                .addLocationRequest(createLocationRequest())

        builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
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
        }

        task.addOnFailureListener {

            val statusCode = (it as ApiException).statusCode
            DLog.e("""
                task error --> $it,
                statusCode --> $statusCode
                message --> ${it.message}
                statusMsg --> ${it.statusMessage}
                cause --> ${it.cause}
                """)
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest.interval = UPDATE_INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
            }
        }, Looper.myLooper())
    }

    fun onLocationChanged(location: Location?) {
        // GPS may be turned off
        if (location == null) {
            return
        }

        // Report to the UI that the location was updated
        mCurrentLocation = location
        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    private val connectionCallbacks = object : GoogleApiClient.ConnectionCallbacks{
        override fun onConnected(p0: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onConnectionSuspended(p0: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val connectionFailedListener = GoogleApiClient.OnConnectionFailedListener {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val TAG = LocationService::class.java.simpleName
        private val INTERVAL : Long = 1000 //1 minute
        private val FASTEST_INTERVAL : Long = 1000 // 1 minute
        private val SMALLEST_DISPLACEMENT = 0.25f //quarter of a meter
    }
}
