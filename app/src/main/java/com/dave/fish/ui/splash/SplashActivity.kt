package com.dave.fish.ui.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dave.fish.api.ApiProvider
import com.dave.fish.api.Network
import com.dave.fish.api.NetworkCallback
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.api.model.GisModel
import com.dave.fish.ui.main.MainActivity
import com.dave.fish.util.DLog

/**
 * Created by soul on 2017. 11. 10..
 */
class SplashActivity : AppCompatActivity(){

    private lateinit var mRealmController : RealmController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRealm()
        initSpinnerData()
    }

    private fun initRealm() {
        mRealmController = RealmController.instance
        mRealmController.setListener(realmListener)
    }

    private fun initSpinnerData(){
        if(isEmptyRealmSpinner()){
            initSelectSpinner()
            initDataSpinner()
            return
        }

        moveMain()
    }

    private fun isEmptyRealmSpinner(): Boolean = mRealmController.getSpinnerItems().isEmpty()

    private fun initDataSpinner() {
        Network.request(ApiProvider.provideTideApi().getGisData(),
                NetworkCallback<GisModel>().apply {
                    success = { gisModel ->
                        val dataList = gisModel.data
                        val gisMap = dataList.let {
                            dataList.groupBy {
                                it.doNm
                            }
                        }.filter {
                            it.key.isNotEmpty()
                        }

                        mRealmController.setSpinner(gisMap)
                    }

                    error = {
                        DLog.e("result API response ===> error ${it.localizedMessage}")
                    }
                })
    }

    private fun initSelectSpinner(){
        mRealmController
                .initSelectedSpinnerItem(
                        FIRST_SPINNER_AREA,
                        SECOND_SPINNER_AREA,
                        FIRST_SPINNER_POSITION,
                        SECOND_SPINNER_POSITION
                )
    }

    private val realmListener = object : RealmListener{
        override fun onTransactionSuccess(listener: () -> Unit) {

        }

        override fun onSpinnerSuccess() {
            moveMain()
        }
    }
    private fun moveMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private val FIRST_SPINNER_AREA = "충청남도"
        private val SECOND_SPINNER_AREA = "안흥"
        private val FIRST_SPINNER_POSITION = 10
        private val SECOND_SPINNER_POSITION = 3
    }
}