package com.dave.fish.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.dave.fish.R
import com.dave.fish.util.Global
import kotlinx.android.synthetic.main.activity_tide_detail.*

/**
 * Created by soul on 2017. 10. 3..
 */
class TideDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tide_detail)
        init()
    }

    private fun init(){
        initLayout()
    }

    private fun initLayout(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        supportActionBar?.setIcon(resources.getDrawable(R.drawable.ic_arrow_back_white_24dp))

//        toolbar.setNavigationOnClickListener {
//            onSupportNavigateUp()
//        }
        val selectedDate = intent.getStringExtra(Global.INTENT_DATE)
        Log.d(TAG, "selectedDate : $selectedDate")
        title_temp.text = selectedDate
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val TAG = TideDetailActivity::class.java.simpleName
    }
}