package com.dave.fish.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by soul on 2017. 9. 27..
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val intent = Intent(context, AlarmSoundService::class.java)
        context.startService(intent)

//        Log.i(TAG, "000 RestartService RestartService called : " + intent.action)
//
//        /**
//         * 서비스 죽일때 알람으로 다시 서비스 등록
//         */
//        if (intent.action == "ACTION.RESTART.PersistentService") {
//
//            Log.i(TAG, "000 RestartService ACTION.RESTART.PersistentService ")
//
//            val i = Intent(context, PersistentService::class.java)
//            context.startService(i)
//        }
//
//        /**
//         * 폰 재시작 할때 서비스 등록
//         */
//        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
//
//            Log.i(TAG, "ACTION_BOOT_COMPLETED")
//            val i = Intent(context, PersistentService::class.java)
//            context.startService(i)
//
//        }
    }
}