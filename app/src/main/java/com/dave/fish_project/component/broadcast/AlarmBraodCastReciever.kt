package com.dave.fish_project.component.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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

    }
}