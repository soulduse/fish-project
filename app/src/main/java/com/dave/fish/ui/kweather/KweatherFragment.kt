package com.dave.fish.ui.kweather

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.common.FragmentProvider
import com.dave.fish.common.firebase.FireEventProvider
import com.dave.fish.ui.main.ViewPagerAdapter
import com.dave.fish.ui.web.WebFragment
import kotlinx.android.synthetic.main.fragment_tip.view.*

/**
 * Created by soul on 2018. 2. 17..
 */
class KweatherFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var fragmentKma: WebFragment
    private lateinit var fragmentMarinKma: WebFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_tip, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mContext = view.context

        fragmentKma = FragmentProvider.instanceFragment(
                WebFragment.newInstance(),
                Constants.KMA_M_URL
        )

        fragmentMarinKma = FragmentProvider.instanceFragment(
                WebFragment.newInstance(),
                Constants.MARIN_KMA_M_URL
        )

        view.tip_viewpager.adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(fragmentKma, getString(R.string.menu_weather_basic))
            addFragment(fragmentMarinKma, getString(R.string.menu_weather_sea))
        }

        view.tip_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                FireEventProvider.trackEvent(FireEventProvider.K_WEATHER_ARRAY[position])
            }
        })

        view.tabs_tip.setupWithViewPager(view.tip_viewpager)
    }

    companion object {
        fun newInstance(): KweatherFragment {
            val fragment = KweatherFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
