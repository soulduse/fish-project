package com.dave.fish_project.component.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dave.fish_project.component.service.PersistentService
import org.joda.time.DateTime

/**
 * Created by soul on 2017. 9. 27..
 */
class AlarmBraodCastReciever : BroadcastReceiver() {

    private lateinit var alarmMgr: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    override fun onReceive(context: Context, intent: Intent) {

        // val intent = Intent(context, AlarmReceiver::java.class)
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        var dateTime = DateTime()

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, dateTime.millis,
                1000 * 60 * 20, alarmIntent)


        Log.i(TAG, "000 RestartService RestartService called : " + intent.action)

        /**
         * 서비스 죽일때 알람으로 다시 서비스 등록
         */
        if (intent.action == "ACTION.RESTART.PersistentService") {

            Log.i(TAG, "000 RestartService ACTION.RESTART.PersistentService ")

            val i = Intent(context, PersistentService::class.java)
            context.startService(i)
        }

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            Log.i(TAG, "ACTION_BOOT_COMPLETED")
            val i = Intent(context, PersistentService::class.java)
            context.startService(i)

        }
    }

    companion object {
        private val TAG = AlarmBraodCastReciever::class.java.simpleName
    }
}