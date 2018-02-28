package com.dave.fish.ui.alarm

import android.app.Activity
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
import com.dave.fish.common.Constants
import com.dave.fish.ui.main.MainActivity
import com.dave.fish.util.DLog
import com.dave.fish.util.PreferenceKeys
import com.dave.fish.util.getDefaultSharedPreferences
import com.dave.fish.util.put
import kotlinx.android.synthetic.main.dialog_alarm.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

/**
 * Created by soul on 2017. 9. 27..
 */
class AlarmReceiver : BroadcastReceiver() {

    private lateinit var mContext: Context

    private lateinit var mMediaPlayer: MediaPlayer

    private val mVibrator by lazy { mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    private val mAudioMgr by lazy { mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    private val mView by lazy { View.inflate(mContext, R.layout.dialog_alarm, null) }

    private var ringtoneUri: Uri ?= null

    private var originalVolume = 0

    private var ringtoneDuration: Int = 0

    override fun onReceive(context: Context, intent: Intent) {
        mContext = context

        ringtoneUri = intent.getParcelableExtra(Constants.EXTRA_RINGTONE_URI)

        ringtoneDuration = intent.getIntExtra(Constants.EXTRA_RINGTONE_DURATION, 60)

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
        mContext.getDefaultSharedPreferences().put(PreferenceKeys.KEY_IS_ALARM_FINISHED, true)
        setVolumeOriginal()
        stopAlarm()
        stopVibrate()
        goneView()
        notifyFinishedAlarm()
    }

    private fun notifyFinishedAlarm() {
        val intent = Intent(Constants.INTENT_FILTER_ALARM_ACTION)
        mContext.sendBroadcast(intent)
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
        // 값이 없을 경우 기본 알람으로 실행한다.
        mMediaPlayer = MediaPlayer.create(mContext, ringtoneUri?:RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))

        launch(UI){
            delay(ringtoneDuration.toLong(), TimeUnit.SECONDS)
            finishAlarm()
        }
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
