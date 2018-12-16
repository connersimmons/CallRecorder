package com.media.dmitry68.callrecorder.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.media.dmitry68.callrecorder.preferences.ManagerPref

class ServiceManager(private val context: Context,
                     private val managerPref: ManagerPref){
    private lateinit var modeOfWork: ModeOfWork

    fun startCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                //TODO: start service on boot device
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_AUTO_CALL_RECORD_ACTION)
            }
            is ModeOfWork.OnDemand -> {
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_ON_DEMAND_RECORD_ACTION)
            }
        }
    }

    fun stopCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION)
            }
            is ModeOfWork.OnDemand -> {
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION)
            }
        }
    }

    fun setModeOfWork(stringModeOfWork: String){
        modeOfWork = when(stringModeOfWork) {
            managerPref.getPrefModeOfWorkDefault() -> {
                ModeOfWork.Background
            }
            managerPref.getPrefModeOfWorkOnDemand() -> {
                ModeOfWork.OnDemand //TODO: make nameOfFile in settings; time in notification; Mode Of Recorder in Notification; Vibrate; second mode ondemand: action start in  notification
            }
            else -> {
                ModeOfWork.Background
            }
        }
    }

    private fun manageForegroundCallService(actionStopOrStart: String){
        val intent = Intent().apply {
            setClass(context, CallForegroundService::class.java)
            action = actionStopOrStart
        }
        ContextCompat.startForegroundService(context, intent)
    }

}