package com.dave.fish.ui.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.dave.fish.R
import kotlinx.android.synthetic.main.dialog_alarm_bottom_sheet.view.*


/**
 * Created by soul on 2018. 1. 5..
 */
class AlarmSoundService: Service() {

    private lateinit var mediaPlayer: MediaPlayer

    private val vibe by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    private val mView by lazy { View.inflate(this, R.layout.dialog_alarm_bottom_sheet, null) }

    private val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        initAlarmView()

        initViewListener()

        initAlarm()

        startAlarm()

        startVibrate()

        return START_NOT_STICKY
    }

    private fun initViewListener(){
        mView.setBackgroundColor(Color.WHITE)
        mView.tv_off_alarm.setOnClickListener {
            finishAlarm()
        }
    }

    private fun initAlarmView() {
        val mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val mLayoutParams = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.RGBA_8888)/* | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON */

        mWindowManager.addView(mView, mLayoutParams)
    }

    private fun finishAlarm(){
        stopAlarm()
        stopVibrate()
        goneView()
    }

    private fun goneView(){
        mView.visibility = View.GONE
    }

    private fun startVibrate(){
        vibe.vibrate(longArrayOf(500, 5000),0)
    }

    private fun stopVibrate(){
        vibe.cancel()
    }

    private fun initAlarm(){
        mediaPlayer = MediaPlayer.create(applicationContext, alarmUri)
    }

    private fun startAlarm(){
        mediaPlayer.start()
    }

    private fun stopAlarm(){
        mediaPlayer.stop()
    }
}