package com.dave.fish.ui.map

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.LatLonModel
import com.dave.fish.db.model.LocationModel
import com.dave.fish.util.DLog
import com.dave.fish.ui.main.MainActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import io.realm.RealmList
import org.joda.time.DateTime
import java.text.DateFormat
import java.util.*


/**
 * Created by soul on 2017. 10. 29..
 */
class LocationService : Service() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private lateinit var broadcaster: LocalBroadcastManager

    private val locationList: ArrayList<Location> = arrayListOf()
    private lateinit var mLocation: Location
    private var requestingLocationUpdates = false
    private var lastUpdateTime = ""

    // Get intent values
    private var locationId: Long = 0L
    private var locationLat: Double = 0.0
    private var locationLon: Double = 0.0
    private var notificationId: Int = 0
    private var locationCreatedAt: String = ""

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

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        DLog.d("service [StartCommand]")
        with(intent){
            locationId = getLongExtra(Constants.EXTRA_LOCATION_MODEL_IDX, 0L)
            locationLat = getDoubleExtra(Constants.EXTRA_LOCATION_LAT, 0.0)
            locationLon = getDoubleExtra(Constants.EXTRA_LOCATION_LON, 0.0)
            notificationId = getIntExtra(Constants.EXTRA_NOTIFIER, 0)
            locationCreatedAt = getStringExtra(Constants.EXTRA_LOCATION_CREATED_AT) ?: ""
        }

        startLocationUpdates()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        DLog.w("service [DESTROY]")
        stopLocationUpdates()
        clearLocationList()
        super.onDestroy()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mLocation = locationResult.lastLocation

                // 위치 정보가 비정상적인 경우 값 저장안함
                if (!isBadLocation()){
                    addLocationList(mLocation)
                    lastUpdateTime = DateFormat.getTimeInstance().format(Date())
                    insertOrUpdateLocationModel()
                }

                sendResultLocation()
            }

            private fun isBadLocation(): Boolean {
                DLog.w("location value hasAccuracy: ${mLocation.hasAccuracy()}, accuracy: ${mLocation.accuracy}")
                if (!mLocation.hasAccuracy()) {
                    return true
                }

                if (mLocation.accuracy.toInt() >= 20) {
                    return true
                }
                return false
            }
        }
    }

    private fun sendResultLocation() {
        DLog.v("service [sendResultLocation]")



        broadcaster.sendBroadcast(Intent(Constants.LOCATION_SERVICE_RESULT))

        initForegroundService()
    }

    private fun insertOrUpdateLocationModel() {
        val latLonList = RealmList<LatLonModel>()
        getLocationList().forEach { item ->
            val latlonModel = LatLonModel().apply {
                latitude = item.latitude
                longtitude = item.longitude
            }
            latLonList.add(latlonModel)
        }

        val locationModel = LocationModel().apply {
            id = locationId
            locations = latLonList
            fixedLat = locationLat
            fixedLon = locationLon

            if(locationCreatedAt.isNotEmpty()){
                createdAt = DateTime(locationCreatedAt).toDate()
            }
        }

        DLog.w("""location values -->
                    id : ${locationModel.id}
                    createdAt : ${locationModel.createdAt}
                    updatedAt : ${locationModel.updatedAt}
                    fixedLat : ${locationModel.fixedLat}
                    fixedLon : ${locationModel.fixedLon}
                    locations : ${locationModel.locations?.toString()}
                    locations.size : ${locationModel.locations?.size}
                    """)

        RealmProvider.instance.writeData(locationModel)
    }

    private fun initForegroundService() {
        val mId = notificationId
        val mBuilder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("해루질앱")
                .setContentText("현재 내 위치를 기록하고 있습니다.")

        val resultIntent = Intent(this, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        startForeground(mId, mBuilder.build())
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        mLocationRequest.run {
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
            }

            addOnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val rae = e as ResolvableApiException
                            DLog.e(rae.localizedMessage)
                        } catch (sie: IntentSender.SendIntentException) {
                            DLog.i("PendingIntent unable to execute request.")
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

    private fun stopLocationUpdates() {
        if (!requestingLocationUpdates) {
            return
        }

        mFusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener {
                    requestingLocationUpdates = false
                }
    }

    private fun getLocationList(): ArrayList<Location> = locationList

    private fun addLocationList(location: Location) {
        locationList.add(location)
    }

    private fun clearLocationList() {
        locationList.clear()
    }

    companion object {
        private val INTERVAL: Long = 1000 * 10
        private val FASTEST_INTERVAL: Long = 1000 * 5
    }
}
