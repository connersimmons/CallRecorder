package com.media.dmitry68.callrecorder.preferences

import android.content.*
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.media.dmitry68.callrecorder.MainPresenter
import com.media.dmitry68.callrecorder.R

class ManagerPref(private val context : Context){
    var presenter: MainPresenter? = null
    private val receiverOnChangePref = ReceiverOnChangePref()
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    private val sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    private val TAG = "LOG"

    fun getFileName() = sharedPref.getString(KEY_PREF_FILE_NAME, context.getString(R.string.pref_file_name))!!

    fun getFlagShowDirectionCall() = sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_DIRECTION_CALL, true)

    fun getFlagShowNumber() = sharedPref.getBoolean(KEY_PREF_FLAG_SHOW_NUMBER, true)

    fun getAudioSource() = sharedPref.getString(KEY_PREF_AUDIO_SOURCE, getPrefAudioSourceVoiceCommunication())!!

    fun getStateService() = sharedPref.getBoolean(KEY_PREF_SERVICE_STATUS, false)

    fun getModeOfWorkInSharedPref() = sharedPref.getString(KEY_PREF_MODE_OF_WORK, getPrefModeOfWorkDefault())!!

    fun getCountOfShake() = sharedPref.getInt(KEY_PREF_COUNT_OF_SHAKE, 3)

    fun getSensitivityShake() = sharedPref.getInt(KEY_PREF_SENSITIVITY_SHAKE, 70)

    fun setStateService(state: Boolean){
        sharedPrefEditor.putBoolean(KEY_PREF_SERVICE_STATUS, state)
        sharedPrefEditor.apply()
    }

    fun setStateRecorder(state: Boolean){
        sharedPrefEditor.putBoolean(KEY_PREF_RECORDER_STATUS, state)
        sharedPrefEditor.apply()
    }

    //TODO: make manager of resource for next fun
    fun getPrefModeOfWorkDefault(): String = context.getString(R.string.pref_mode_of_work_default)

    fun getPrefModeOfWorkOnDemand(): String = context.getString(R.string.pref_mode_of_work_on_demand)

    fun getPrefAudioSourceVoiceCommunication(): String = context.getString(R.string.pref_audio_source_voice_communication)

    fun getPrefAudioSourceMic(): String = context.getString(R.string.pref_audio_source_mic)

    fun getPrefAudioSourceVoiceCall(): String = context.getString(R.string.pref_audio_source_voice_call)

    fun getPrefAudioSourceDefault(): String = context.getString(R.string.pref_audio_source_default)

    fun registerListenerOnSharedPref() {
        Log.d(TAG, "ManagerPref register on SharedPreferenceChangeReceiver")
        val intentFilterOnChangeModeOfWork = IntentFilter(SettingsFragment.CHANGE_PREFERENCE_MODE_OF_WORK).apply {
            addAction(SettingsFragment.PAUSE_PREFERENCE_FRAGMENT)
        }
        localBroadcastManager.registerReceiver(receiverOnChangePref, intentFilterOnChangeModeOfWork)
    }

    private fun unRegisterListenerOnSharedPref() {
        Log.d(TAG, "ManagerPref unregister on SharedPreferenceChangeReceiver")
        localBroadcastManager.unregisterReceiver(receiverOnChangePref)
    }

    companion object {
        const val KEY_PREF_FILE_NAME = "pref_file_name"
        const val KEY_PREF_FLAG_SHOW_DIRECTION_CALL = "pref_flag_show_direction_call"
        const val KEY_PREF_FLAG_SHOW_NUMBER = "pref_flag_show_number"
        const val KEY_PREF_AUDIO_SOURCE = "pref_audio_source"
        const val KEY_PREF_SERVICE_STATUS = "pref_service_status"
        const val KEY_PREF_RECORDER_STATUS = "pref_recorder_status"
        const val KEY_PREF_MODE_OF_WORK = "pref_mode_of_work"
        const val KEY_PREF_CATEGORY_ON_SHAKE_MODE = "pref_category_on_shake_mode"
        const val KEY_PREF_COUNT_OF_SHAKE = "pref_count_of_shake"
        const val KEY_PREF_SENSITIVITY_SHAKE = "pref_sensitivity_shake"
    }

    inner class ReceiverOnChangePref: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                SettingsFragment.PAUSE_PREFERENCE_FRAGMENT -> {
                    unRegisterListenerOnSharedPref()
                }
                SettingsFragment.CHANGE_PREFERENCE_MODE_OF_WORK -> {
                    Log.d(TAG, "ManagerPref: CHANGE_PREFERENCE_MODE_OF_WORK")
                    presenter?.onChangeModeOfWork(getModeOfWorkInSharedPref())
                }
            }
        }
    }
}