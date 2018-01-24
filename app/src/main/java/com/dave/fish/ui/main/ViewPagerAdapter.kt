package com.dave.fish.ui.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

/**
 * Created by soul on 2017. 8. 27..
 */
class ViewPagerAdapter(manager : FragmentManager) : FragmentStatePagerAdapter(manager){

    private var mFragmentList = mutableListOf<Fragment>()
    private var mFragmentTitleList = mutableListOf<String>()

    override fun getItem(position: Int): Fragment = mFragmentList[position]

    override fun getCount(): Int = mFragmentList.size

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    override fun getPageTitle(position: Int): CharSequence? = mFragmentTitleList[position]
}
