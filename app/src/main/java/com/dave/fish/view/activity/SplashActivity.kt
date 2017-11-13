package com.dave.fish.view.activity

import android.content.Intent
import com.dave.fish.db.RealmController
import com.dave.fish.db.RealmListener
import com.dave.fish.network.RetrofitController
import com.dave.fish.util.DLog

/**
 * Created by soul on 2017. 11. 10..
 */
class SplashActivity : BaseActivity(){

    private lateinit var mRealmController : RealmController

    override fun getContentId(): Int {
        return 0
    }

    override fun onLoadStart() {
        initRealm()
    }

    override fun onLoadContent() {
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

    private fun isEmptyRealmSpinner(): Boolean {
        return mRealmController.getSpinnerItems(realm).isEmpty()
    }

    private fun initDataSpinner() {
        RetrofitController
                .instance
                .getGisData()
                .subscribe({ gisModel ->
                    val dataList = gisModel.data
                    val gisMap = dataList.let {
                        dataList.groupBy {
                            it.doNm
                        }
                    }.filter {
                        it.key.isNotEmpty()
                    }

                    DLog.d("""
                            map data -->
                            size : ${gisMap.size}
                            keys : ${gisMap.keys}
                            values : ${gisMap.values}
                            """)
                    mRealmController.setSpinner(realm, gisMap)

                }, { e ->
                    DLog.e("result API response ===> error ${e.localizedMessage}")
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
        override fun onSpinnerSuccess() {
            moveMain()
        }

        override fun onTransactionSuccess() {

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