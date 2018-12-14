package com.media.dmitry68.callrecorder.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.media.dmitry68.callrecorder.R

class ServiceManager(private val context: Context){
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
            context.getString(R.string.pref_mode_of_work_default) -> {
                ModeOfWork.Background
            }
            context.getString(R.string.pref_mode_of_work_on_demand) -> {
                ModeOfWork.OnDemand
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