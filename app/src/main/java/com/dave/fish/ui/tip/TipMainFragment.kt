package com.dave.fish.ui.tip

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish.R
import com.dave.fish.ui.main.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_tip.view.*


/**
 * Created by soul on 2018. 2. 16..
 */
class TipMainFragment : Fragment() {
    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_tip, container, false)
        rootView.tip_viewpager.adapter = ViewPagerAdapter(fragmentManager!!).apply {
            addFragment(TipCatchTableFragment.newInstance(), getString(R.string.title_tip_catch_table))
            addFragment(TipTideFragment.newInstance(), getString(R.string.title_tip_tide))
            addFragment(TipInfoFragment.newInstance(), getString(R.string.title_tip_info))
        }

        rootView.tip_viewpager.offscreenPageLimit = (rootView.tip_viewpager.adapter as ViewPagerAdapter).count
        rootView.tabs_tip.setupWithViewPager(rootView.tip_viewpager)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance(): TipMainFragment {
            val fragment = TipMainFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}




