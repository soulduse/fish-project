package com.dave.fish.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.dave.fish.R
import io.realm.Realm

/**
 * Created by soul on 2017. 11. 13..
 */
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRealm()

        initAnimation()

        val contentId = getContentId()
        if(contentId != 0){
            setContentView(contentId)
        }
        AWSMobileClient.getInstance().initialize(this).execute()

        initViews()

        initData()
    }

    private fun initRealm(){
        realm = Realm.getDefaultInstance()
    }

    private fun initAnimation(){
        overridePendingTransition(R.anim.anim_popup_slide_right_in, R.anim.anim_none)
    }

    abstract fun getContentId(): Int

    abstract fun initViews()

    abstract fun initData()

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_none, R.anim.anim_popup_slide_right_out)
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}