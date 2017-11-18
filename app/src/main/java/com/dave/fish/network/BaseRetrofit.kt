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
class BaseRetrofit {

    private lateinit var retrofit: Retrofit

    // 조석정보 API
    private lateinit var tideServiceApi: TideApi
    // 날씨정보 API
    private lateinit var kmaServiceApi : KmaApi

    private var baseUrl = ""

    fun getTideRetrofit(): TideApi {
        baseUrl = TIDE_URL
        retrofit = createRetrofit()
        tideServiceApi = retrofit.create(TideApi::class.java)
        return tideServiceApi
    }

    fun getKmaRetrofit(): KmaApi {
        baseUrl = FORECAST_URL
        retrofit = createRetrofit()
        kmaServiceApi = retrofit.create(KmaApi::class.java)
        return kmaServiceApi
    }

    private fun createRetrofit() : Retrofit{
        return Retrofit.Builder()
                .baseUrl(baseUrl)
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
        val INSTANCE = BaseRetrofit()
    }

    enum class Api{
        Tide, Kma
    }

    companion object {
        val instance: BaseRetrofit by lazy { Holder.INSTANCE }
        private val TIDE_URL = "http://www.khoa.go.kr/swtc/"
        private val FORECAST_URL = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/"
    }
}
