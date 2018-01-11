package com.dave.fish.ui.alarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.dave.fish.R
import com.dave.fish.ui.main.MainActivity

/**
 * Created by soul on 2017. 9. 27..
 */
class AlarmReceiver : BroadcastReceiver() {

    fun showNotification(context: Context){
        val remoteView = RemoteViews(context.packageName, R.layout.dialog_alarm_bottom_sheet)
        remoteView.setOnClickPendingIntent(R.id.tv_off_alarm)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val contentIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val noti = Notification.Builder(context)
                .setContentTitle("간조시각 알림")
                .setContentText("안전한 해루질을 위해 들어갑시다!")
                .setSmallIcon(R.drawable.ic_toys_white_24dp)
                .setFullScreenIntent(contentIntent, true)
                .addAction(R.drawable.ic_wb_sunny_grey_500_24dp)
                .setPriority(Notification.PRIORITY_HIGH)
                .build()

        val notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationMgr.notify(1, noti)
    }

    override fun onReceive(context: Context, intent: Intent) {

        showNotification(context)

//        val intent = Intent(context, AlarmSoundService::class.java)
//        context.startService(intent)

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