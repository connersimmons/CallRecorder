package com.media.dmitry68.callrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.MVPPresenter

class ServiceManager(private val context: Context){

    var presenter: MVPPresenter? = null
    lateinit var modeOfWork: ModeOfWork
    private val innerReceiverOnRestartService = ReceiverOnRestartService()
    private val TAG = "LOG"

    fun startCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                //TODO: start service on boot device
                Log.d(TAG, "Service manager: START_FOREGROUND_AUTO_CALL_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_AUTO_CALL_RECORD_ACTION)
            }
            is ModeOfWork.OnDemandShake -> {
                Log.d(TAG, "Service manager: START_FOREGROUND_ON_DEMAND_SHAKE_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_ON_DEMAND_SHAKE_RECORD_ACTION)
            }
            is ModeOfWork.OnDemandButton -> {
                Log.d(TAG, "Service manager: START_FOREGROUND_ON_DEMAND_BUTTON_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_ON_DEMAND_BUTTON_RECORD_ACTION)
            }
        }
    }

    fun stopCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                Log.d(TAG, "Service manager: STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION)
            }
            is ModeOfWork.OnDemandShake -> {
                Log.d(TAG, "Service manager: STOP_FOREGROUND_ON_DEMAND_SHAKE_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_ON_DEMAND_SHAKE_RECORD_ACTION)
            }
            is ModeOfWork.OnDemandButton -> {
                Log.d(TAG, "Service manager: STOP_FOREGROUND_ON_DEMAND_BUTTON_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_ON_DEMAND_BUTTON_RECORD_ACTION)
            }
        }
    }

    fun registerReceiverForRestartService(){
        val intentFilterOnStopCallService = IntentFilter(CallForegroundService.STOP_FOREGROUND_SERVICE)
        LocalBroadcastManager.getInstance(context).registerReceiver(innerReceiverOnRestartService, intentFilterOnStopCallService)
    }

    //TODO: test service on xiaomi device;
    // TODO: stopwatch in background mode; notification on new file; add feature auto stop on call stop on demand mode
    //TODO: feature on demand mode init with only call

    private fun manageForegroundCallService(actionStopOrStart: String){
        val intent = Intent().apply {
            setClass(context, CallForegroundService::class.java)
            action = actionStopOrStart
        }
        ContextCompat.startForegroundService(context, intent)
    }

    inner class ReceiverOnRestartService: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            presenter?.onStopServiceForHisRestart()
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(innerReceiverOnRestartService)
        }
    }
}