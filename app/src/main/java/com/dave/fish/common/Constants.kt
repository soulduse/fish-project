package com.dave.fish.common

/**
 * Created by soul on 2017. 11. 16..
 */
object Constants {

    const val KEY_TIDE_MAIN_SPINNER = 0
    const val KEY_TIDE_SIDE_SPINNER = 1

    // WebView URL
    const val KMA_M_URL = "http://m.kma.go.kr/m/index.jsp"
    const val MARIN_KMA_M_URL = "http://marine.kma.go.kr/m/main.html"
    const val FLOW_M_URL = "https://www.windy.com/?36.408,127.895,6"

    // Notification
    const val NOTIFICATION_CHANEL = "notification_chanel"
    const val EXTRA_NOTIFICATION_ID = 1001
    const val EXTRA_NOTIFIER = "EXTRA_NOTIFIER"

    // Location
    const val UPDATE_COMPLETE_LOCATION = 200
    const val LOCATION_SERVICE_RESULT = "com.dave.fish.LOCATION_RESULT"
    const val LOCATION_SERVICE_MESSAGE = "com.dave.fish.LOCATION_MESSAGE"
    const val RESPONSE_LOCATION_VALUES = "com.dave.fish.LOCATION_VALUES"

    // INTENT EXTRA
    const val EXTRA_RINGTONE_URI = "RINGTONE_URI"
    const val EXTRA_RINGTONE_DURATION = "RINGTONE_DURATION"
    const val EXTRA_LOCATION_MODEL_IDX = "EXTRA_LOCATION_MODEL_IDX"
    const val EXTRA_LOCATION_LAT = "EXTRA_LOCATION_LAT"
    const val EXTRA_LOCATION_LON = "EXTRA_LOCATION_LON"
    const val EXTRA_LOCATION_CREATED_AT = "EXTRA_LOCATION_CREATED_AT"
    const val EXTRA_PERMISSIONS = "EXTRA_PERMISSIONS"
    const val EXTRA_PERMISSION_ALERT_MESSAGE = "EXTRA_PERMISSION_ALERT_MESSAGE"

    // Alarm BroadcastReceiver
    const val INTENT_FILTER_ALARM_ACTION = "FILTER_ALARM_ACTION"

    // bundle
    const val BUNDLE_FRAGMENT_URL = "fragment_url"
}
