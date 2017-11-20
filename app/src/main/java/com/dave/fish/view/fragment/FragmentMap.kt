package com.dave.fish.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.db.RealmController
import com.dave.fish.model.realm.SpinnerSecondModel
import com.dave.fish.util.DLog
import com.dave.fish.view.activity.DetailMapActivity
import com.dave.fish.view.service.LocationService
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_menu_two.*
import java.util.*


/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentMap : BaseFragment(),
        OnMapReadyCallback{

    private val mRealmController : RealmController = RealmController.instance
    private lateinit var selectedItem : SpinnerSecondModel
    private lateinit var receiver: BroadcastReceiver

    private lateinit var mMap: GoogleMap
    private var mapView: MapView? = null

    override fun getContentId(): Int = R.layout.fragment_menu_two

    private val locA = LatLng(37.4832, 126.421)
    private val locB = LatLng(37.4843, 126.522)
    private val locC = LatLng(37.4854, 126.623)
    private val locD = LatLng(37.4865, 126.724)
    private val locE = LatLng(37.4876, 126.825)
    private val locF = LatLng(37.4887, 126.926)

    override fun onLoadStart(savedInstanceState : Bundle?) {
        selectedItem = mRealmController.findSelectedSecondModel(realm)
        MapsInitializer.initialize(this.activity)

        val mapview = view?.findViewById<MapView>(R.id.google_map_view)
        mapview?.isClickable = false
        mapview?.onCreate(savedInstanceState)
        mapview?.onResume()
        mapview?.getMapAsync(this)

        val geocoder = Geocoder(context)
        val locationList = geocoder.getFromLocation(selectedItem.obsLat, selectedItem.obsLon, 10)
        locationList?.let {
            val address = try{
                locationList[0].getAddressLine(0).filterNot { c->resources.getString(R.string.korea).contains(c) }
            }catch (e : IndexOutOfBoundsException){
                resources.getString(R.string.warning_empty_address)
            }

            tv_record_address.text = address
        }

        btn_start_record.isSelected = LocationService.isRecordServiceStarting
        if(btn_start_record.isSelected){
            btn_start_record.text = resources.getString(R.string.record_stop)
        }else{
            btn_start_record.text = resources.getString(R.string.record_start)
        }

        btn_start_record.setOnClickListener {
            val isRecorded = btn_start_record.isSelected
            val intentService = Intent(activity, LocationService::class.java)
            intentService.putExtra(Constants.EXTRA_NOTIFIER, Constants.EXTRA_NOTIFICATION_ID)

            btn_start_record.isSelected = isRecorded.not()

            if(isRecorded){
                activity.stopService(intentService)
                btn_start_record.text = resources.getString(R.string.record_start)
            }else{
                activity.startService(intentService)
                btn_start_record.text = resources.getString(R.string.record_stop)
            }
        }
    }

    override fun onLoadContent() {
        // response location values from service
        receiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent) {
                val locationMsg = intent.getStringExtra(Constants.LOCATION_SERVICE_MESSAGE)
                val locationValues = intent.getParcelableArrayListExtra<Location>(Constants.RESPONSE_LOCATION_VALUES).map {
                    LatLng(it.latitude, it.longitude)
                }

                DLog.w("[locationValues]\nsize : ${locationValues.size}\npoints :$locationValues")
                val polyLine = mMap.addPolyline(PolylineOptions()
                        .addAll(locationValues)
                        .width(10f)
                        .color(Color.RED)
                        .geodesic(true))

                mMap.animateCamera(CameraUpdateFactory.newLatLng(locationValues.last()))
                mMap.setMinZoomPreference(19.0f)
                mMap.setMaxZoomPreference(23.0f)
                polyLine.tag = "내경로"

                tv_record_time.text = locationMsg

            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val mLatLng = LatLng(selectedItem.obsLat, selectedItem.obsLon)
        selectedItem = mRealmController.findSelectedSecondModel(realm)
        mMap = map
        val mapUtil = mMap.uiSettings
        mapUtil.isScrollGesturesEnabled = false
        mapUtil.isZoomGesturesEnabled = false

        mMap.setMinZoomPreference(12.0f)
        mMap.setMaxZoomPreference(15.0f)
        mMap.addMarker(MarkerOptions().position(mLatLng).title(selectedItem.obsPostName))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng))

        enableMyLocation()

        mMap.setOnMapClickListener {
            val detailMapIntent = Intent(activity, DetailMapActivity::class.java)
            detailMapIntent.putExtra("lat", selectedItem.obsLat)
            detailMapIntent.putExtra("lon", selectedItem.obsLon)
            startActivity(detailMapIntent)
        }
    }

    private var permissionlistener: PermissionListener = object : PermissionListener {
        @SuppressLint("MissingPermission")
        override fun onPermissionGranted() {
            mMap.isMyLocationEnabled = true
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableMyLocation() {
        TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(context).registerReceiver((receiver),
                IntentFilter(Constants.LOCATION_SERVICE_RESULT)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        super.onStop()
    }

    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    companion object {
        private val TAG = FragmentMap::class.java.simpleName
    }
}
