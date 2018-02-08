package com.dave.fish.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.widget.Toast
import com.dave.fish.MyApplication
import com.dave.fish.common.DistanceUtil
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
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
    private var minZoom = 0.0f
    private var maxZoom = 0.0f
    private var locationValues: MutableList<LatLng> = mutableListOf()
    private var drawTrigger: ()-> Boolean = { false }

    fun initMap(mapView : MapView, lat: Double, lon:Double): GoogleMapUtil{
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon

        return this@GoogleMapUtil
    }

    fun initMap(mapView : SupportMapFragment, lat: Double, lon:Double): GoogleMapUtil{
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon

        return this@GoogleMapUtil
    }

    fun initPolyLine(locationValues: List<LatLng>?): GoogleMapUtil{
        locationValues?.let {
            this.locationValues = locationValues.toMutableList()
        }
        return this@GoogleMapUtil
    }

    fun setStartListener(drawTrigger: ()-> Boolean): GoogleMapUtil{
        this.drawTrigger = drawTrigger
        return this@GoogleMapUtil
    }

    /**
     * Map Fragment     -> 12.0f / 15.0f
     * Detail Activity  -> 8.0f / 21.0f
     */
    fun initZoomLevel(minZoom: Float, maxZoom: Float): GoogleMapUtil{
        this.minZoom = minZoom
        this.maxZoom = maxZoom
        return this@GoogleMapUtil
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

    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        val mLatLng = LatLng(lat, lon)
        mMap.run {
            setOnMyLocationButtonClickListener(this@GoogleMapUtil)
            setOnMyLocationClickListener(this@GoogleMapUtil)
            addMarker(MarkerOptions().position(mLatLng))
            moveCamera(CameraUpdateFactory.newLatLng(mLatLng))
            addMarker(MarkerOptions().position(mLatLng))
            moveCamera(CameraUpdateFactory.zoomTo(12.0f))
        }

        if(drawTrigger()){
            drawPolyLine()
        }
        enableMyLocation()
    }

    private fun drawPolyLine(){
        if(locationValues.isNotEmpty()){
            addPolyLine()
            moveCamera()
            setZoom()
        }
    }

    private fun addPolyLine() {
        mMap.addPolyline(PolylineOptions()
                .addAll(locationValues)
                .width(10f)
                .color(Color.RED)
                .geodesic(true))
    }

    private fun moveCamera() {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(locationValues.last()))
    }

    private fun setZoom() {
        with(mMap){
            setMinZoomPreference(minZoom)
            setMaxZoomPreference(maxZoom)
        }
    }

    private object Holder {
        val INSTANCE = GoogleMapUtil()
    }

    companion object {
        val TAG = GoogleMapUtil::class.java.simpleName
        val instance: GoogleMapUtil by lazy { Holder.INSTANCE }
    }
}