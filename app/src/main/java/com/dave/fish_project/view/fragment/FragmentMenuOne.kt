package com.dave.fish_project.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dave.fish_project.R
import com.dave.fish_project.model.WeeklyModel
import com.dave.fish_project.network.RetrofitController
import com.sickmartian.calendarview.CalendarView
import kotlinx.android.synthetic.main.fragment_menu_one.*
import org.joda.time.DateTime
import java.util.*
import android.graphics.Color.parseColor
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_item_add_calendar.view.*


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
        initData()
    }

    private fun initData(){
        RetrofitController().getWeeklyData()
                .subscribe({
                    tideModel->
                    var weeklyDataList = tideModel.weeklyDataList
                    for(item : WeeklyModel.WeeklyData in weeklyDataList!!){
                        Log.d(TAG, item.toString())
                        val testView = layoutInflater.inflate(R.layout.view_item_add_calendar, null)
//                        testView.tv_tide_level.text = "1234"
                        var dateTextView = TextView(context)
//                        dateTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                        dateTextView.setBackgroundColor(Color.parseColor("#00FFFFFF"))
//                        dateTextView.setTextColor(Color.parseColor("#FF7200"))
                        dateTextView.textSize = 6f
                        dateTextView.text = """
                            ${getSplitListItem(item.lvl1)}
                            ${getSplitListItem(item.lvl2)}
                            ${getSplitListItem(item.lvl3)}
                            ${getSplitListItem(item.lvl4)}
                                            """

                        Glide.with(context)
                                .load(R.drawable.ic_sentiment_dissatisfied_cyan_400_24dp)
                                .into(testView.iv_tide_state)

                        item.lvl1
                        item.lvl2
                        item.lvl3
                        item.lvl4


                        testView.tv_tide_level.text = getSplitListItem(item.lvl1)
                        testView.tv_tide_level2.text = getSplitListItem(item.lvl3)

                        Log.d(TAG, """
                            (저)lvl1 - ${getSplitListItem(item.lvl1)}
                            (고)lvl2 - ${getSplitListItem(item.lvl2)}
                            (저)lvl3 - ${getSplitListItem(item.lvl3)}
                            (고)lvl4 - ${getSplitListItem(item.lvl4)}
                                            """)

                        var tideDate = DateTime(item.searchDate)
                        Log.d(TAG, """
                            ㄴ year ---> ${tideDate.year}
                            ㄴ month ---> ${tideDate.monthOfYear}
                            ㄴ day ---> ${tideDate.dayOfMonth}
                            """)
                        monthView.addViewToDay(CalendarView.DayMetadata(tideDate.year, tideDate.monthOfYear, tideDate.dayOfMonth),
                                testView)
                    }
                    Log.d(TAG, "used api")
                }, {
                    e ->
                    Log.d(TAG, "Something wrong --> ${e.localizedMessage}")
                })
    }

    fun getContainLowTide() : String{

    }

    fun getSplitListItem(lvlItem : String) : String{
        return lvlItem.split("/").last()
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