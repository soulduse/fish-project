package com.dave.fish.view.fragment

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.gun0912.tedpermission.TedPermission
import io.realm.Realm
import android.widget.Toast
import com.dave.fish.model.realm.SpinnerSecondModel
import com.dave.fish.view.activity.DetailMapActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import kotlinx.android.synthetic.main.fragment_menu_two.*
import java.util.ArrayList


/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentMap : Fragment(), OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

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
        mapview.isClickable = true
        mapview.onCreate(savedInstanceState)
        mapview.onResume()
        mapview.getMapAsync(this)

        mapview.setOnClickListener {
            Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
            val detailMapIntent = Intent(activity, DetailMapActivity::class.java)
            detailMapIntent.putExtra("lat", selectedItem.obsLat)
            detailMapIntent.putExtra("lon", selectedItem.obsLon)
            startActivity(detailMapIntent)
        }
//        onClickMap()
    }

    private fun onClickMap(){
        google_map_view.setOnClickListener {
            Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
            val detailMapIntent = Intent(activity, DetailMapActivity::class.java)
            detailMapIntent.putExtra("lat", selectedItem.obsLat)
            detailMapIntent.putExtra("lon", selectedItem.obsLon)
            startActivity(detailMapIntent)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val mLatLng = LatLng(selectedItem.obsLat, selectedItem.obsLon)
        selectedItem = mRealmController.findSelectedSecondModel(realm)
        mMap = map
        val mapUtil = mMap.uiSettings
        mapUtil.isScrollGesturesEnabled = false
        mapUtil.isZoomGesturesEnabled = false

        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        enableMyLocation()
        mMap.setMinZoomPreference(12.0f)
        mMap.setMaxZoomPreference(15.0f)
        mMap.addMarker(MarkerOptions().position(mLatLng).title(selectedItem.obsPostName))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng))
    }

    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
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
//        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {
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
