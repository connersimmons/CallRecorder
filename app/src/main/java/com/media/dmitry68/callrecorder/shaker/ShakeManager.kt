package com.media.dmitry68.callrecorder.shaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.preferences.SettingsFragment.Companion.CHANGE_PREFERENCE_COUNT_OF_SHAKE
import com.media.dmitry68.callrecorder.preferences.SettingsFragment.Companion.CHANGE_PREFERENCE_SENSITIVITY

class ShakeManager(context: Context,
                   private val shakeDetector: ShakeDetector) {

    private val managerPref = ManagerPref(context)
    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    private val innerReceiver = ReceiverOfManageShakeDetectorPref()
    private val TAG = "LOG"

    init {
        val intentFilterForChangeShakePref = IntentFilter(CHANGE_PREFERENCE_COUNT_OF_SHAKE).apply {
            addAction(CHANGE_PREFERENCE_SENSITIVITY)
        }
        localBroadcastManager.registerReceiver(innerReceiver, intentFilterForChangeShakePref)
        changeCountOfShake(managerPref.getCountOfShake())
        changeSensitivity(managerPref.getSensitivityShake())
    }

    fun unRegisterInnerReceiver() = localBroadcastManager.unregisterReceiver(innerReceiver)

    private fun changeCountOfShake(newCountOfShake: Int){
        shakeDetector.countOfShakeForEvent = newCountOfShake
    }

    private fun changeSensitivity(newSensitivity: Int){
        val percentSensitivity = 1 - (newSensitivity.toFloat() / 100)
        val thresholdGravity = 2 + (2 * percentSensitivity)
        Log.d(TAG, "ShakeManager: new ThresholdGravity: $thresholdGravity")
        shakeDetector.shakeThresholdGravity = thresholdGravity
    }

    inner class ReceiverOfManageShakeDetectorPref : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                CHANGE_PREFERENCE_COUNT_OF_SHAKE -> {
                    Log.d(TAG, "ShakeManager: CHANGE_PREFERENCE_COUNT_OF_SHAKE")
                    changeCountOfShake(managerPref.getCountOfShake())
                }
                CHANGE_PREFERENCE_SENSITIVITY -> {
                    Log.d(TAG, "ShakeManager: CHANGE_PREFERENCE_SENSITIVITY")
                    changeSensitivity(managerPref.getSensitivityShake())
                }
            }
        }
    }
}