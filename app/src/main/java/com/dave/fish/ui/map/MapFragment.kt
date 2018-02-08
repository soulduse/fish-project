package com.dave.fish.ui.map

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
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dave.fish.MyApplication
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.LatLonModel
import com.dave.fish.db.model.LocationModel
import com.dave.fish.db.model.SpinnerSecondModel
import com.dave.fish.util.DLog
import com.dave.fish.util.SystemUtil
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_menu_two.*
import org.joda.time.DateTime
import java.util.*

/**
 * Created by soul on 2017. 8. 27..
 */
class MapFragment : Fragment(),
        OnMapReadyCallback{

    private lateinit var receiver: BroadcastReceiver

    private lateinit var mMap: GoogleMap
    private var realmLocationIndex = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_menu_two, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsInitializer.initialize(this.activity)

        initMapView(savedInstanceState)

        initAddress()

        initRecord()

        btn_show_record.setOnClickListener {

        }

        initData()
    }

    private fun initMapView(savedInstanceState: Bundle?) {
        google_map_view.apply {
            isClickable = false
            onCreate(savedInstanceState)
            onResume()
            getMapAsync(this@MapFragment)
        }
    }

    private fun initRecord() {
        initRecordText()

        initRecordClick()
    }

    private fun initRecordClick() {
        btn_start_record.setOnClickListener {
            val isRecorded = btn_start_record.isSelected
            val intentService = Intent(activity, LocationService::class.java).apply {
                putExtra(Constants.EXTRA_NOTIFIER, Constants.EXTRA_NOTIFICATION_ID)
            }

            btn_start_record.isSelected = isRecorded.not()

            if (isRecorded) {
                activity?.stopService(intentService)
                btn_start_record.text = resources.getString(R.string.record_start)
            } else {
                realmLocationIndex = DateTime.now().millis / 1000
                activity?.startService(intentService)
                btn_start_record.text = resources.getString(R.string.record_stop)
            }
        }
    }

    private fun initRecordText() {
        btn_start_record.isSelected = SystemUtil.isRunningService(LocationService::class.java)
        if (btn_start_record.isSelected) {
            btn_start_record.text = resources.getString(R.string.record_stop)
        } else {
            btn_start_record.text = resources.getString(R.string.record_start)
        }
    }

    private fun initAddress() {
        val geoCoder = Geocoder(MyApplication.context)
        RealmProvider.instance.getSecondSpinnerItem()?.let {
            val locationList = geoCoder.getFromLocation(it.obsLat, it.obsLon, 10)
            locationList?.let {
                val address = try {
                    locationList[0].getAddressLine(0).filterNot { c -> resources.getString(R.string.korea).contains(c) }
                } catch (e: IndexOutOfBoundsException) {
                    resources.getString(R.string.warning_empty_address)
                }

                tv_record_address.text = address
            }
        }
    }

    private fun initData() {
        // response location values from service
        receiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent) {
                val locationValues = intent.getParcelableArrayListExtra<Location>(Constants.RESPONSE_LOCATION_VALUES).map {
                    LatLng(it.latitude, it.longitude)
                }

                setRealmLocation(locationValues)

                DLog.w("[locationValues]\nsize : ${locationValues.size}\npoints :$locationValues")
                val polyLine = mMap.addPolyline(PolylineOptions()
                        .addAll(locationValues)
                        .width(10f)
                        .color(Color.RED)
                        .geodesic(true))

                mMap.animateCamera(CameraUpdateFactory.newLatLng(locationValues.last()))
                mMap.setMinZoomPreference(16.0f)
                mMap.setMaxZoomPreference(21.0f)
                polyLine.tag = "내경로"
            }

            private fun setRealmLocation(locationValues: List<LatLng>) {
                val selectedSecondSpinner = RealmProvider.instance.getSecondSpinnerItem()

                val latLonList = RealmList<LatLonModel>()
                locationValues.forEach { item ->
                    val latlonModel = LatLonModel().apply {
                        latitude = item.latitude
                        longtitude = item.longitude
                    }
                    latLonList.add(latlonModel)
                }

                val locationModel = LocationModel().apply {
                    id = realmLocationIndex
                    locations = latLonList
                    selectedSecondSpinner?.let {
                        fixedLat = it.obsLat
                        fixedLon = it.obsLon
                    }
                }

                DLog.w("""location values -->
                    id : ${locationModel.id}
                    createdAt : ${locationModel.createdAt}
                    updatedAt : ${locationModel.updatedAt}
                    fixedLat : ${locationModel.fixedLat}
                    fixedLon : ${locationModel.fixedLon}
                    locations : ${locationModel.locations.toString()}
                    locations.size : ${locationModel.locations.size}
                    """)

                RealmProvider.instance.writeData(locationModel)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val getSecondSpinnerItem = RealmProvider.instance.getSecondSpinnerItem()
        val secondItem = RealmProvider.instance.copyData(getSecondSpinnerItem!!) as SpinnerSecondModel

        secondItem.let { selected->
            val mLatLng = LatLng(selected.obsLat, selected.obsLon)
            mMap = map

            with(mMap){
                val mapUtil = mMap.uiSettings
                with(mapUtil){
                    isScrollGesturesEnabled = false
                    isZoomGesturesEnabled = false
                }

                setMinZoomPreference(12.0f)
                setMaxZoomPreference(15.0f)
                addMarker(MarkerOptions().position(mLatLng).title(selected.obsPostName))
                moveCamera(CameraUpdateFactory.newLatLng(mLatLng))

                setOnMapClickListener {
                    val detailMapIntent = Intent(activity, DetailMapActivity::class.java)
                    detailMapIntent.putExtra("lat", selected.obsLat)
                    detailMapIntent.putExtra("lon", selected.obsLon)
                    startActivity(detailMapIntent)
                }
            }

            enableMyLocation()
        }
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        @SuppressLint("MissingPermission")
        override fun onPermissionGranted() {
            mMap.isMyLocationEnabled = true
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            Toast.makeText(MyApplication.context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableMyLocation() {
        TedPermission.with(MyApplication.context)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(MyApplication.context!!).registerReceiver((receiver),
                IntentFilter(Constants.LOCATION_SERVICE_RESULT)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(MyApplication.context!!).unregisterReceiver(receiver)
        super.onStop()
    }

    companion object {
        fun newInstance() : MapFragment {
            val fragmemt = MapFragment()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}

