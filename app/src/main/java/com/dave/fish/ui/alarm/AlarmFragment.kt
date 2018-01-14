package com.dave.fish.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.dave.fish.MyApplication
import com.dave.fish.R
import com.dave.fish.util.DLog
import kotlinx.android.synthetic.main.fragment_alaram.*
import org.joda.time.DateTime


/**
 * Created by soul on 2017. 12. 16..
 */
class AlarmFragment : Fragment() {

    private lateinit var alarmMgr: AlarmManager

    private lateinit var alarmIntent: Intent

    private lateinit var pendingIntent: PendingIntent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_alaram, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNumberPicker()
        initAlarm()
        getAlarmTime()
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

    private fun initAlarm() {
        alarmMgr = MyApplication.context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent("com.dave.fish.START_ALARM")
        pendingIntent = PendingIntent.getBroadcast(MyApplication.context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        start_alarm.setOnClickListener {
            setExactAlarm(
                    AlarmManager.RTC_WAKEUP,
                    getAlarmTime(),
                    pendingIntent
            )
        }
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
    }

    private val changedListener = NumberPicker.OnValueChangeListener { _, _, _ ->
        initAlarmData()
    }

    companion object {
        private val HOUR_OF_DAY = 24
        private val MINUTES_OF_HOUR = 60
        private val STEP_NUMBER = 5
        private val BASIC_HOUR = 2

        private val MINUTES : Array<String> = Array(12, {( it * STEP_NUMBER ).toString()})

        fun newInstance(): AlarmFragment {
            val fragmemt = AlarmFragment()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}
