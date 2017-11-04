package com.dave.fish.view.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult

/**
 * Created by soul on 2017. 10. 29..
 */
class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mGoogleApiClient : GoogleApiClient

    override fun onCreate() {
        super.onCreate()
        Log.w(TAG, "service onCreate")
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w(TAG, "service onStartCommand")
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0f, locationListener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, locationListener)
        Toast.makeText(applicationContext, "서비스 시작됨", Toast.LENGTH_LONG).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val locationListener = object : LocationListener {
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }

        override fun onLocationChanged(location: Location) {
            val lat: Double = location.latitude
            val lon: Double = location.longitude

            Toast.makeText(applicationContext, "onLocationChanged : lat --> $lat, lon --> $lon", Toast.LENGTH_LONG).show()
            Log.d(TAG, "onLocationChanged : lat --> $lat, lon --> $lon")
        }
    }

    private fun createLocationRequest() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.run {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            smallestDisplacement = SMALLEST_DISPLACEMENT //added
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
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
        val builder : LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
//        val result : PendingResult

        val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result?.setResultCallback(object : ResultCallback<LocationSettingsResult> {
            override fun onResult(p0: LocationSettingsResult) {
//                listener.onResult(locationSettingsResult)
            }
        })
    }


    /*
    private LocationSetListener locationSettingsListener = new LocationSetListener() {
        @Override
        public void onResult(LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                GpsManager.getInstance().startLocationUpdates();
                break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    if (status.hasResolution()) {
                        status.startResolutionForResult(activity, 1000);
                    }
                } catch (IntentSender.SendIntentException e) {

            }
                break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                ToastUtil.getInstance().show("unavailiable");
                break;
            }
        }
    }
    */

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
        private val INTERVAL : Long = 1000 * 60 //1 minute
        private val FASTEST_INTERVAL : Long = 1000 * 60 // 1 minute
        private val SMALLEST_DISPLACEMENT = 0.25f //quarter of a meter
    }
}
