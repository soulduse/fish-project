package com.dave.fish.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
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
import com.dave.fish.db.model.LocationModel
import com.dave.fish.db.model.SpinnerSecondModel
import com.dave.fish.ui.map.detail.DetailMapActivity
import com.dave.fish.ui.map.record.RecordActivity
import com.dave.fish.util.SystemUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_menu_two.*
import org.jetbrains.anko.support.v4.toast
import org.joda.time.DateTime
import java.util.*

/**
 * Created by soul on 2017. 8. 27..
 */
class MapFragment : Fragment(),
        OnMapReadyCallback{

    private lateinit var receiver: BroadcastReceiver

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_menu_two, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsInitializer.initialize(this.activity)

        initMapView(savedInstanceState)

        initAddress()

        initRecord()

        btn_show_record.setOnClickListener {
            startActivity(Intent(activity, RecordActivity::class.java))
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
                val selectedSecondSpinner = RealmProvider.instance.getSecondSpinnerItem()
                with(intentService){
                    val currentDate = DateTime.now()
                    putExtra(Constants.EXTRA_LOCATION_MODEL_IDX, currentDate.millis / 1000)
                    putExtra(Constants.EXTRA_LOCATION_LAT, selectedSecondSpinner?.obsLat)
                    putExtra(Constants.EXTRA_LOCATION_LON, selectedSecondSpinner?.obsLon)
                    putExtra(Constants.EXTRA_LOCATION_CREATED_AT, currentDate.toString())
                }
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
        RealmProvider.instance.getSecondSpinnerItem()?.let {
            tv_record_address.text = GeoUtil.getAddress(it.obsLat, it.obsLon)
        }
    }

    private fun initData() {
        // response location values from service
        receiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent) {
                with(mMap){
                    val savedLocationModel = RealmProvider.instance.findData(LocationModel::class.java) as List<LocationModel>
                    if(savedLocationModel.isNotEmpty()){
                        val savedLatLng = savedLocationModel.last().locations?.toList()?.map { LatLng(it.latitude, it.longtitude) }
                        if(savedLatLng != null && savedLatLng.isNotEmpty()){
                            addPolyline(PolylineOptions()
                                    .addAll(savedLatLng)
                                    .width(10f)
                                    .color(Color.RED)
                                    .geodesic(true))

                            if(savedLatLng.isNotEmpty()){
                                animateCamera(CameraUpdateFactory.newLatLng(savedLatLng.last()))
                            }
                            setMinZoomPreference(16.0f)
                            setMaxZoomPreference(21.0f)
                        }else{
                            toast("GPS 수신상태가 좋지 않습니다. GPS를 재탐색합니다.")
                        }
                    }
                }
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
                    detailMapIntent.putExtra(Constants.EXTRA_LOCATION_LAT, selected.obsLat)
                    detailMapIntent.putExtra(Constants.EXTRA_LOCATION_LON, selected.obsLon)
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

