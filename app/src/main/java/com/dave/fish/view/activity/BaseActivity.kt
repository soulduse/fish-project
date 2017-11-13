package com.dave.fish.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.dave.fish.R
import io.realm.Realm

/**
 * Created by soul on 2017. 11. 13..
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val realm : Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentId = getContentId()
        if(contentId != 0){
            setContentView(contentId)

            window.attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes.height = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes.horizontalMargin = 0f

            overridePendingTransition(R.anim.anim_popup_slide_right_in, R.anim.anim_none)
        }
        onLoadStart()
        onLoadContent()
    }

    abstract fun getContentId(): Int

    abstract fun onLoadStart()

    abstract fun onLoadContent()

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_none, R.anim.anim_popup_slide_right_out)
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}