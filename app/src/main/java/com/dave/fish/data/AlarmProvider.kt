package com.dave.fish.data

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by soul on 2018. 1. 16..
 */
class AlarmProvider(private val context: Context) {

    val isStarted: Boolean?
        get() = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_IS_STARTED, false)

    fun updateIsStarted(isStarted: Boolean){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(KEY_IS_STARTED, isStarted)
                .apply()
    }

    val soundDuration: Int?
        get() = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(KEY_SOUND_DURATION, 0)

    fun updateSoundDuration(duration: Int){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(KEY_SOUND_DURATION, duration)
                .apply()
    }

    companion object {
        private val KEY_IS_STARTED = "is_started"
        private val KEY_SOUND_DURATION = "sound_duration"
        private val KEY_RINGTONE_SOUND = "ringtone_sound"
    }
}