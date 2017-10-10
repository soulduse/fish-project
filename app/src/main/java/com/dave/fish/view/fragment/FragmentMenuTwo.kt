package com.dave.fish.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.nhn.android.maps.NMapContext
import com.nhn.android.maps.NMapView
import kotlinx.android.synthetic.main.fragment_menu_two.*


/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentMenuTwo : Fragment() {

    private lateinit var mMapContext: NMapContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMapContext = NMapContext(super.getActivity()!!)
        mMapContext.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_menu_two, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.setClientId(resources.getString(R.string.naver_map_key))// 클라이언트 아이디 설정
        mMapContext.setupMapView(mapView)
    }

    override fun onStart() {
        super.onStart()
        mMapContext.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapContext.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapContext.onPause()
    }

    override fun onStop() {
        mMapContext.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        mMapContext.onDestroy()
        super.onDestroy()
    }

    companion object {
        private val TAG = FragmentMenuTwo::class.java.simpleName
    }
}
