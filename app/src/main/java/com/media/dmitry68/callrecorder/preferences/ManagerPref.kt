package com.media.dmitry68.callrecorder.preferences

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.media.dmitry68.callrecorder.R
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class ManagerPref(private val context : Context) : PreferenceChangeAware(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    private val observer = {
            property: KProperty<*>,
            oldValue: Any,
            newValue: Any -> propertyChangeSupport.firePropertyChange(property.name, oldValue, newValue)
    }
    private val TAG = "LOG"
    init {
        Log.d(TAG, "ManagerPref register on SharedPreferenceChangeListener")
        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    var propertyModeOfWork: String by Delegates.observable(getModeOfWorkInSharedPref(), observer)

    fun getFileName() = sharedPref.getString(KEY_PREF_FILE_NAME, context.getString(R.string.pref_file_name))!!

    fun getFlagShowDirectionCall() = sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_DIRECTION_CALL, true)

    fun getFlagShowNumber() = sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_NUMBER, true)

    fun getAudioSource() = sharedPref.getString(KEY_PREF_AUDIO_SOURCE, getPrefAudioSourceVoiceCommunication())!!

    fun getStateService() = sharedPref.getBoolean(KEY_PREF_SERVICE_STATUS, false)

    fun setStateService(state: Boolean){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context).edit()
        sharedPref.putBoolean(KEY_PREF_SERVICE_STATUS, state)
        sharedPref.apply()
    }

    fun getPrefModeOfWorkDefault(): String = context.getString(R.string.pref_mode_of_work_default)

    fun getPrefModeOfWorkOnDemand(): String = context.getString(R.string.pref_mode_of_work_on_demand)

    fun getPrefAudioSourceVoiceCommunication(): String = context.getString(R.string.pref_audio_source_voice_communication)

    fun getPrefAudioSourceMic(): String = context.getString(R.string.pref_audio_source_mic)

    fun getPrefAudioSourceVoiceCall(): String = context.getString(R.string.pref_audio_source_voice_call)

    fun getPrefAudioSourceDefault(): String = context.getString(R.string.pref_audio_source_default)

    private fun getModeOfWorkInSharedPref() = sharedPref.getString(KEY_PREF_MODE_OF_WORK, getPrefModeOfWorkDefault())!!

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "Shared Preference change : $key")
        if (key.equals(KEY_PREF_MODE_OF_WORK)){
            propertyModeOfWork = getModeOfWorkInSharedPref()
        }
    }

    companion object {
        const val KEY_PREF_FILE_NAME = "pref_file_name"
        const val KEY_PREF_FLAG_SHOW_DIRECTION_CALL = "pref_flag_show_direction_call"
        const val KEY_PREF_FLAG_SHOW_NUMBER = "pref_flag_show_number"
        const val KEY_PREF_AUDIO_SOURCE = "pref_audio_source"
        const val KEY_PREF_SERVICE_STATUS = "pref_service_status"
        const val KEY_PREF_MODE_OF_WORK = "pref_mode_of_work"
    }
}