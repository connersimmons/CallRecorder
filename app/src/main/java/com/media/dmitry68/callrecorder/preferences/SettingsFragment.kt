package com.media.dmitry68.callrecorder.preferences

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.preference.PreferenceFragmentCompat
import com.media.dmitry68.callrecorder.R


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        setPreferencesFromResource(R.xml.preferences, p1)
        setSummary(ManagerPref.KEY_PREF_AUDIO_SOURCE)
        setSummary(ManagerPref.KEY_PREF_MODE_OF_WORK)
        setSummary(ManagerPref.KEY_PREF_FILE_NAME)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(PAUSE_PREFERENCE_FRAGMENT))
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key){
            ManagerPref.KEY_PREF_AUDIO_SOURCE -> {
                setSummary(ManagerPref.KEY_PREF_AUDIO_SOURCE)
            }
            ManagerPref.KEY_PREF_MODE_OF_WORK -> {
                setSummary(ManagerPref.KEY_PREF_MODE_OF_WORK)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(CHANGE_PREFERENCE_MODE_OF_WORK))
            }
            ManagerPref.KEY_PREF_FILE_NAME -> {
                setSummary(ManagerPref.KEY_PREF_FILE_NAME)
            }
        }
    }

    private fun setSummary(key: String) {
        val listPreference = findPreference(key)
        listPreference.summary = preferenceManager.sharedPreferences.getString(key, "")
    }

    companion object {
        const val PAUSE_PREFERENCE_FRAGMENT = "com.media.dmitry68.callrecorder.preferences.PAUSE_PREFERENCE_FRAGMENT"
        const val CHANGE_PREFERENCE_MODE_OF_WORK = "com.media.dmitry68.callrecorder.preferences.CHANGE_PREFERENCE_MODE_OF_WORK"
    }
}