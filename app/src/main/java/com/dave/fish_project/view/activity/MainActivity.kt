package com.dave.fish_project.view.activity

import android.arch.lifecycle.LifecycleActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.MenuItem
import com.dave.fish_project.R
import com.dave.fish_project.network.RetrofitController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : LifecycleActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        menu ->
        when(menu.itemId){
            R.id.navigation_home -> {
                Log.d(TAG, "navigation home")
                true
            }
            R.id.navigation_reserved ->{
                Log.d(TAG, "navigation reserved")
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation_menu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        RetrofitController().getChartData()
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
    }
}
