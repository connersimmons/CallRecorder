package com.media.dmitry68.callrecorder.preferences

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.media.dmitry68.callrecorder.R


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        setPreferencesFromResource(R.xml.preferences, p1)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(ManagerPref.KEY_PREF_AUDIO_SOURCE)){
            val audioSourcePreference = findPreference(key)
            audioSourcePreference.summary = sharedPreferences!!.getString(key, "")
        }
    }
}