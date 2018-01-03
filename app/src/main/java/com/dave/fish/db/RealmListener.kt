package com.dave.fish.db

/**
 * Created by soul on 2017. 9. 23..
 */
interface RealmListener {
    fun onTransactionSuccess(listener:()->Unit)
    fun onSpinnerSuccess()
}
