package com.media.dmitry68.callrecorder.preferences

import android.content.Context
import android.support.v7.preference.PreferenceManager
import com.media.dmitry68.callrecorder.R

class ManagerPref(private val context : Context){
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun getFileName() = sharedPref.getString(KEY_PREF_FILE_NAME, context.getString(R.string.pref_file_name))!!

    fun getFlagShowDirectionCall() = sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_DIRECTION_CALL, true)

    fun getFlagShowNumber() = sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_NUMBER, true)

    fun getAudioSource() = sharedPref.getString(KEY_PREF_AUDIO_SOURCE, context.getString(R.string.pref_audio_source_voice_communication))!!

    fun getStateService() = sharedPref.getBoolean(KEY_PREF_SERVICE_STATUS, false)

    fun setStateService(state: Boolean){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context).edit()
        sharedPref.putBoolean(KEY_PREF_SERVICE_STATUS, state)
        sharedPref.apply()
    }

    companion object {
        const val KEY_PREF_FILE_NAME = "pref_file_name"
        const val KEY_PREF_FLAG_SHOW_DIRECTION_CALL = "pref_flag_show_direction_call"
        const val KEY_PREF_FLAG_SHOW_NUMBER = "pref_flag_show_number"
        const val KEY_PREF_AUDIO_SOURCE = "pref_audio_source"
        const val KEY_PREF_SERVICE_STATUS = "pref_service_status"
    }
}