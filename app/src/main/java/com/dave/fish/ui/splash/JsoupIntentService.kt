package com.dave.fish.ui.splash

import android.app.IntentService
import android.content.Intent
import com.dave.fish.ui.fweather.WeatherRepo
import com.dave.fish.util.DLog
import com.dave.fish.util.PreferenceKeys
import com.dave.fish.util.getDefaultSharedPreferences
import com.dave.fish.util.put
import com.google.gson.Gson
import org.jetbrains.anko.longToast
import org.joda.time.DateTime
import org.jsoup.Jsoup
import kotlin.concurrent.thread

/**
 * Created by soul on 2018. 2. 23..
 */
class JsoupIntentService: IntentService("JsoupIntentService") {

    override fun onHandleIntent(intent: Intent) {
        initFWeatherData()
    }

    private fun initFWeatherData() {
        val savedFweatherDate = this.getDefaultSharedPreferences().getString(PreferenceKeys.KEY_F_WEATHER_JSOUP_SAVED_YEAR_MONTH_DAY, null)
        val fWeatherRepos = this.getDefaultSharedPreferences().getString(PreferenceKeys.KEY_F_WEATHER_JSOUP_LIST, null)
        val currentTime = DateTime()
        if (savedFweatherDate.isNullOrEmpty() ||
                fWeatherRepos.isNullOrEmpty() ||
                savedFweatherDate != currentTime.toString("yyyyMMdd")) {
            thread {
                try {
                    DLog.w("async start")
                    val baseURL = "http://www.imocwx.com/"
                    val urls = (0..24 step 2).map { "http://www.imocwx.com/cwm.php?Area=1&Time=$it" }
                    val currentDate = DateTime()

                    val weatherRepos = mutableListOf<WeatherRepo>()
                    urls.forEach {
                        val doc = Jsoup.connect(it).get()
                        val title = doc.select(".content .title").text()
                        val image = doc.select(".content img").attr("src")


                        val firstIdx = title.indexOf("${currentDate.year}")
                        val lastIdx = title.indexOf("(JST)") - 1
                        var dateJP = title.slice(firstIdx..lastIdx)
                        dateJP = dateJP
                                .replace("年", "년")
                                .replace("月", "월")
                                .replace("日", "일")
                                .replace("時", "시")

                        val prefixDate = dateJP.substringBefore("(")
                        val suffixDate = dateJP.substringAfter(")")

                        weatherRepos.add(WeatherRepo(title = "$prefixDate $suffixDate", imageUrl = "$baseURL$image"))
                    }

                    this.getDefaultSharedPreferences().put(PreferenceKeys.KEY_F_WEATHER_JSOUP_LIST, Gson().toJson(weatherRepos))
                    this.getDefaultSharedPreferences().put(PreferenceKeys.KEY_F_WEATHER_JSOUP_SAVED_YEAR_MONTH_DAY, currentDate.toString("yyyyMMdd"))

                } catch (e: Exception) {
                    longToast("네트워크 불안정으로 데이터를 받아올 수 없습니다.")
                }
            }
        } else {
            DLog.w("이미 데이터가 있어서 패스됨")
        }
    }
}