package com.dave.fish.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dave.fish.R
import com.dave.fish.api.ApiProvider
import com.dave.fish.api.Network
import com.dave.fish.api.NetworkCallback
import com.dave.fish.api.model.WeeklyModel
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.model.TideWeeklyModel
import com.dave.fish.util.DLog
import com.dave.fish.util.DateUtil
import com.dave.fish.util.DateUtil.DATE_PATTERN_YEAR_MONTH_DAY
import com.dave.fish.util.Global
import com.dave.fish.util.TideUtil
import com.google.gson.Gson
import com.sickmartian.calendarview.CalendarView
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_menu_calendar.*
import kotlinx.android.synthetic.main.view_item_add_calendar.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*


/**
 * Created by soul on 2017. 8. 27..
 */
class CalendarFragment : Fragment() {
    private var firstDayOfWeek: Int? = null
    private var mDate: DateTime = DateTime()

    private lateinit var mContext: Context

    private var lockSelect = false

    private val mRealmController: RealmProvider = RealmProvider.instance

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_menu_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = view.context
        initView()
        refreshCalendar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstDayOfWeek = CalendarView.MONDAY_SHIFT
        if (savedInstanceState == null) {
            setCurrentDate()
        } else {
            mDate = DateTime(
                    savedInstanceState.getInt(YEAR_PARAMETER),
                    savedInstanceState.getInt(MONTH_PARAMETER),
                    savedInstanceState.getInt(DAY_PARAMETER),
                    0,0,0,0
            )
            firstDayOfWeek = savedInstanceState.getInt(FIRST_DAY_OF_WEEK_PARAMETER)
        }
    }

    private fun initView(){
        onMoveCalendar()
        initMonthView()
    }

    private fun refreshCalendar() {
        if(isCurrentDate(mDate)){
            monthView.setCurrentDay(getCalendarForState())
        }else{
            monthView.setCurrentDay(0)
        }

        val postId = mRealmController.getSecondSpinnerItem()?.obsPostId
        DLog.w("postid ---> $postId")
        postId?.let {
            val minDayOfMonth = getDateForState().dayOfMonth().withMinimumValue()
            val maxDayOfMonth = getDateForState().dayOfMonth().withMaximumValue()
            val currentDayOfMonth = getDateForState().dayOfMonth()
            val tideMonthList = mRealmController.findTideMonth(postId, getDateForState())

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
        monthView.setCurrentDay(getCalendarForState())

        monthView.setDaySelectedListener(object : CalendarView.DaySelectionListener {
            override fun onLongClick(p0: CalendarView?, p1: CalendarView.DayMetadata?) {
            }

            override fun onTapEnded(p0: CalendarView, p1: CalendarView.DayMetadata) {
                val date = DateTime(p1.year, p1.month, p1.day, 0, 0, 0).toString("yyyy-MM-dd")
                val intent = Intent(mContext, TideDetailActivity::class.java)
                intent.putExtra(Global.INTENT_DATE, date)
                activity?.startActivity(intent)
            }
        })
        setDateByStateDependingOnView()
    }

    private fun onMoveCalendar() {
        tv_prev_month.setOnClickListener {
            movePrevMonth()
            refreshCalendar()
        }

        tv_current_month.setOnClickListener {
            setDateByStateDependingOnView()
            refreshCalendar()
        }

        tv_next_month.setOnClickListener {
            moveNextMonth()
            refreshCalendar()
        }
    }

    private fun initCalendarData(postId: String, dateTime: DateTime, tideMonthList : RealmResults<TideWeeklyModel>) {
        if(!lockSelect && tideMonthList.isNotEmpty()){
            addDataToCalendar(tideMonthList, postId)
            lockSelect = true
        }else if(tideMonthList.size < MINIMUM_DAY_SIZE){
            requestMonthData(postId, dateTime)
        }
    }

    private fun requestMonthData(postId: String, dateTime: DateTime){
        Network.request(ApiProvider.provideTideApi().getWeeklyData(
                postId,
                dateTime.toString(DATE_PATTERN_YEAR_MONTH_DAY)
        ), NetworkCallback<WeeklyModel>().apply {
            success = { tideModel->
                val weeklyDataList = tideModel.weeklyDataList

                weeklyDataList.forEach {
                    val weeklyModel = getWeeklyFromJson(it).apply {
                        this.key = it.obsPostName + "_" + it.searchDate
                        this.postId = postId
                    }

                    mRealmController.writeData(weeklyModel)
                }


                addDataToCalendar(weeklyDataList, postId)
            }

            error = { e->
                Toast.makeText(mContext, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initDataOnThisMonth(postId: String, dateTime: DateTime){
        if(DateUtil.isThisMonth(dateTime)){
            requestMonthData(postId, dateTime)
        }
    }

    private fun getWeeklyFromJson(data: WeeklyModel.WeeklyData): TideWeeklyModel {
        val gson = Gson()
        val weeklyJson = gson.toJson(data)
        return gson.fromJson<TideWeeklyModel>(weeklyJson, TideWeeklyModel::class.java)
    }

    private fun addDataToCalendar(itemList: List<Any>, postId: String) {
        itemList.forEach { item ->
            val calendarItemView = layoutInflater.inflate(R.layout.view_item_add_calendar, null)
            drawTideValues(item, calendarItemView)

            var tideDate = DateTime()
            when (item) {
                is WeeklyModel.WeeklyData -> {
                    val weeklyModel = getWeeklyFromJson(item).apply {
                        this.key = item.obsPostName + "_" + item.searchDate
                        this.postId = postId
                    }

                    mRealmController.writeData(weeklyModel)

                    item.am.let {
                        val weatherIcon = getWeatherIcon(item.am)
                        if (weatherIcon != 0) {
                            Glide.with(mContext)
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
                            Glide.with(mContext)
                                    .load(getWeatherIcon(item.am))
                                    .into(calendarItemView.iv_tide_state)
                            calendarItemView.iv_tide_state.visibility = View.VISIBLE
                        }
                    }
                    tideDate = DateTime(item.searchDate)
                }
            }

            if (isEmptyInMonthView(tideDate)) {
                monthView.addViewToDay(CalendarView.DayMetadata(tideDate.year, tideDate.monthOfYear, tideDate.dayOfMonth),
                        calendarItemView)
            }

        }
    }

    private fun drawTideValues(item: Any, calendarItemView: View) {
        val lowTideList = getLowTide(item)
        drawTideLevel(calendarItemView.tv_tide_level, lowTideList[0])
        if(lowTideList.size > 1){
            drawTideLevel(calendarItemView.tv_tide_level2, lowTideList[1])
        }
    }

    private fun drawTideLevel(tv: TextView, lowTide: String){
        tv.text = TideUtil.getHeight(lowTide)

        if (TideUtil.getHeight(lowTide).toInt() <= 100) {
            tv.setTextColor(Color.RED)
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

    private fun getWeatherIcon(weather: String): Int = when (weather.toUpperCase()) {
        WeatherIcons.CLOUD.name -> R.drawable.icon_cloud_sun
        WeatherIcons.MORECLOUD.name -> R.drawable.ic_cloud_grey_500_24dp
        WeatherIcons.RAIN.name -> R.drawable.icon_rain
        WeatherIcons.SUN.name -> R.drawable.ic_wb_sunny_grey_500_24dp
        WeatherIcons.CLOUDRAIN.name -> R.drawable.icon_rain
        else -> 0
    }

    private fun getLowTide(item: Any): MutableList<String> {
        when (item) {
            is WeeklyModel.WeeklyData -> TideUtil.setTide(item)

            is TideWeeklyModel -> TideUtil.setTide(item)
        }
        return TideUtil.getLowItemList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState){
            putInt(DAY_PARAMETER, mDate.dayOfMonth)
            putInt(MONTH_PARAMETER, mDate.monthOfYear)
            putInt(YEAR_PARAMETER, mDate.year)
            putInt(FIRST_DAY_OF_WEEK_PARAMETER, monthView.firstDayOfTheWeek)
        }
    }

    private fun setCurrentDate() {
        val seoul = DateTimeZone.forID("Asia/Seoul")
        mDate = DateTime(seoul)
    }

    private fun getDateForState() = DateTime(
            mDate.year,
            mDate.monthOfYear,
            mDate.dayOfMonth,
            0,
            0,
            0,
            0
    )

    private fun getCalendarForState() = getDateForState().toCalendar(Locale.KOREA)

    private fun isCurrentDate(compareDate: DateTime) =
            DateTime().toString(DATE_PATTERN_YEAR_AND_MONTH) == compareDate.toString(DATE_PATTERN_YEAR_AND_MONTH)

    private fun movePrevMonth() {
        mDate = mDate.minusMonths(1)
        setMonthViewDate()
    }

    private fun moveNextMonth() {
        mDate = mDate.plusMonths(1)
        setMonthViewDate()
    }

    private fun setDateByStateDependingOnView() {
        setCurrentDate()
        setMonthViewDate()
    }

    @SuppressLint("SetTextI18n")
    private fun setMonthViewDate() {
        monthView.setDate(mDate.monthOfYear, mDate.year)
        calendar_date.text = "${mDate.year}.${mDate.monthOfYear}"
    }

    fun setVisibleNavigationCalendar(visible : Int){
        navigation_calendar_container?.visibility = visible
    }

    companion object {
        private val DAY_PARAMETER = "day"
        private val MONTH_PARAMETER = "month"
        private val YEAR_PARAMETER = "year"
        private val DATE_PATTERN_YEAR_AND_MONTH = "yyyyMM"
        private val FIRST_DAY_OF_WEEK_PARAMETER = "firstDay"
        private val MINIMUM_DAY_SIZE = 28

        enum class WeatherIcons {
            SUN, RAIN, CLOUD, MORECLOUD, CLOUDRAIN
        }

        fun newInstance() : CalendarFragment {
            val fragmemt = CalendarFragment()
            val bundle = Bundle()
            fragmemt.arguments = bundle

            return fragmemt
        }
    }
}
