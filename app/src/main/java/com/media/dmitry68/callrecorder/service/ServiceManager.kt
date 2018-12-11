package com.media.dmitry68.callrecorder.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.media.dmitry68.callrecorder.R

class ServiceManager(val context: Context){
    private lateinit var modeOfWork: ModeOfWork

    fun startCallService() {
        val intent = Intent().apply {//TODO: start service on boot device
            setClass(context, CallService::class.java)
            action = CallService.START_FOREGROUND_ACTION
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopCallService() {
        val intent = Intent().apply {
            setClass(context, CallService::class.java)
            action = CallService.STOP_FOREGROUND_ACTION
        }
        ContextCompat.startForegroundService(context, intent)
    }


    fun setModeOfWork(stringModeOfWork: String){
        when(stringModeOfWork) {
            context.getString(R.string.pref_mode_of_work_default) -> {
                modeOfWork = ModeOfWork.Background
            }
            context.getString(R.string.pref_mode_of_work_on_demand) -> {
                modeOfWork = ModeOfWork.OnDemand
            }
            else -> {
                modeOfWork = ModeOfWork.Background
            }
        }
    }
}