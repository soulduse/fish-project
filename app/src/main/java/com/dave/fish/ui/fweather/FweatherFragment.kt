package com.dave.fish.ui.fweather

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
import org.joda.time.DateTime

/**
 * Created by soul on 2018. 2. 17..
 */
class FweatherFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var fragmentHeight: JapanWeatherFragment
    private lateinit var fragmentRain: JapanWeatherFragment
    private lateinit var fragmentMeasure: JapanWeatherFragment
    private lateinit var fragmentFlow: WebFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_tip, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mContext = view.context

        initFragments()

        view.tip_viewpager.adapter = ViewPagerAdapter(childFragmentManager).apply {
            addFragment(fragmentHeight, getString(R.string.weather_height_japan))
            addFragment(fragmentRain, getString(R.string.weather_cloud_japan))
            addFragment(fragmentMeasure, getString(R.string.weather_measure_japan))
            addFragment(fragmentFlow, getString(R.string.weather_flow_usa))
        }

        view.tip_viewpager.offscreenPageLimit = (view.tip_viewpager.adapter as ViewPagerAdapter).count
        view.tabs_tip.setupWithViewPager(view.tip_viewpager)

        view.tip_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                FireEventProvider.trackEvent(FireEventProvider.F_WEATHER_ARRAY[position])
            }
        })
    }

    private fun initFragments() {
        fragmentHeight = FragmentProvider.instanceFragment(
                JapanWeatherFragment.newInstance(),
                getHeightUrls()
        )
        fragmentRain = FragmentProvider.instanceFragment(
                JapanWeatherFragment.newInstance(),
                getRainUrls()
        )
        fragmentMeasure = FragmentProvider.instanceFragment(
                JapanWeatherFragment.newInstance(),
                getMeasureUrls()
        )

        fragmentFlow = FragmentProvider.instanceFragment(
                WebFragment.newInstance(),
                Constants.FLOW_M_URL
        )
    }

    private fun getHeightUrls(): Array<String> {
        val currentDay = convertString(DateTime().dayOfMonth-1)
        val urls = (0..24 step 2).map { "http://www.imocwx.com/cwm/$currentDay/00/cwmsjp_${convertString(it)}.gif${getSideCode(DateType.DAY)}" }
        return urls.toTypedArray()
    }

    private fun getMeasureUrls(): Array<String> {
        val urls = (0..26).map { "http://www.imocwx.com/guid/gd1${convertString(it)}sjp.gif${getSideCode(DateType.DAY)}" }
        return urls.toTypedArray()
    }

    private fun getRainUrls(): Array<String> {
        val urls = (0..26).map { "http://www.imocwx.com/guid/gd0${convertString(it)}sjp.gif${getSideCode(DateType.DAY)}" }
        return urls.toTypedArray()
    }

    private fun convertString(num: Int): String =
            if (num < 10) { "0$num" } else { num.toString() }

    private fun getSideCode(type: DateType): String{
        val currentDate = DateTime()
        val currentPatternDate = when(type){
            DateType.DAY -> currentDate.toString("yyyyMMdd")
            DateType.HOUR -> currentDate.toString("yyyyMMddHH")
            DateType.MINUTE -> currentDate.toString("yyyyMMddHHmm")
        }

        return "?sidecode=$currentPatternDate"
    }

    enum class DateType{
        DAY, HOUR, MINUTE
    }

    companion object {
        fun newInstance(): FweatherFragment {
            val fragment = FweatherFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
