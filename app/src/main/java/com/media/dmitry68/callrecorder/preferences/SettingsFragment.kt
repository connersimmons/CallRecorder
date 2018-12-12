package com.media.dmitry68.callrecorder.preferences

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.media.dmitry68.callrecorder.R


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        setPreferencesFromResource(R.xml.preferences, p1)
        setSummary(ManagerPref.KEY_PREF_AUDIO_SOURCE)
        setSummary(ManagerPref.KEY_PREF_MODE_OF_WORK)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(ManagerPref.KEY_PREF_AUDIO_SOURCE))
            setSummary(ManagerPref.KEY_PREF_AUDIO_SOURCE)
        if (key.equals(ManagerPref.KEY_PREF_MODE_OF_WORK))
            setSummary(ManagerPref.KEY_PREF_MODE_OF_WORK)
    }

    private fun setSummary(key: String) {
        val listPreference = findPreference(key)
        listPreference.summary = preferenceManager.sharedPreferences.getString(key, "")
    }
}