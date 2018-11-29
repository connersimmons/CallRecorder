package com.media.dmitry68.callrecorder.preferences

import android.content.Context
import android.support.v7.preference.PreferenceManager
import com.media.dmitry68.callrecorder.R

class ManagerPref(private val context : Context){
    fun getFileName() : String{
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getString(KEY_PREF_FILE_NAME, context.getString(R.string.pref_file_name))!!
    }

    fun getFlagShowDirectionCall() : Boolean{
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_DIRECTION_CALL, true)
    }

    fun getFlagShowNumber() : Boolean{
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_NUMBER, true)
    }

    companion object {
        const val KEY_PREF_FILE_NAME = "pref_file_name"
        const val KEY_PREF_FLAG_SHOW_DIRECTION_CALL = "pref_flag_show_direction_call"
        const val KEY_PREF_FLAG_SHOW_NUMBER = "pref_flag_show_number"
    }
}