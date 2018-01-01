package com.dave.fish.ui.alarm

import android.os.Bundle
import android.widget.NumberPicker
import com.dave.fish.R
import com.dave.fish.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_alaram.*

/**
 * Created by soul on 2017. 12. 16..
 */
class AlarmFragment : BaseFragment() {

    override fun getContentId(): Int = R.layout.fragment_alaram

    override fun initViews(savedInstanceState: Bundle?) {
        initNumberPicker()
    }

    override fun initData() {
        initAlarm()
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

    private fun initAlarm(){
        result_time.text = resources.getString(
                R.string.number_picker_selected_values,
                num_picker_hour.value,
                num_picker_min.value * STEP_NUMBER
        )
    }

    private val changedListener = NumberPicker.OnValueChangeListener { _, _, _ ->
        initAlarm()
    }

    companion object {
        private val HOUR_OF_DAY = 24
        private val MINUTES_OF_HOUR = 60
        private val STEP_NUMBER = 5
        private val BASIC_HOUR = 2

        private val MINUTES : Array<String> = Array(12, {(it*5).toString()})

        fun newInstance(): AlarmFragment {
            val fragmemt = AlarmFragment()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}
