package com.dave.fish.ui.fweather

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.util.DLog
import kotlinx.android.synthetic.main.fragment_weather.view.*


/**
 * Created by soul on 2018. 2. 17..
 */
class JapanWeatherFragment : Fragment() {

    private lateinit var mContext: Context

    private val adapter by lazy { WeatherAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_weather, container, false)
        mContext = rootView.context
        with(rootView.weather_recyclerview) {
            layoutManager = LinearLayoutManager(mContext)
            adapter = this@JapanWeatherFragment.adapter
        }

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val images = arguments?.getStringArray(Constants.BUNDLE_FRAGMENT_URLS)
        val items: List<WeatherRepo> = images?.map { WeatherRepo(imageUrl = it, title = "") }!!.toList()

        with(adapter){
            setItems(items)
        }
    }

    private fun clearResults() {
        with(adapter) {
            clearItems()
            notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance(): JapanWeatherFragment {
            val fragment = JapanWeatherFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}