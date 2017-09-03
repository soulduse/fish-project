package com.dave.fish_project

import com.dave.fish_project.network.RetrofitController
import com.dave.fish_project.view.activity.MainActivity
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.*

/**
 * Created by soul on 2017. 9. 3..
 */

@RunWith(MainActivity.class)
class ApiTest {

    @Test
    fun groupByTest(){
        RetrofitController()
                .getGisData()
                .subscribe({
                    response ->
                    var dataList = response.data
                    dataList?.let{
                        var dataResult = dataList.groupBy {
                            it.doNm
                        }
                    }

                    assertEquals(3, 4)
                })
    }


    companion object {
        private val TAG = ApiTest.javaClass.simpleName
    }
}