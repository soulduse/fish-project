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

    private val polyLineOptions : PolylineOptions = PolylineOptions()
    private lateinit var polyLine : Polyline

    override fun getContentId(): Int = R.layout.fragment_menu_two

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
//                val locationMsg = intent?.getStringExtra(Constants.LOCATION_SERVICE_MESSAGE)
                val locationMsg = intent.getStringExtra(Constants.LOCATION_SERVICE_MESSAGE)
                val locationValues = intent.getParcelableExtra<Location>(Constants.RESPONSE_LOCATION_VALUES)
                val latLng = LatLng(
                        locationValues.latitude,
                        locationValues.longitude
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.setMinZoomPreference(15.0f)
                mMap.setMaxZoomPreference(20.0f)

                polyLineOptions.add(latLng)
                        .width(5f)
                        .color(Color.RED)
                        .geodesic(true)
                polyLine = mMap.addPolyline(polyLineOptions)
                polyLine.tag = "내경로"

                enableMyLocation()

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

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
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
