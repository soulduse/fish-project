package com.dave.fish.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dave.fish.R
import com.dave.fish.db.RealmController
import com.dave.fish.model.WeeklyModel
import com.dave.fish.network.RetrofitController
import com.sickmartian.calendarview.CalendarView
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_menu_one.*
import kotlinx.android.synthetic.main.view_item_add_calendar.view.*
import org.joda.time.DateTime
import java.util.*


/**
 * Created by soul on 2017. 8. 27..
 */
class FragmentMenuOne : Fragment(){
    var firstDayOfWeek : Int ?= null

    private val DAY_PARAMETER = "day"
    private val MONTH_PARAMETER = "month"
    private val YEAR_PARAMETER = "year"
    private val FIRST_DAY_OF_WEEK_PARAMETER = "firstDay"

    private var mYear: Int = 0
    private var mDay: Int = 0
    private var mMonth: Int = 0

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstDayOfWeek = CalendarView.MONDAY_SHIFT
        realm = Realm.getDefaultInstance()
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_menu_one, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
        val selectedSpinnerItem = RealmController.instance.findSelectedSpinnerItem(realm)
        Log.w(TAG, "doNm : ${selectedSpinnerItem?.doNm}, postNm : ${selectedSpinnerItem?.postName}")
        selectedSpinnerItem?.let {
            val postId = RealmController
                    .instance
                    .findByPostName(
                            realm,
                            selectedSpinnerItem.doNm,
                            selectedSpinnerItem.postName
                    )?.obsPostId
            postId?.let {
                val minDayOfMonth = DateTime().dayOfMonth().withMinimumValue()
                initData(postId, minDayOfMonth)
                initData(postId, minDayOfMonth.plusDays(7))
                initData(postId, minDayOfMonth.plusDays(14))
                initData(postId, minDayOfMonth.plusDays(21))
                initData(postId, minDayOfMonth.plusDays(28))
            }
        }


        setDateByStateDependingOnView()
        monthView.firstDayOfTheWeek = CalendarView.SUNDAY_SHIFT
        monthView.setCurrentDay(getCalendarForState())
    }

    fun initData(postId : String, dateTime: DateTime){
        Log.d(TAG, "postID --> $postId, dateTime : ${dateTime}")
        RetrofitController().getWeeklyData(postId, dateTime)
                .subscribe({
                    tideModel->
                    val weeklyDataList = tideModel.weeklyDataList
                    for(item : WeeklyModel.WeeklyData in weeklyDataList!!){
                        Log.w(TAG, "What is data items --> ${item.toString()}")
                        val testView = layoutInflater.inflate(R.layout.view_item_add_calendar, null)

                        val lowTide = getContainLowTide(item)
                        val firstTideHeight = getSplitListItem(lowTide[0])
                        val secondTideHeight = getSplitListItem(lowTide[1])

                        testView.tv_tide_level.text = firstTideHeight
                        testView.tv_tide_level2.text = secondTideHeight

                        Log.w(TAG, "firstTideHeight : $firstTideHeight, secondTideHeight : $secondTideHeight")

                        if(firstTideHeight.isNotEmpty() && firstTideHeight.toInt() <= 100){
                            testView.tv_tide_level.setTextColor(Color.RED)
                        }
                        if(secondTideHeight.isNotEmpty() && secondTideHeight.toInt() <= 100){
                            testView.tv_tide_level2.setTextColor(Color.RED)
                        }

                        item.am?.let {
                            val weatherIcon = getWeatherIcon(item?.am)
                            if(weatherIcon != 0){
                                Glide.with(context)
                                        .load(getWeatherIcon(item?.am))
                                        .into(testView.iv_tide_state)
                            }
                        }

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

    private fun getWeatherIcon(weather : String) : Int{
        return when(weather.toUpperCase()){
            WeatherIcons.CLOUD.name -> R.drawable.icon_cloud_sun
            WeatherIcons.MORECLOUD.name -> R.drawable.ic_cloud_grey_500_24dp
            WeatherIcons.RAIN.name -> R.drawable.icon_rain
            WeatherIcons.SUN.name -> R.drawable.ic_wb_sunny_grey_500_24dp
            WeatherIcons.CLOUDRAIN.name -> R.drawable.icon_rain
            else -> 0
        }
    }

    fun getContainLowTide(item : WeeklyModel.WeeklyData) : Array<String> {
        val lowItem = Array(2){"";""}
        val CONTAIN_STR = "저"
        var count = 0
        if(item.lvl1.contains(CONTAIN_STR)){
            var waterHeight = getSplitListItem(item.lvl1)
            Log.d(TAG, "height lvl1 --> ${waterHeight}")
            if(lowItem[count].isEmpty()){
                lowItem[count] = waterHeight
            }else{
                lowItem[count++] = waterHeight
            }

        }
        if(item.lvl2.contains(CONTAIN_STR)){
            var waterHeight = getSplitListItem(item.lvl2)
            Log.d(TAG, "height lvl2 --> ${waterHeight}")
            if(lowItem[count].isEmpty()){
                lowItem[count] = waterHeight
            }else{
                lowItem[++count] = waterHeight
            }
        }
        if(item.lvl3.contains(CONTAIN_STR)){
            var waterHeight = getSplitListItem(item.lvl3)
            Log.d(TAG, "height lvl3 --> ${waterHeight}")
            if(lowItem[count].isEmpty()){
                lowItem[count] = waterHeight
            }else{
                lowItem[++count] = waterHeight
            }
        }
        if(item.lvl4.contains(CONTAIN_STR)){
            var waterHeight = getSplitListItem(item.lvl4)
            Log.d(TAG, "height lvl4 --> ${waterHeight}")
            if(lowItem[count].isEmpty()){
                lowItem[count] = waterHeight
            }else{
                lowItem[++count] = waterHeight
            }
        }

        return lowItem
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

        enum class WeatherIcons{
            SUN, RAIN, CLOUD, MORECLOUD, CLOUDRAIN
        }
    }
}