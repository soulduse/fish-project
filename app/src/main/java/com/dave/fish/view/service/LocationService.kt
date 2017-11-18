package com.dave.fish.view.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.dave.fish.common.Constants
import com.dave.fish.util.DLog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.text.DateFormat
import java.util.*
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.dave.fish.R
import com.dave.fish.view.activity.MainActivity


/**
 * Created by soul on 2017. 10. 29..
 */
class LocationService : Service() {

    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private lateinit var mSettingsClient : SettingsClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest : LocationSettingsRequest
    private lateinit var broadcaster : LocalBroadcastManager

    private lateinit var mLocation: Location
    private var requestingLocationUpdates = false
    private var lastUpdateTime = ""
    private var textLog = ""
    private var priority = 0

    private lateinit var intent : Intent

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
        broadcaster = LocalBroadcastManager.getInstance(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        DLog.d("service [StartCommand]")
        isRecordServiceStarting = true
        this.intent = intent
        startLocationUpdates()

        return START_NOT_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        DLog.w("service [STOP]")
        return super.stopService(name)
    }

    override fun onDestroy() {
        DLog.w("service [DESTROY]")
        isRecordServiceStarting = true
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun initForegroundService(){
        val mId = this.intent.getIntExtra(Constants.EXTRA_NOTIFIER, 0)
        val mBuilder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("해루질앱")
                .setContentText("현재 내 위치를 기록하고 있습니다.")
        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(this, MainActivity::class.java)
        DLog.w("mId value --> $mId")
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(this)
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity::class.java)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // mId allows you to update the notification later on.
//            mNotificationManager.notify(mId, mBuilder.build())
        startForeground(mId, mBuilder.build())
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mLocation = locationResult.lastLocation
                lastUpdateTime = DateFormat.getTimeInstance().format(Date())
                sendResultLocation()
            }
        }
    }

    private fun sendResultLocation() {
        // getLastLocation()
        if (mLocation != null) {
            DLog.v("service [sendResultLocation]")
            textLog = """
                ---------- UpdateLocation ----------
                Latitude=${mLocation.latitude}
                Longitude=${mLocation.longitude}
                Accuracy= ${mLocation.accuracy}
                Altitude= ${mLocation.altitude}
                Speed= ${mLocation.speed}
                Bearing= ${mLocation.bearing}
                Time= $lastUpdateTime """.trimMargin()

            val intent = Intent(Constants.LOCATION_SERVICE_RESULT)
            intent.putExtra(Constants.LOCATION_SERVICE_MESSAGE, textLog)
            intent.putExtra(Constants.RESPONSE_LOCATION_VALUES, mLocation)
            broadcaster.sendBroadcast(intent)

            initForegroundService()
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
    private fun startLocationUpdates() {
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

        requestingLocationUpdates = true
    }

    private fun stopLocationUpdates(){
        if(!requestingLocationUpdates){
            return
        }

        mFusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener {
                    requestingLocationUpdates = false
                }
    }

    companion object {
        val TAG = LocationService::class.java.simpleName
        private val INTERVAL : Long = 1000 //1 minute
        private val FASTEST_INTERVAL : Long = 1000 // 1 minute
        private val SMALLEST_DISPLACEMENT = 0.25f //quarter of a meter
        var isRecordServiceStarting = false
    }
}
