package com.dave.fish.api

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.HttpException

/**
 * Created by soul on 2017. 12. 31..
 */
object Network {
    fun deafultError(t: Throwable) {
        t.printStackTrace()
    }

    fun <T> request(call: Deferred<T>, callback: NetworkCallback<T>) {
        request(call, callback.success, callback.error)
    }

    private fun <T> request(call: Deferred<T>, onSucess: ((T) -> Unit)?, onError: ((Throwable) -> Unit)?) {
        launch(UI) {
            try {
                onSucess?.let {
                    onSucess(call.await())
                }
            } catch (httpException: HttpException) {
                // a non-2XX response was received
                deafultError(httpException)
            } catch (t: Throwable) {
                // a networking or data conversion error
                onError?.let {
                    onError(t)
                }
            }
        }
    }
}
