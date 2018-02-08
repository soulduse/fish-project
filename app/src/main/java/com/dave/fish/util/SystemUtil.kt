package com.dave.fish.util

import android.app.ActivityManager
import android.content.Context
import com.dave.fish.MyApplication

/**
 * Created by soul on 2018. 2. 8..
 */
object SystemUtil {

    fun <T: Class<*>> isRunningService(t: T): Boolean{
        val manager = MyApplication.context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.getRunningServices(Integer.MAX_VALUE).forEach {
            if(t.name == it.service.className){
                return true
            }
        }
        return false
    }

}