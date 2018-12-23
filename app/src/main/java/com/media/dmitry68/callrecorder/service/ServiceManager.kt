package com.media.dmitry68.callrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.MVPPresenter
import com.media.dmitry68.callrecorder.preferences.ManagerPref

class ServiceManager(private val context: Context,
                     private val managerPref: ManagerPref){

    var presenter: MVPPresenter? = null
    private lateinit var modeOfWork: ModeOfWork
    private val innerReceiverOnRestartService = ReceiverOnRestartService()
    private val TAG = "LOG"

    fun startCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                //TODO: start service on boot device
                Log.d(TAG, "Service manager: START_FOREGROUND_AUTO_CALL_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_AUTO_CALL_RECORD_ACTION)
            }
            is ModeOfWork.OnDemand -> {
                Log.d(TAG, "Service manager: START_FOREGROUND_ON_DEMAND_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_ON_DEMAND_RECORD_ACTION)
            }
        }
    }

    fun stopCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                Log.d(TAG, "Service manager: STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION)
            }
            is ModeOfWork.OnDemand -> {
                Log.d(TAG, "Service manager: STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION")
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION)
            }
        }
    }

    fun registerReceiverForRestartService(){
        val intentFilterOnStopCallService = IntentFilter(CallForegroundService.STOP_FOREGROUND_SERVICE)
        LocalBroadcastManager.getInstance(context).registerReceiver(innerReceiverOnRestartService, intentFilterOnStopCallService)
    }

    fun setModeOfWork(stringModeOfWork: String){
        modeOfWork = when(stringModeOfWork) {
            managerPref.getPrefModeOfWorkDefault() -> {
                ModeOfWork.Background
            }
            managerPref.getPrefModeOfWorkOnDemand() -> {
                ModeOfWork.OnDemand //TODO: make nameOfFile in settings; Mode Of Recorder in Notification; Vibrate; second mode ondemand: action start in  notification
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

    inner class ReceiverOnRestartService: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            presenter?.onStopServiceForHisRestart()
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(innerReceiverOnRestartService)
        }
    }

}