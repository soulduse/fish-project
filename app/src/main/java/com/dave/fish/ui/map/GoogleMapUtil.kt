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
    private var locationValues: MutableList<LatLng> = mutableListOf()

    fun initMap(mapView : MapView, lat: Double, lon:Double): GoogleMapUtil {
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon

        return this@GoogleMapUtil
    }

    fun initMap(mapView : SupportMapFragment, lat: Double, lon:Double): GoogleMapUtil {
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon

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
            setMinZoomPreference(7.0f)
            setMaxZoomPreference(17.0f)
            addMarker(MarkerOptions().position(mLatLng))
            moveCamera(CameraUpdateFactory.newLatLng(mLatLng))
            moveCamera(CameraUpdateFactory.zoomTo(16.0f))
        }

        drawPolyLine()
        enableMyLocation()
    }

    fun initPolyLine(locationValues: List<LatLng>){
        this.locationValues = locationValues.toMutableList()
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
            setMinZoomPreference(8.0f)
            setMaxZoomPreference(21.0f)
        }
    }

    /**
     * @method 5초 이내 갱신된 거리가 50미터 미만이라면 값을 추가
     * @reason 단말 GPS 수신 이상으로 비정상 값이 들어오는 것을 방지
     */
    private fun isAbleAddPolyLine(location1 : Location, location2 : Location) : Boolean{

        val distance = DistanceUtil.distance(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude,
                "M")

        val meterOfDistance = Math.round(distance)

        if(meterOfDistance <= 50){
            return true
        }

        return false
    }


    private object Holder {
        val INSTANCE = GoogleMapUtil()
    }

    companion object {
        val TAG = GoogleMapUtil::class.java.simpleName
        val instance: GoogleMapUtil by lazy { Holder.INSTANCE }
    }
}