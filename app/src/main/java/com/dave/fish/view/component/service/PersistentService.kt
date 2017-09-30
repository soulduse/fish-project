package com.dave.fish.view.component.service

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import android.util.Log

import com.dave.fish.view.component.broadcast.AlarmBroadCastReceiver


/**
 * Created by soul on 2017. 9. 29..
 */

class PersistentService : Service() {

    private var countDownTimer: CountDownTimer? = null


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        unregisterRestartAlarm()
        super.onCreate()

        initData()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        startForeground(1, Notification())

        /**
         * startForeground 를 사용하면 notification 을 보여주어야 하는데 없애기 위한 코드
         */
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("")
                    .setContentText("")
                    .build();

        } else {
            notification = new Notification(0, "", System.currentTimeMillis());
            notification.setLatestEventInfo(getApplicationContext(), "", "", null);
        }

        nm.notify(startId, notification);
        nm.cancel(startId);

        */
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i(TAG, "PersistentService onDestroy")
        countDownTimer!!.cancel()

        /**
         * 서비스 종료 시 알람 등록을 통해 서비스 재 실행
         */
        registerRestartAlarm()
    }

    /**
     * 데이터 초기화
     */
    private fun initData() {


        countDownTimer()
        countDownTimer!!.start()
    }

    fun countDownTimer() {

        countDownTimer = object : CountDownTimer(MILLISINFUTURE.toLong(), COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {

                Log.i(TAG, "PersistentService onTick")
            }

            override fun onFinish() {

                Log.i(TAG, "PersistentService onFinish")
            }
        }
    }


    /**
     * 알람 매니져에 서비스 등록
     */
    private fun registerRestartAlarm() {
        Log.i(TAG, "000 PersistentService registerRestartAlarm")
        val intent = Intent(this@PersistentService, AlarmBroadCastReceiver::class.java)
        intent.action = "ACTION.RESTART.PersistentService"
        val sender = PendingIntent.getBroadcast(this@PersistentService, 0, intent, 0)

        var firstTime = SystemClock.elapsedRealtime()
        firstTime += (1 * 1000).toLong()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /**
         * 알람 등록
         */
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, (1 * 1000).toLong(), sender)

    }

    /**
     * 알람 매니져에 서비스 해제
     */
    private fun unregisterRestartAlarm() {

        Log.i(TAG, "000 PersistentService unregisterRestartAlarm")

        val intent = Intent(this@PersistentService, AlarmBroadCastReceiver::class.java)
        intent.action = "ACTION.RESTART.PersistentService"
        val sender = PendingIntent.getBroadcast(this@PersistentService, 0, intent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender)

    }

    companion object {
        private val TAG = PersistentService::class.java.simpleName
        private val MILLISINFUTURE = 1000 * 1000
        private val COUNT_DOWN_INTERVAL = 1000
    }

}