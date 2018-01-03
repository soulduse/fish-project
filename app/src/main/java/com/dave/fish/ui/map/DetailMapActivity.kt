package com.dave.fish.ui.map

import com.dave.fish.R
import com.dave.fish.ui.BaseActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment


/**
 * Created by soul on 2017. 10. 26..
 */
class DetailMapActivity : BaseActivity()
{
    private lateinit var mMap: GoogleMap

    override fun getContentId(): Int = R.layout.activity_map_detail

    override fun initViews() {
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_detail) as SupportMapFragment
        GoogleMapUtil.instance.initMap(mapFragment, lat, lon)

    }

    override fun initData() {

    }

//    override fun onMapReady(map: GoogleMap) {
//        mMap = map
//
//        val lat = intent.getDoubleExtra("lat", 0.0)
//        val lon = intent.getDoubleExtra("lon", 0.0)
//
//        val mLatLng = LatLng(lat, lon)
//
//        mMap.setOnMyLocationButtonClickListener(this)
//        mMap.setOnMyLocationClickListener(this)
//        enableMyLocation()
//        mMap.setMinZoomPreference(12.0f)
//        mMap.setMaxZoomPreference(15.0f)
//        mMap.addMarker(MarkerOptions().position(mLatLng).title(selectedItem.obsPostName))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng))
//
//    }

//    private fun enableMyLocation() {
//        TedPermission.with(applicationContext)
//                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
//                .check()
////        }
//    }
}