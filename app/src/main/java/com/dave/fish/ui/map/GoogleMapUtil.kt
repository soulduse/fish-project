package com.dave.fish.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import com.dave.fish.common.DistanceUtil
import com.dave.fish.util.DLog
import com.dave.fish.util.permission.PermissionCheck
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


/**
 * Created by soul on 2017. 10. 27..
 */
class GoogleMapUtil : GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback{

    private lateinit var mMap : GoogleMap
    private lateinit var mContext: Context
    private var lat = 0.0
    private var lon = 0.0
    private var locationValues: MutableList<LatLng> = mutableListOf()
    private var isMyLocation = false

    fun initMap(mapView : MapView, lat: Double, lon:Double): GoogleMapUtil {
        mContext = mapView.context!!
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon

        return this@GoogleMapUtil
    }

    fun initMap(mapView : SupportMapFragment, lat: Double, lon:Double): GoogleMapUtil {
        mContext = mapView.context!!
        mapView.getMapAsync(this)
        this.lat = lat
        this.lon = lon

        return this@GoogleMapUtil
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        DLog.w("enableMyLocation in!")

        PermissionCheck
                .withContext(mContext)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .success {
                    mMap.isMyLocationEnabled = true
                }
                .check()
    }

    /**
     * TODO MyLocation 버튼 3가지 옵션으로 확장하여 카메라 이동시키기 { 내위치, 마커위치, 둘사이의 중간 위치 }
     * 참고하면 좋은 Url :
     * https://stackoverflow.com/questions/16764002/how-to-center-the-camera-so-that-marker-is-at-the-bottom-of-screen-google-map
     * https://developers.google.com/maps/documentation/android-api/views?hl=fr-FR#moving_the_camera
     * */

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
            moveCamera(CameraUpdateFactory.zoomTo(14.0f))

            // 카메라 스위칭 : 현재위치, 타겟위치
            setOnMyLocationButtonClickListener {
                isMyLocation = isMyLocation.not()
                val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(lat, lon))
                        .zoom(14f)
                        .build()

                animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                isMyLocation
            }
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