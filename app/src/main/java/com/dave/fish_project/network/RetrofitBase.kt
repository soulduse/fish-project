package com.dave.fish_project.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import java.util.concurrent.TimeUnit

/**
 * Created by soul on 2017. 8. 23..
 */
class RetrofitBase {

    var retrofit: Retrofit
    var apiService : TideApi

    init {
        retrofit = createRetrofit()
        apiService = createAPI()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()
    }

    private fun createAPI(): TideApi {
        return retrofit.create(TideApi::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = Level.BODY

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

    companion object {
        val INSTANCE: RetrofitBase by lazy { Holder.INSTANCE }
        val BASE_URL = "http://www.khoa.go.kr/swtc/"
    }
}
