package com.dave.fish.ui.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dave.fish.api.ApiProvider
import com.dave.fish.api.Network
import com.dave.fish.api.NetworkCallback
import com.dave.fish.db.RealmProvider
import com.dave.fish.db.RealmListener
import com.dave.fish.api.model.GisModel
import com.dave.fish.common.Constants
import com.dave.fish.db.model.SelectItemModel
import com.dave.fish.ui.fweather.WeatherRepo
import com.dave.fish.ui.main.MainActivity
import com.dave.fish.util.DLog
import com.dave.fish.util.PreferenceKeys
import com.dave.fish.util.getDefaultSharedPreferences
import com.dave.fish.util.put
import com.google.gson.Gson
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.longToast
import org.joda.time.DateTime
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * Created by soul on 2017. 11. 10..
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var mRealmController: RealmProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRealm()
        startParseWithJsoup()
        initSpinnerData()
    }

    private fun initRealm() {
        mRealmController = RealmProvider.instance
        mRealmController.setListener(realmListener)
    }

    private fun startParseWithJsoup() {
        val jsoupIntent = Intent(this, JsoupIntentService::class.java)
        startService(jsoupIntent)
    }

    private fun initSpinnerData() {
        if (isEmptyRealmSpinner()) {
            initDataSpinner()
            initSelectSpinner()
            return
        }

        moveMain()
    }

    private fun isEmptyRealmSpinner(): Boolean = mRealmController.getSpinner().isEmpty()

    private fun initDataSpinner() {
        Network.request(ApiProvider.provideTideApi().getGisData(),
                NetworkCallback<GisModel>().apply {
                    success = { gisModel ->
                        val gisMap = gisModel.data
                                .filter { !it.doNm.isNullOrEmpty() }
                                .filter { it.doNm != "황해남도" }
                                .filter { it.useYn == "Y" }
                                .sortedBy { it.doNm }
                                .groupBy { it.doNm }

                        DLog.w(gisMap.toString())
                        mRealmController.setInitSpinner(gisMap)
                    }

                    error = {
                        DLog.e("result API response ===> error ${it.localizedMessage}")
                    }
                })
    }

    private fun initSelectSpinner() {
        val selectItemModel = SelectItemModel().apply {
            this.keyTide = Constants.KEY_TIDE_SIDE_SPINNER
            this.doNm = FIRST_SPINNER_AREA
            this.postName = SECOND_SPINNER_AREA

        }

        // side
        mRealmController.writeData(selectItemModel)

        // main
        mRealmController
                .setSelectSpinner(
                        FIRST_SPINNER_AREA,
                        SECOND_SPINNER_AREA
                )
    }

    private val realmListener = object : RealmListener {
        override fun onTransactionSuccess(listener: () -> Unit) {

        }

        override fun onSpinnerSuccess() {
            moveMain()
        }
    }

    private fun moveMain() {
        async {
            delay(200, TimeUnit.MILLISECONDS)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val FIRST_SPINNER_AREA = "충청남도"
        private const val SECOND_SPINNER_AREA = "안흥"
    }
}