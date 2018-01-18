package com.dave.fish.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by soul on 2018. 1. 18..
 */
fun Context.getDefaultSharedPreferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(this)

fun SharedPreferences.clear(){
    edit().clear().apply()
}

fun SharedPreferences.put(key: String, value: Any){
    when(value){
        is String -> edit().putString(key, value).apply()
        is Int -> edit().putInt(key, value).apply()
        is Float -> edit().putFloat(key, value).apply()
        is Boolean -> edit().putBoolean(key, value).apply()
        is Long -> edit().putLong(key, value).apply()
        is Set<*> -> {
            if(value.all { it is String })
                edit().putStringSet(key, value as Set<String>).apply()
        }
    }
}

fun SharedPreferences.put(vararg pairs: Pair<String, String>){
    edit().apply{
        pairs.forEach {
            putString(it.first, it.second)
        }
    }.apply()
}

fun SharedPreferences.remove(key: String){
    edit().remove(key).apply()
}