package com.media.dmitry68.callrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotifyManager
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.recorder.Recorder
import com.media.dmitry68.callrecorder.stateCall.Caller
import com.media.dmitry68.callrecorder.stopwatch.StopwatchManager
import com.media.dmitry68.callrecorder.vibrator.VibrateManager

class ServiceOnDemandManager(private val appContext: Context,
                             private val notificationManager: NotifyManager) {
    private val innerReceiverForStopRecorder = ReceiverOfManageRecorder()
    private val localBroadcastManager = LocalBroadcastManager.getInstance(appContext)
    private val caller = Caller()
    private var flagCall = false
    private lateinit var recorder: Recorder
    private lateinit var stopwatchManager: StopwatchManager
    private lateinit var vibrateManager: VibrateManager
    private val TAG = "LOG"

    fun startRecordOnShakeDetector(){
        Log.d(TAG, "Start record on Shake Detector")
        vibrateManager = VibrateManager(appContext)
        vibrateManager.vibrate()
        initRecord(appContext)
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.STOP_REGISTER_SHAKE_DETECTOR))
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.START_CALL_RECEIVER))
        stopwatchManager = StopwatchManager(notificationManager)
        stopwatchManager.start()
    }

    private fun initRecord(context: Context) {
        recorder = Recorder(caller, context).apply { startRecord() }
        context.registerReceiver(innerReceiverForStopRecorder, IntentFilter(STOP_RECORD_ACTION_ON_SHAKE_DETECTOR))
        localBroadcastManager.registerReceiver(innerReceiverForStopRecorder, IntentFilter(ON_CALL_STATE_CHANGED).apply {
            addAction(CallForegroundService.STOP_FOREGROUND_SERVICE)
        })
        notificationManager.addAction(STOP_RECORD_ACTION_ON_SHAKE_DETECTOR)
    }

    private fun stopRecord(){
        Log.d(TAG, "Stop record on Demand Manager")
        notificationManager.removeAction()
        recorder.stopRecord()
        if (flagCall)
            recorder.addToAudioFileCallNumberAndDirection()
        flagCall = false
        stopwatchManager.stop()
        notificationManager.addText(notificationManager.contentText)
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
                    stopRecord()
                    localBroadcastManager.sendBroadcast(Intent(CallForegroundService.STOP_CALL_RECEIVER))
                    localBroadcastManager.sendBroadcast(Intent(CallForegroundService.START_REGISTER_SHAKE_DETECTOR))
                    context?.unregisterReceiver(innerReceiverForStopRecorder)
                }
                CallForegroundService.STOP_FOREGROUND_SERVICE -> {
                    Log.d(TAG, "ServiceOnDemandManager: onReceive STOP_FOREGROUND_SERVICE")
                    stopRecord()
                }
                ON_CALL_STATE_CHANGED -> {
                    flagCall = true
                    caller.number = intent.getStringExtra(CallReceiver.CALL_NUMBER)
                    caller.directCallState = intent.getStringExtra(CallReceiver.DIRECT_CALL)
                    recorder.setSpeakerphoneInCall() //TODO: test with other audio source and make it feature in pref
                }
            }
        }
    }
}