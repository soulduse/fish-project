package com.dave.fish.ui.map.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.LocationModel
import com.dave.fish.ui.map.GoogleMapUtil
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_map_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


/**
 * Created by soul on 2017. 10. 26..
 */
class DetailMapActivity : AppCompatActivity() {

    private var idx: Long = 0L
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private val mapFragment by lazy { supportFragmentManager.findFragmentById(R.id.map_detail) as SupportMapFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_detail)

        with(intent) {
            idx = getLongExtra(Constants.EXTRA_LOCATION_MODEL_IDX, 0L)
            lat = getDoubleExtra(Constants.EXTRA_LOCATION_LAT, 0.0)
            lon = getDoubleExtra(Constants.EXTRA_LOCATION_LON, 0.0)
        }

        initToolbar()

        initMap()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if(!isIdxEmpty()){
            toolbar_delete.visibility = View.VISIBLE
        }
    }

    private fun initMap() {
        if (isIdxEmpty()) {
            GoogleMapUtil.instance
                    .initMap(mapFragment, lat, lon)
        } else {
            val latLonList = getLocations()
            latLonList?.let {
                GoogleMapUtil.instance
                        .initMap(mapFragment, lat, lon)
                        .initPolyLine(it)
            }

            toolbar_delete.setOnClickListener {
                alert("정말로 기록된 위치를 삭제하시겠습니까?"){
                    yesButton {
                        RealmProvider.instance.deleteData(LocationModel::class.java, "id", idx)
                        toast("삭제 되었습니다.")
                        finish()
                    }
                    noButton {  }
                }.show()
            }
        }
    }

    private fun getLocations(): List<LatLng>? {
        val locationModel: LocationModel = if (isIdxEmpty()) {
            (RealmProvider.instance.findData(LocationModel::class.java) as List<LocationModel>).last()
        } else {
            RealmProvider.instance.findData(LocationModel::class.java, "id", idx) as LocationModel
        }

        return locationModel.locations?.map { LatLng(it.latitude, it.longtitude) }
    }

    private fun isIdxEmpty(): Boolean = idx == 0L

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
