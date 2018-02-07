package com.dave.fish.ui.map

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dave.fish.R
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.LocationModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


/**
 * Created by soul on 2017. 10. 26..
 */
class DetailMapActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_detail)

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_detail) as SupportMapFragment
        val locationList = (RealmProvider.instance.findData(LocationModel::class.java) as List<LocationModel>).last()
        val latLonList = locationList.locations.map { LatLng(it.latitude, it.longtitude) }

        GoogleMapUtil.instance
                .initMap(mapFragment, lat, lon)
                .initPolyLine(latLonList)
                .initZoomLevel(8.0f, 21.0f)
    }
}
