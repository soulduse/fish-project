package com.dave.fish.view.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.model.realm.TideWeeklyModel
import com.dave.fish.model.retrofit.WeeklyModel
import com.dave.fish.network.RetrofitController
import com.dave.fish.util.DLog
import com.dave.fish.util.DateUtil
import com.dave.fish.util.Global
import com.dave.fish.util.TideUtil
import com.dave.fish.view.activity.TideDetailActivity
import com.sickmartian.calendarview.CalendarView
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_menu_one.*
import kotlinx.android.synthetic.main.view_item_add_calendar.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import java.util.*


/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentCalendar : BaseFragment() {
    private var firstDayOfWeek: Int? = null
    private var mYear: Int = 0
    private var mDay: Int = 0
    private var mMonth: Int = 0

    private var changingDate = LocalDate()
    private var lockSelect = false

    private val mRealmController: RealmController = RealmController.instance

    override fun getContentId(): Int = R.layout.fragment_menu_one

    override fun initViews(savedInstanceState: Bundle?) {
        initView()
    }

    override fun initData() {
        refreshCalendar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstDayOfWeek = CalendarView.MONDAY_SHIFT
        if (savedInstanceState == null) {
            setStateByCalendar()
        } else {
            mDay = savedInstanceState.getInt(DAY_PARAMETER)
            mMonth = savedInstanceState.getInt(MONTH_PARAMETER)
            mYear = savedInstanceState.getInt(YEAR_PARAMETER)
            firstDayOfWeek = savedInstanceState.getInt(FIRST_DAY_OF_WEEK_PARAMETER)
        }
    }

    private fun initView(){
        onMoveCalendar()
        initMonthView()
    }

    private fun refreshCalendar() {
        val yearAndMonth = "${changingDate.year}${changingDate.monthOfYear}"
        if(yearAndMonth == getCurrentDate()){
            monthView.setCurrentDay(getCalendarForState())
        }else{
            monthView.setCurrentDay(0)
        }

        val secondSpinnerItem = mRealmController.findSelectedSecondModel(realm)
        val postId = secondSpinnerItem.obsPostId
        postId.let {
            val minDayOfMonth = getDateForState().dayOfMonth().withMinimumValue()
            val maxDayOfMonth = getDateForState().dayOfMonth().withMaximumValue()
            val currentDayOfMonth = getDateForState().dayOfMonth()
            val tideMonthList = mRealmController.findTideMonth(realm, postId, getDateForState())

            val dayOfMonthList = mutableListOf<DateTime>(
                    minDayOfMonth,
                    minDayOfMonth.plusDays(7),
                    minDayOfMonth.plusDays(14),
                    minDayOfMonth.plusDays(21),
                    maxDayOfMonth.minusDays(6)
            )

            initDataOnThisMonth(postId, currentDayOfMonth.dateTime)

            dayOfMonthList.forEach {
                initCalendarData(postId, it, tideMonthList)
            }

            lockSelect = false
        }
    }

    private fun initMonthView(){
        monthView.firstDayOfTheWeek = CalendarView.SUNDAY_SHIFT
        DLog.d("getCalendarForState() --> ${getCalendarForState()}")
        monthView.setCurrentDay(getCalendarForState())

        monthView.setDaySelectedListener(object : CalendarView.DaySelectionListener {
            override fun onLongClick(p0: CalendarView?, p1: CalendarView.DayMetadata?) {
            }

            override fun onTapEnded(p0: CalendarView, p1: CalendarView.DayMetadata) {
                val date = "${p1.year}-${addZeroToDate(p1.month)}-${addZeroToDate(p1.day)}"
                val intent = Intent(context, TideDetailActivity::class.java)
                intent.putExtra(Global.INTENT_DATE, date)
                activity.startActivity(intent)
            }
        })
        setDateByStateDependingOnView()
    }

    private fun onMoveCalendar() {
        tv_prev_month.setOnClickListener {
            setPrevDateByStateDependingOnView()
            refreshCalendar()
        }

        tv_current_month.setOnClickListener {
            setDateByStateDependingOnView()
            refreshCalendar()
        }

        tv_next_month.setOnClickListener {
            setNextDateByStateDependingOnView()
            refreshCalendar()
        }
    }

    private fun initCalendarData(postId: String, dateTime: DateTime, tideMonthList : RealmResults<TideWeeklyModel>) {
        if(!lockSelect && tideMonthList.isNotEmpty()){
            addDataToCalendar(tideMonthList, postId)
            lockSelect = true
        }else if(tideMonthList.isEmpty()){
            requestMonthData(postId, dateTime)
        }
    }

    private fun requestMonthData(postId: String, dateTime: DateTime){
        RetrofitController
                .instance
                .getWeeklyData(postId, dateTime)
                .subscribe({ tideModel ->
                    val weeklyDataList = tideModel.weeklyDataList
                    addDataToCalendar(weeklyDataList, postId)
                }, { e ->
                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                })
    }

    private fun initDataOnThisMonth(postId: String, dateTime: DateTime){
        if(DateUtil.isThisMonth(dateTime)){
            requestMonthData(postId, dateTime)
        }
    }

    private fun addDataToCalendar(itemList: List<Any>, postId: String) {
        itemList.forEach { item ->
            val calendarItemView = layoutInflater.inflate(R.layout.view_item_add_calendar, null)
            val lowTideList = getLowTide(item)
            for (i in 0..lowTideList.size) {
                when (i) {
                    0 -> {
                        calendarItemView.tv_tide_level.text = TideUtil.getHeight(lowTideList[i])
                        if (TideUtil.getHeight(lowTideList[i]).toInt() <= 100) {
                            calendarItemView.tv_tide_level.setTextColor(Color.RED)
                        }
                    }

                    1 -> {
                        try {
                            calendarItemView.tv_tide_level2.text = TideUtil.getHeight(lowTideList[i])
                            if (TideUtil.getHeight(lowTideList[i]).toInt() <= 100) {
                                calendarItemView.tv_tide_level2.setTextColor(Color.RED)
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            calendarItemView.tv_tide_level2.text = ""
                        }
                    }
                }
            }

            var tideDate = DateTime()
            when (item) {
                is WeeklyModel.WeeklyData -> {
                    mRealmController.setTideWeekly(realm, item, postId)
                    item.am.let {
                        val weatherIcon = getWeatherIcon(item.am)
                        if (weatherIcon != 0) {
                            Glide.with(context)
                                    .load(getWeatherIcon(item.am))
                                    .into(calendarItemView.iv_tide_state)
                            calendarItemView.iv_tide_state.visibility = View.VISIBLE
                        }
                    }
                    tideDate = DateTime(item.searchDate)
                }

                is TideWeeklyModel -> {
                    item.am.let {
                        val weatherIcon = getWeatherIcon(item.am)
                        if (weatherIcon != 0) {
                            Glide.with(context)
                                    .load(getWeatherIcon(item.am))
                                    .into(calendarItemView.iv_tide_state)
                            calendarItemView.iv_tide_state.visibility = View.VISIBLE
                        }
                    }
                    tideDate = DateTime(item.searchDate)
                }
            }
            if (isEmptyInMonthView(tideDate)) {
                DLog.w("Item instanceOf --> passed Date (${tideDate.year}/${tideDate.monthOfYear}/${tideDate.dayOfMonth})")
                monthView.addViewToDay(CalendarView.DayMetadata(tideDate.year, tideDate.monthOfYear, tideDate.dayOfMonth),
                        calendarItemView)
            }

        }
    }

    private fun isEmptyInMonthView(tideDate: DateTime): Boolean {
        val dayContentList = monthView
                .getDayContent(
                        CalendarView.DayMetadata(
                                tideDate.year,
                                tideDate.monthOfYear,
                                tideDate.dayOfMonth)
                )

        if (null == dayContentList || dayContentList.isEmpty()) {
            return true
        }

        return false
    }

    private fun getWeatherIcon(weather: String): Int {
        return when (weather.toUpperCase()) {
            WeatherIcons.CLOUD.name -> R.drawable.icon_cloud_sun
            WeatherIcons.MORECLOUD.name -> R.drawable.ic_cloud_grey_500_24dp
            WeatherIcons.RAIN.name -> R.drawable.icon_rain
            WeatherIcons.SUN.name -> R.drawable.ic_wb_sunny_grey_500_24dp
            WeatherIcons.CLOUDRAIN.name -> R.drawable.icon_rain
            else -> 0
        }
    }

    fun getLowTide(item: Any): MutableList<String> {
        when (item) {
            is WeeklyModel.WeeklyData -> {
                TideUtil.setTide(item)
            }

            is TideWeeklyModel -> {
                TideUtil.setTide(item)
            }
        }
        return TideUtil.getLowItemList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(DAY_PARAMETER, mDay)
        outState.putInt(MONTH_PARAMETER, mMonth)
        outState.putInt(YEAR_PARAMETER, mYear)
        outState.putInt(FIRST_DAY_OF_WEEK_PARAMETER, monthView.firstDayOfTheWeek)
    }

    private fun addZeroToDate(dateNum: Int): String {
        if (dateNum < 10) {
            return "0" + dateNum
        }

        return "$dateNum"
    }

    fun setStateByCalendar() {
        val seoul = DateTimeZone.forID("Asia/Seoul")
        val dateTime = DateTime(seoul)
        mYear = dateTime.year
        mMonth = dateTime.monthOfYear
        mDay = dateTime.dayOfMonth
    }

    private fun getDateForState(): DateTime {
        return DateTime(mYear, mMonth, mDay, 0, 0, 0, 0)
    }

    private fun getCalendarForState(): Calendar {
        val newDateTime = DateTime(mYear, mMonth, mDay, 0, 0, 0, 0)
        return newDateTime.toCalendar(Locale.KOREA)
    }

    private fun getCurrentDate() : String{
        return DateTime().toString("yyyyMM")
    }

    private fun setPrevDateByStateDependingOnView() {
        changeYearAndMonth(--mMonth)
        monthView.setDate(mMonth, mYear)
        changingDate = changingDate.minusMonths(1)
        calendar_date.text = "$mYear.$mMonth"
    }

    private fun setNextDateByStateDependingOnView() {
        changeYearAndMonth(++mMonth)
        monthView.setDate(mMonth, mYear)
        changingDate = changingDate.plusMonths(1)
        calendar_date.text = "$mYear.$mMonth"
    }

    private fun setDateByStateDependingOnView() {
        setStateByCalendar()
        monthView.setDate(mMonth, mYear)
        changingDate = LocalDate.now()
        calendar_date.text = "$mYear.$mMonth"
    }

    private fun changeYearAndMonth(month: Int) {
        when {
            month < 1 -> {
                --mYear
                mMonth = 12
            }

            month > 12 -> {
                ++mYear
                mMonth = 1
            }
        }
    }

    fun setVisibleNavigationCalendar(visible : Int){
        navigation_calendar_container.visibility = visible
    }

    companion object {
        private val TAG = FragmentCalendar::class.java.simpleName
        private val DAY_PARAMETER = "day"
        private val MONTH_PARAMETER = "month"
        private val YEAR_PARAMETER = "year"
        private val FIRST_DAY_OF_WEEK_PARAMETER = "firstDay"

        enum class WeatherIcons {
            SUN, RAIN, CLOUD, MORECLOUD, CLOUDRAIN
        }
    }
}
