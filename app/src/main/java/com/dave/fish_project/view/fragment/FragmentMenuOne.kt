package com.dave.fish_project.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dave.fish_project.R
import com.dave.fish_project.model.WeeklyModel
import com.dave.fish_project.network.RetrofitController
import com.sickmartian.calendarview.CalendarView
import kotlinx.android.synthetic.main.fragment_menu_one.*
import java.util.*

/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentMenuOne : Fragment(){

    var tideData : WeeklyModel?= null
    var firstDayOfWeek : Int ?= null

    private val DAY_PARAMETER = "day"
    private val MONTH_PARAMETER = "month"
    private val YEAR_PARAMETER = "year"
    private val FIRST_DAY_OF_WEEK_PARAMETER = "firstDay"

    private var mYear: Int = 0
    private var mDay: Int = 0
    private var mMonth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initData()
        firstDayOfWeek = CalendarView.MONDAY_SHIFT
        if (savedInstanceState == null) {
            val cal = Calendar.getInstance()
            setStateByCalendar(cal)
        } else {
            mDay = savedInstanceState.getInt(DAY_PARAMETER)
            mMonth = savedInstanceState.getInt(MONTH_PARAMETER)
            mYear = savedInstanceState.getInt(YEAR_PARAMETER)
            firstDayOfWeek = savedInstanceState.getInt(FIRST_DAY_OF_WEEK_PARAMETER)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // The two views can't have the same id, or the state won't be preserved
        // correctly and they will throw an exception

        return inflater?.inflate(R.layout.fragment_menu_one, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDateByStateDependingOnView()
        monthView.firstDayOfTheWeek = CalendarView.MONDAY_SHIFT
        monthView.setCurrentDay(getCalendarForState())
    }

    private fun initData(){
        RetrofitController().getWeeklyData()
                .subscribe({
                    tideModel->
                    var weeklyDataList = tideModel.weeklyDataList
                    for(item : WeeklyModel.WeeklyData in weeklyDataList!!){
                    }
                    Log.d(TAG, "used api")
                }, {
                    erorr ->
                    Log.d(TAG, "Something wrong")
                })
    }

    fun setStateByCalendar(cal: Calendar) {
        mYear = cal.get(Calendar.YEAR)
        mMonth = cal.get(Calendar.MONTH) + 1 // We use base 1 months..
        // You should use joda time or a sane Calendar really
        mDay = cal.get(Calendar.DATE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(DAY_PARAMETER, mDay)
        outState.putInt(MONTH_PARAMETER, mMonth)
        outState.putInt(YEAR_PARAMETER, mYear)
        outState.putInt(FIRST_DAY_OF_WEEK_PARAMETER, monthView.firstDayOfTheWeek)
    }

    private fun getCalendarForState(): Calendar {
        val newCalendar = Calendar.getInstance()
        newCalendar.minimalDaysInFirstWeek = 1
        newCalendar.firstDayOfWeek = Calendar.SUNDAY
        newCalendar.set(Calendar.YEAR, mYear)
        newCalendar.set(Calendar.MONTH, mMonth - 1)
        newCalendar.set(Calendar.DATE, mDay)
        newCalendar.set(Calendar.HOUR_OF_DAY, 0)
        newCalendar.set(Calendar.MINUTE, 0)
        newCalendar.set(Calendar.SECOND, 0)
        newCalendar.set(Calendar.MILLISECOND, 0)
        return newCalendar
    }

    private fun setDateByStateDependingOnView() {
        monthView.setDate(mMonth, mYear)
    }

    companion object {
        private val TAG = FragmentMenuOne.javaClass.simpleName
    }
}