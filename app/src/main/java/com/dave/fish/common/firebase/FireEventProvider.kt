package com.dave.fish.common.firebase

import android.os.Bundle
import com.dave.fish.MyApplication
import com.google.firebase.analytics.FirebaseAnalytics
import org.joda.time.DateTime

/**
 * Created by soul on 2018. 2. 19..
 */
object FireEventProvider {
    private const val TAB_TIDE_WEATHER = "탭_물때_날씨"
    private const val TAB_MAP_RECORD = "탭_지도_기록"
    private const val TAB_K_WEATHER = "탭_국내예보"
    private const val TAB_F_WEATHER = "탭_해외예보"
    private const val TAB_ALARM = "탭_알림설정"
    private const val TAB_TIP = "탭_해루질팁"
    const val TAB_SETTING = "탭_기타"

    val TAB_ARRAY = arrayOf(
            TAB_TIDE_WEATHER,
            TAB_MAP_RECORD,
            TAB_K_WEATHER,
            TAB_F_WEATHER,
            TAB_ALARM,
            TAB_TIP)

    // 메인
    const val MAIN_FIRST_SPINNER = "메인_첫번째_스피너"
    const val MAIN_SECOND_SPINNER = "메인_두번째_스피너"

    // 사이드스피너
    const val SIDE_SPINNER = "사이드_스피너"

    // 물때&날씨
    const val DETAIL_SELECT_CALENDAR = "물때날씨_스피너_선택값"
    const val DETAIL_SELECTED_CALENDAR = "물때표_디테일뷰_진입"
    const val DETAIL_SHARE = "물때날씨_물때표_내용공유"

    // 지도&기록
    const val MAP_START_RECORD = "지도_기록시작"
    const val MAP_STOP_RECORD = "지도_기록종료"
    const val MAP_SHOW_RECORDED = "지도_기록보기"
    const val MAP_SHOW_DETAIL_RECORDED = "지도_기록리스트_확인"

    // 국내예보
    private const val K_WEATHER_A = "국내예보_기상청"
    private const val K_WEATHER_B = "국내예보_해양날씨"
    val K_WEATHER_ARRAY = arrayOf(
            K_WEATHER_A,
            K_WEATHER_B)

    // 해외예보
    private const val F_WEATHER_A = "해외예보_파고예보"
    private const val F_WEATHER_B = "해외예보_비구름예보"
    private const val F_WEATHER_C = "해외예보_강수량예보"
    private const val F_WEATHER_D = "해외예보_날씨흐름"
    val F_WEATHER_ARRAY = arrayOf(
            F_WEATHER_A,
            F_WEATHER_B,
            F_WEATHER_C,
            F_WEATHER_D
    )

    // 알림설정
    const val ALARM_DRAG_TIME = "알람_날짜변경"
    const val ALARM_CHANGE_DURATION = "알람_지속시간변경"
    const val ALARM_CHANGE_MUSIC = "알람_소리변경"
    const val ALARM_START = "알람_시작"
    const val ALARM_STOP = "알람_종료"

    // 해루질팁
    private const val TIP_CATCH_HISTORY = "팁_월별조과표"
    private const val TIP_TIDE = "팁_물때표_보는법"
    private const val TIP_INFO = "팁_해루질이란"
    val TIP_ARRAY = arrayOf(
            TIP_CATCH_HISTORY,
            TIP_TIDE,
            TIP_INFO)

    // 기타
    const val SETTING_SEND_MAIL = "기타_메일보내기"

    // 광고클릭
    const val AD_BANNER = "광고_하단배너_클릭"

    const val ICON = "아이콘클릭"

    private const val TIME_STAMP: String = "time_stamp"

    fun trackEvent(clickName: String, value: String? = null) {
        val bundle = getBaseBundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, clickName)
            value?.let { putString(FirebaseAnalytics.Param.VALUE, value) }
        }
        trackEvent(clickName, bundle)
    }

    private fun trackEvent(event: String, bundle: Bundle) {
        val context = MyApplication.getGlobalContext()
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle)
    }

    private fun getBaseBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(TIME_STAMP, DateTime().toString())
        return bundle
    }
}