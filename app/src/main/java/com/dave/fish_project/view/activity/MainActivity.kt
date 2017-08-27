package com.dave.fish_project.view.activity

import android.arch.lifecycle.LifecycleActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MenuItem
import com.dave.fish_project.R
import com.dave.fish_project.network.RetrofitController
import com.dave.fish_project.view.adapter.ViewPagerAdapter
import com.dave.fish_project.view.fragment.FragmentMenuOne
import com.dave.fish_project.view.fragment.FragmentMenuTwo
import kotlinx.android.synthetic.main.activity_main.*
import android.databinding.adapters.CompoundButtonBindingAdapter.setChecked



class MainActivity : LifecycleActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        menu ->
        when(menu.itemId){
            R.id.navigation_home -> {
                Log.d(TAG, "navigation home")
                main_viewpager.currentItem = 0
                true
            }
            R.id.navigation_reserved ->{
                Log.d(TAG, "navigation reserved")
                main_viewpager.currentItem = 1
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation_menu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setupViewPager()
        RetrofitController().getChartData()
    }



    private fun setupViewPager(){
        main_viewpager.adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(FragmentMenuOne())
            addFragment(FragmentMenuTwo())
        }

        main_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                navigation_menu.menu.getItem(position).isChecked = true
            }
        })
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
    }
}
