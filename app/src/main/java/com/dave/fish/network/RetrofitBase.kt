package com.dave.fish.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import java.util.concurrent.TimeUnit

/**
 * Created by soul on 2017. 8. 23..
 */
class RetrofitBase {

    private lateinit var retrofit: Retrofit
    var tideServiceApi: TideApi ?= null
    var kmaServiceApi : KmaApi ?= null

    fun initRetrofit(api:Api){
        retrofit = createRetrofit(api)
        setServiceApi(api)
    }

    private fun setServiceApi(api:Api){
        when(api){
            Api.Kma ->{
                kmaServiceApi = retrofit.create(KmaApi::class.java)
            }

            Api.Tide ->{
                tideServiceApi = retrofit.create(TideApi::class.java)
            }
        }
    }

    private fun createRetrofit(api: Api) : Retrofit{
        val url = when(api){
            Api.Kma ->{
                FORECAST_URL
            }
            else -> {
                BASE_URL
            }
        }

        return Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = Level.BASIC

        val client = OkHttpClient.Builder()
        client.retryOnConnectionFailure(true)
        client.readTimeout(60, TimeUnit.MINUTES)
        client.connectTimeout(60, TimeUnit.MINUTES)
        client.addInterceptor(logging)

        return client.build()
    }

    private object Holder {
        val INSTANCE = RetrofitBase()
    }

    enum class Api{
        Tide, Kma
    }

    companion object {
        val INSTANCE: RetrofitBase by lazy { Holder.INSTANCE }
        private val BASE_URL = "http://www.khoa.go.kr/swtc/"
        private val FORECAST_URL = "http://http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"
    }
}
