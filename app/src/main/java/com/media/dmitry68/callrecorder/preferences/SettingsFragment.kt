package com.media.dmitry68.callrecorder.preferences

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.support.v7.preference.PreferenceFragmentCompat
import com.media.dmitry68.callrecorder.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var managerPref: ManagerPref
    private lateinit var prefCategoryOnDemandMode: Preference

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        setPreferencesFromResource(R.xml.preferences, p1)
        managerPref = ManagerPref(context!!)
        prefCategoryOnDemandMode = findPreference(ManagerPref.KEY_PREF_CATEGORY_ON_DEMAND_MODE)
        setSummary(ManagerPref.KEY_PREF_MODE_OF_WORK)
        setSummaryInt(ManagerPref.KEY_PREF_COUNT_OF_SHAKE)
        setSummary(ManagerPref.KEY_PREF_FILE_NAME)
        setSummary(ManagerPref.KEY_PREF_AUDIO_SOURCE)
        manageOfVisiblePreferenceOnScreen(managerPref.getModeOfWorkInSharedPref(),
            managerPref.getPrefModeOfWorkOnDemand(), prefCategoryOnDemandMode)
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

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        var dialogFragment: PreferenceDialogFragmentCompat? = null
        if (preference is NumberPreference){
            val key = preference.key
            dialogFragment = NumberPreferenceFragmentCompat.newInstance(key)
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(
                fragmentManager,
                "android.support.v7.preference.PreferenceDialogFragmentCompat.DIALOG"
            )
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key){
            ManagerPref.KEY_PREF_MODE_OF_WORK -> {
                setSummary(ManagerPref.KEY_PREF_MODE_OF_WORK)
                manageOfVisiblePreferenceOnScreen(managerPref.getModeOfWorkInSharedPref(),
                    managerPref.getPrefModeOfWorkOnDemand(), prefCategoryOnDemandMode)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent(CHANGE_PREFERENCE_MODE_OF_WORK))
            }
            ManagerPref.KEY_PREF_COUNT_OF_SHAKE -> {
                setSummaryInt(ManagerPref.KEY_PREF_COUNT_OF_SHAKE)
            }
            ManagerPref.KEY_PREF_FILE_NAME -> {
                setSummary(ManagerPref.KEY_PREF_FILE_NAME)
            }
            ManagerPref.KEY_PREF_AUDIO_SOURCE -> {
                setSummary(ManagerPref.KEY_PREF_AUDIO_SOURCE)
            }
        }
    }

    private fun setSummary(key: String) {
        val preference = findPreference(key)
        preference.summary = preferenceManager.sharedPreferences.getString(key, "")
    }

    private fun setSummaryInt(key: String){
        val preference = findPreference(key)
        preference.summary = preferenceManager.sharedPreferences.getInt(key, 0).toString()
    }

    private fun manageOfVisiblePreferenceOnScreen(valueInSharedPref: String, valueOnThisPrefVisible: String, preference: Preference){
        val preferenceScreen = preferenceScreen
        if (valueInSharedPref == valueOnThisPrefVisible)
            preferenceScreen.addPreference(preference)
        else
            preferenceScreen.removePreference(preference)
    }

    companion object {
        const val PAUSE_PREFERENCE_FRAGMENT = "com.media.dmitry68.callrecorder.preferences.PAUSE_PREFERENCE_FRAGMENT"
        const val CHANGE_PREFERENCE_MODE_OF_WORK = "com.media.dmitry68.callrecorder.preferences.CHANGE_PREFERENCE_MODE_OF_WORK"
    }
}