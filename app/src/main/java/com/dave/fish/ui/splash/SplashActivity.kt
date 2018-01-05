package com.dave.fish.ui.splash

import android.content.Intent
import com.dave.fish.api.ApiProvider
import com.dave.fish.api.Network
import com.dave.fish.api.NetworkCallback
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.api.model.GisModel
import com.dave.fish.ui.BaseActivity
import com.dave.fish.ui.main.MainActivity
import com.dave.fish.util.DLog

/**
 * Created by soul on 2017. 11. 10..
 */
class SplashActivity : BaseActivity(){

    private lateinit var mRealmController : RealmController

    override fun getContentId(): Int = 0

    override fun initViews() {
        initRealm()
    }

    override fun initData() {
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

    private fun isEmptyRealmSpinner(): Boolean = mRealmController.getSpinnerItems(realm).isEmpty()

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

                        mRealmController.setSpinner(realm, gisMap)
                    }

                    error = {
                        DLog.e("result API response ===> error ${it.localizedMessage}")
                    }
                })
    }

    private fun initSelectSpinner(){
        mRealmController
                .initSelectedSpinnerItem(
                        realm,
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
        private val TAG = SplashActivity::class.java.simpleName
        private val FIRST_SPINNER_AREA = "충청남도"
        private val SECOND_SPINNER_AREA = "안흥"
        private val FIRST_SPINNER_POSITION = 10
        private val SECOND_SPINNER_POSITION = 3
    }
}