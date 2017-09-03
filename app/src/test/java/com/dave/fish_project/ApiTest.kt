package com.dave.fish_project

import com.dave.fish_project.network.RetrofitController
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by soul on 2017. 9. 3..
 */
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