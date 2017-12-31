package com.dave.fish.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Created by soul on 2017. 12. 31..
 */
object ApiProvider {

    private const val TIDE_URL = "http://www.khoa.go.kr/swtc/"
    private const val KMA_URL = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/"

    fun provideTideApi(): TideApi {
        return Retrofit.Builder()
                .baseUrl(TIDE_URL)
                .client(provideOkHttpClient(provideLoggingInterceptor()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(TideApi::class.java)
    }

    fun provideKmaApi(): KmaApi {
        return Retrofit.Builder()
                .baseUrl(KMA_URL)
                .client(provideOkHttpClient(provideLoggingInterceptor()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(KmaApi::class.java)
    }

    private fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val b = OkHttpClient.Builder()
        b.addInterceptor(interceptor)
        return b.build()
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}
