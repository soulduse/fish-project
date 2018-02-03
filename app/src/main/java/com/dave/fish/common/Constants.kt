package com.dave.fish.common

/**
 * Created by soul on 2017. 11. 16..
 */
object Constants {

    val KEY_TIDE_MAIN_SPINNER = 0
    val KEY_TIDE_SIDE_SPINNER = 1

    // WebView URL
    val KMA_M_URL = "http://m.kma.go.kr/m/index.jsp"
    val MARIN_KMA_M_URL = "http://marine.kma.go.kr/m/main.html"
    val FLOW_M_URL = "https://www.windy.com/?36.408,127.895,6"

    // Notification
    val NOTIFICATION_CHANEL = "notification_chanel"
    val EXTRA_NOTIFICATION_ID = 1001
    val EXTRA_NOTIFIER = "EXTRA_NOTIFIER"

    // Location
    val UPDATE_COMPLETE_LOCATION = 200
    val LOCATION_SERVICE_RESULT = "com.dave.fish.LOCATION_RESULT"
    val LOCATION_SERVICE_MESSAGE = "com.dave.fish.LOCATION_MESSAGE"
    val RESPONSE_LOCATION_VALUES = "com.dave.fish.LOCATION_VALUES"

    // INTENT EXTRA
    val EXTRA_RINGTONE_URI = "RINGTONE_URI"
    val EXTRA_RINGTONE_DURATION = "RINGTONE_DURATION"

    // Alarm BroadcastReceiver
    val INTENT_FILTER_ALARM_ACTION = "FILTER_ALARM_ACTION"

    // bundle
    val BUNDLE_FRAGMENT_URL = "fragment_url"
}
