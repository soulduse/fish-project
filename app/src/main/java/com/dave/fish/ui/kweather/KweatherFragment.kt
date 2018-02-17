package com.dave.fish.ui.kweather

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.common.Constants
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_tip, container, false)
        mContext = rootView.context

        fragmentKma = instanceFragment(
                WebFragment.newInstance(),
                Constants.KMA_M_URL
        )

        fragmentMarinKma = instanceFragment(
                WebFragment.newInstance(),
                Constants.MARIN_KMA_M_URL
        )

        rootView.tip_viewpager.adapter = ViewPagerAdapter(fragmentManager!!).apply {
            addFragment(fragmentKma, getString(R.string.menu_weather_basic))
            addFragment(fragmentMarinKma, getString(R.string.menu_weather_sea))
        }

        rootView.tip_viewpager.offscreenPageLimit = (rootView.tip_viewpager.adapter as ViewPagerAdapter).count
        rootView.tabs_tip.setupWithViewPager(rootView.tip_viewpager)
        return rootView
    }

    private fun <T : Fragment> instanceFragment(mFragment: T, url: String? = ""): T {
        url?.let {
            mFragment.arguments = Bundle().apply {
                putString(Constants.BUNDLE_FRAGMENT_URL, url)
            }
        }

        return mFragment
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
