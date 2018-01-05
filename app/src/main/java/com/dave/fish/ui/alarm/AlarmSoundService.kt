package com.dave.fish.ui.alarm

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import com.dave.fish.ui.AlarmAlertDialogActivity

/**
 * Created by soul on 2018. 1. 5..
 */
class AlarmSoundService: Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        ringtone()

        val alertIntent = Intent(applicationContext, AlarmAlertDialogActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        applicationContext.startActivity(alertIntent)

        return START_NOT_STICKY
    }
}