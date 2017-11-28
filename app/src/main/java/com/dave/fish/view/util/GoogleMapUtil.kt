package com.dave.fish.view.util

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import com.dave.fish.MyApplication
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*

/**
 * Created by soul on 2017. 10. 27..
 */
class GoogleMapUtil : GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback{

    private lateinit var mMap : GoogleMap
    private var lat = 0.0
    private var lon = 0.0

    fun initMap(mapView : MapView, lat: Double, lon:Double){
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon
    }

    fun initMap(mapView : SupportMapFragment, lat: Double, lon:Double){
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon
    }

    private fun enableMyLocation() {
        TedPermission.with(MyApplication.context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }


    private var permissionlistener: PermissionListener = object : PermissionListener {
        @SuppressLint("MissingPermission")
        override fun onPermissionGranted() {
            Toast.makeText(MyApplication.context, "Permission Granted", Toast.LENGTH_SHORT).show()
            mMap.isMyLocationEnabled = true
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            Toast.makeText(MyApplication.context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))

        return false
    }

    override fun onMyLocationClick(p0: Location) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        val mLatLng = LatLng(lat, lon)
        mMap?.run {
            setOnMyLocationButtonClickListener(this@GoogleMapUtil)
            setOnMyLocationClickListener(this@GoogleMapUtil)
            setMinZoomPreference(7.0f)
            setMaxZoomPreference(17.0f)
            addMarker(MarkerOptions().position(mLatLng))
            moveCamera(CameraUpdateFactory.newLatLng(mLatLng))
            moveCamera(CameraUpdateFactory.zoomTo(12.0f))
        }

        enableMyLocation()
    }


    private object Holder {
        val INSTANCE = GoogleMapUtil()
    }

    companion object {
        val TAG = GoogleMapUtil::class.java.simpleName
        val instance: GoogleMapUtil by lazy { Holder.INSTANCE }
    }
}