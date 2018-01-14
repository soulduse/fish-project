package com.dave.fish.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.dave.fish.R
import kotlinx.android.synthetic.main.dialog_alarm_bottom_sheet.view.*


/**
 * Created by soul on 2017. 9. 27..
 */
class AlarmReceiver : BroadcastReceiver() {

    private lateinit var mContext: Context

    private lateinit var mMediaPlayer: MediaPlayer

    private val mVibrator by lazy { mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    private val mAudioMgr by lazy { mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    private val mView by lazy { View.inflate(mContext, R.layout.dialog_alarm_bottom_sheet, null) }

    private val mAlarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    private var originalVolume = 0

    override fun onReceive(context: Context, intent: Intent) {
        mContext = context

        originalVolume = mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC)

        initAlarmView()

        initViewListener()

        initAlarm()

        startAlarm()

        startVibrate()
    }

    private fun initViewListener(){
        mView.setBackgroundColor(Color.WHITE)
        mView.tv_off_alarm.setOnClickListener {
            finishAlarm()
        }
    }

    private fun initAlarmView() {
        val mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

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
        setVolumeOriginal()
        stopAlarm()
        stopVibrate()
        goneView()
    }

    private fun goneView(){
        mView.visibility = View.GONE
    }

    private fun startVibrate(){
        mVibrator.vibrate(longArrayOf(500, 3500),0)
    }

    private fun stopVibrate(){
        mVibrator.cancel()
    }

    private fun initAlarm(){
        setVolumeMax()
        mMediaPlayer = MediaPlayer.create(mContext, mAlarmUri)
    }

    private fun setVolumeMax() {
        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
    }

    private fun setVolumeOriginal(){
        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
    }

    private fun startAlarm(){
        mMediaPlayer.start()
    }

    private fun stopAlarm(){
        mMediaPlayer.stop()
    }
}
