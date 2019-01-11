package com.media.dmitry68.callrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotifyManager
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.recorder.Recorder
import com.media.dmitry68.callrecorder.stateCall.Caller
import com.media.dmitry68.callrecorder.stopwatch.StopwatchManager
import com.media.dmitry68.callrecorder.vibrator.VibrateManager

class ServiceOnDemandManager(private val appContext: Context,
                             private val notificationManager: NotifyManager) {
    val vibrateManager = VibrateManager(appContext)
    private val innerReceiverForStopRecorder = ReceiverOfManageRecorder()
    private val localBroadcastManager = LocalBroadcastManager.getInstance(appContext)
    private val prefManager = ManagerPref(appContext)
    private val caller = Caller()
    private var flagCall = false
    private lateinit var recorder: Recorder
    private lateinit var stopwatchManager: StopwatchManager
    private val TAG = "LOG"

    fun startRecordOnShakeDetector(){
        Log.d(TAG, "Start record on Shake Detector")
        vibrateManager.vibrate(1000L)
        initRecord()
        appContext.registerReceiver(innerReceiverForStopRecorder, IntentFilter(STOP_RECORD_ACTION_ON_SHAKE_DETECTOR))
        localBroadcastManager.registerReceiver(innerReceiverForStopRecorder, IntentFilter(ON_CALL_STATE_CHANGED).apply {
            addAction(CallForegroundService.STOP_FOREGROUND_SERVICE)
        })
        notificationManager.addAction(STOP_RECORD_ACTION_ON_SHAKE_DETECTOR)
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.STOP_REGISTER_SHAKE_DETECTOR))
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.START_CALL_RECEIVER))
        stopwatchManager = StopwatchManager(notificationManager)
        stopwatchManager.start()
    }

    private fun initRecord() {
        recorder = Recorder(caller, appContext).apply { startRecord() }
        prefManager.setStateRecorder(true)
    }

    private fun stopRecordOnShakeDetector(){
        Log.d(TAG, "ServiceOnDemandManager: stopRecordOnShakeDetector")
        notificationManager.removeAction()
        stopRecord()
        stopwatchManager.stop()
        appContext.unregisterReceiver(innerReceiverForStopRecorder)
        localBroadcastManager.unregisterReceiver(innerReceiverForStopRecorder)
    }

    private fun stopRecord(){
        Log.d(TAG, "Stop record on Demand Manager")
        recorder.stopRecord()
        prefManager.setStateRecorder(false)
        if (flagCall)
            recorder.addToAudioFileCallNumberAndDirection()
        flagCall = false
    }

    companion object {
        const val STOP_RECORD_ACTION_ON_SHAKE_DETECTOR = "com.media.dmitry68.callrecorder.service.STOP_RECORD_ACTION_ON_SHAKE_DETECTOR"
        const val ON_CALL_STATE_CHANGED = "com.media.dmitry68.callrecorder.service.ON_CALL_STATE_CHANGED"
    }

    inner class ReceiverOfManageRecorder : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action){
                STOP_RECORD_ACTION_ON_SHAKE_DETECTOR -> {
                    Log.d(TAG, "ServiceOnDemandManager: onReceive STOP_RECORD_ACTION_ON_SHAKE_DETECTOR")
                    stopRecordOnShakeDetector()
                    localBroadcastManager.sendBroadcast(Intent(CallForegroundService.STOP_CALL_RECEIVER))
                    localBroadcastManager.sendBroadcast(Intent(CallForegroundService.START_REGISTER_SHAKE_DETECTOR))
                    notificationManager.addText(notificationManager.contentText)
                }
                CallForegroundService.STOP_FOREGROUND_SERVICE -> {
                    Log.d(TAG, "ServiceOnDemandManager: onReceive STOP_FOREGROUND_SERVICE")
                    stopRecordOnShakeDetector()
                    notificationManager.removeNotification()
                }
                ON_CALL_STATE_CHANGED -> {
                    Log.d(TAG, "ServiceOnDemandManager: onReceive ON_CALL_STATE_CHANGED")
                    flagCall = true
                    caller.number = intent.getStringExtra(CallReceiver.CALL_NUMBER)
                    caller.directCallState = intent.getStringExtra(CallReceiver.DIRECT_CALL)
                    recorder.setSpeakerphoneInCall() //TODO: test with other audio source and make it feature in pref
                }
            }
        }
    }
}