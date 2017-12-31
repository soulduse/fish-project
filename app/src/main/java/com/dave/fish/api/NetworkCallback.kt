package com.dave.fish.api

/**
 * Created by soul on 2017. 12. 31..
 */
class NetworkCallback<T> {
    var success: ((T) -> Unit)?= null
    var error: ((Throwable)-> Unit) ?= null
}
