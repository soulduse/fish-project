package com.dave.fish.view.fragment

import android.Manifest
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.model.realm.SpinnerSecondModel
import com.dave.fish.view.activity.DetailMapActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_menu_two.*
import java.util.*

/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentMap : Fragment(),
        OnMapReadyCallback{

    private val realm : Realm = Realm.getDefaultInstance()
    private val mRealmController : RealmController = RealmController.instance
    private lateinit var selectedItem : SpinnerSecondModel

    private lateinit var mMap: GoogleMap
    var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedItem = mRealmController.findSelectedSecondModel(realm)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MapsInitializer.initialize(this.activity)
        return inflater.inflate(R.layout.fragment_menu_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapview = view.findViewById<MapView>(R.id.google_map_view)
        mapview.isClickable = false
        mapview.onCreate(savedInstanceState)
        mapview.onResume()
        mapview.getMapAsync(this)

        val geocoder = Geocoder(context)
        val locationList = geocoder.getFromLocation(selectedItem.obsLat, selectedItem.obsLon, 10)
        locationList?.let {
            val address = try{
                locationList[0].getAddressLine(0).filterNot { c->"대한민국".contains(c) }
            }catch (e : IndexOutOfBoundsException){
                resources.getString(R.string.warning_empty_address)
            }

            tv_record_address.text = address
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val mLatLng = LatLng(selectedItem.obsLat, selectedItem.obsLon)
        selectedItem = mRealmController.findSelectedSecondModel(realm)
        mMap = map
        val mapUtil = mMap.uiSettings
        mapUtil.isScrollGesturesEnabled = false
        mapUtil.isZoomGesturesEnabled = false

        enableMyLocation()
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

    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            mMap.isMyLocationEnabled = false
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
