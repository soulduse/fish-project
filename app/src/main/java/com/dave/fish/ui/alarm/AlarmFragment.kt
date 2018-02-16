package com.dave.fish.ui.alarm

import android.app.Activity.RESULT_OK
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.dave.fish.R
import com.dave.fish.common.Constants
import com.dave.fish.util.PreferenceKeys
import com.dave.fish.util.getDefaultSharedPreferences
import com.dave.fish.util.put
import kotlinx.android.synthetic.main.fragment_alaram.*
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import org.joda.time.DateTime


/**
 * Created by soul on 2017. 12. 16..
 */
class AlarmFragment : Fragment() {

    private lateinit var mContext: Context

    private lateinit var alarmIntent: Intent

    private var pendingIntent: PendingIntent?= null

    private val sharedPreference by lazy { mContext.getDefaultSharedPreferences() }

    private val alarmMgr: AlarmManager by lazy { mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    private var idxDuration = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_alaram, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = view.context

        initNumberPicker()

        initAlarm()

        initAlarmData()
    }

    private fun initNumberPicker() {
        num_picker_hour.run {
            minValue = 0
            maxValue = HOUR_OF_DAY
            value = BASIC_HOUR
        }

        num_picker_min.run {
            minValue = 0
            maxValue = (MINUTES_OF_HOUR / STEP_NUMBER) - 1
            displayedValues = MINUTES
        }

        num_picker_hour.setOnValueChangedListener(changedListener)
        num_picker_min.setOnValueChangedListener(changedListener)
    }

    private val changedListener = NumberPicker.OnValueChangeListener { _, _, _ ->
        initAlarmData()
    }


    private fun initAlarm() {
        setStartAlarm()

        setSoundDuration()

        setAlarmMusic()
    }

    private fun setAlarmMusic() {
        sharedPreference.getString(PreferenceKeys.KEY_RINGTONE_NAME, null)?.let {
            tv_alarm_music.text = it
        }

        tv_alarm_music.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, mContext.getString(R.string.alarm_list_title))  // 제목을 넣는다.
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)  // 무음을 선택 리스트에서 제외
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true) // 기본 벨소리는 선택 리스트에 넣는다.
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            }
            startActivityForResult(intent, REQUEST_CODE_RINGTONE)
        }
    }

    private fun setStartAlarm() {
        start_alarm.setOnClickListener {
            sharedPreference.put(PreferenceKeys.KEY_IS_ALARM_FINISHED, start_alarm.isSelected)
            updateAlarmTriggerUI()
            doAlarm()
        }
    }

    private fun updateAlarmTriggerUI(){
        val isAlarmFinished = sharedPreference.getBoolean(PreferenceKeys.KEY_IS_ALARM_FINISHED, true)
        when(isAlarmFinished){
            true -> {
                start_alarm.text = mContext.getString(R.string.start_alarm)
                start_alarm.isSelected = false
            }
            false -> {
                start_alarm.text = mContext.getString(R.string.stop_alarm)
                start_alarm.isSelected = true
            }
        }
    }

    private fun doAlarm() {
        when (start_alarm.isSelected) {
            true -> startAlarm()
            false -> stopAlarm()
        }
    }

    private fun stopAlarm() {
        toast(resources.getString(R.string.stopped_alarm))
        pendingIntent?.cancel()
        alarmMgr.cancel(pendingIntent)
    }

    private fun startAlarm() {
        longToast(resources.getString(
                R.string.started_alarm,
                num_picker_hour.value,
                num_picker_min.value * STEP_NUMBER
        ))

        initAlarmIntentData()

        pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        setExactAlarm(
                AlarmManager.RTC_WAKEUP,
                getAlarmTime(),
                pendingIntent!!
        )
    }

    private fun initAlarmIntentData() {
        val ringtoneUri: Uri = Uri.parse(sharedPreference.getString(PreferenceKeys.KEY_RINGTONE_URI, ""))
        val ringtoneDuration = sharedPreference.getInt(PreferenceKeys.KEY_SOUND_DURATION, 0)
        alarmIntent = Intent("com.dave.fish.START_ALARM").apply {

            if (ringtoneUri.toString().isNotEmpty()) {
                putExtra(Constants.EXTRA_RINGTONE_URI, ringtoneUri)
            }

            if (ringtoneDuration != 0) {
                putExtra(Constants.EXTRA_RINGTONE_DURATION, ringtoneDuration)
            }
        }
    }

    private fun setSoundDuration() {
        val soundDuration: Int = sharedPreference.getInt(PreferenceKeys.KEY_SOUND_DURATION, 0)
        if (soundDuration != 0) {
            tv_sound_duration.text = getSecondOrMinute(soundDuration)
        }

        tv_sound_duration.setOnClickListener {
            if (idxDuration == SOUND_DURATIONS.size) {
                idxDuration = 0
            }
            sharedPreference.put(PreferenceKeys.KEY_SOUND_DURATION, SOUND_DURATIONS[idxDuration])
            tv_sound_duration.text = getSecondOrMinute(SOUND_DURATIONS[idxDuration])
            idxDuration++
        }
    }

    private fun getSecondOrMinute(sec: Int): String{
        if(sec % 60 == 0){
            return (sec / 60).toString()+" 분"
        }

        return sec.toString()+" 초"
    }

    private fun setExactAlarm(type: Int, triggerAtMillis: Long, operation: PendingIntent ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmMgr.setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmMgr.setExact(type, triggerAtMillis, operation)
        else
            alarmMgr.set(type, triggerAtMillis, operation)
    }

    private fun getAlarmTime(): Long{
        return DateTime()
                .plusHours(num_picker_hour.value)
                .plusMinutes(num_picker_min.value * STEP_NUMBER)
                .millis
    }

    private fun initAlarmData(){
        result_time.text = resources.getString(
                R.string.number_picker_selected_values,
                num_picker_hour.value,
                num_picker_min.value * STEP_NUMBER
        )

        result_time_detail.text = DateTime(getAlarmTime()).toString("MM/dd (E) kk:mm")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_RINGTONE && resultCode == RESULT_OK){
            val ringtoneUri: Uri? = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

            val ringToneName = RingtoneManager.getRingtone(mContext, ringtoneUri).getTitle(mContext)
            ringtoneUri?.let {
                sharedPreference.put(PreferenceKeys.KEY_RINGTONE_URI, ringtoneUri.toString())
                sharedPreference.put(PreferenceKeys.KEY_RINGTONE_NAME, ringToneName)
                tv_alarm_music.text = ringToneName
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateAlarmTriggerUI()
        mContext.registerReceiver(mMessageReceiver, IntentFilter(Constants.INTENT_FILTER_ALARM_ACTION))
    }

    override fun onPause() {
        super.onPause()
        mContext.unregisterReceiver(mMessageReceiver)
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateAlarmTriggerUI()
        }
    }

    companion object {
        private const val HOUR_OF_DAY = 24
        private const val MINUTES_OF_HOUR = 60
        private const val STEP_NUMBER = 5
        private const val BASIC_HOUR = 2
        private const val REQUEST_CODE_RINGTONE = 1000
        private val SOUND_DURATIONS = intArrayOf(15, 30, 60, 120)

        private val MINUTES : Array<String> = Array(12, {( it * STEP_NUMBER ).toString()})

        fun newInstance(): AlarmFragment {
            val fragmemt = AlarmFragment()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}
