package com.media.dmitry68.callrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotifyManager
import com.media.dmitry68.callrecorder.recorder.Recorder
import com.media.dmitry68.callrecorder.stateCall.Caller
import com.media.dmitry68.callrecorder.stopwatch.StopwatchManager
import com.media.dmitry68.callrecorder.vibrator.VibrateManager

class ServiceOnDemandManager(private val appContext: Context,
                             private val notificationManager: NotifyManager) {
    private val innerReceiverForStopRecorder = ReceiverOfManageRecorder()
    private val localBroadcastManager = LocalBroadcastManager.getInstance(appContext)
    private lateinit var recorder: Recorder
    private lateinit var stopwatchManager: StopwatchManager
    private lateinit var vibrateManager: VibrateManager
    private val TAG = "LOG"

    fun startRecordOnShakeDetector(){
        Log.d(TAG, "Start record on Shake Detector")
        vibrateManager = VibrateManager(appContext)
        vibrateManager.vibrate()
        initRecord(appContext)
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.STOP_REGISTER_SHAKE_DETECTOR))//TODO: add vibrate
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.START_CALL_RECEIVER))
        stopwatchManager = StopwatchManager(notificationManager)
        stopwatchManager.start()
    }

    private fun stopRecordOnShakeDetector(){
        Log.d(TAG, "Stop record on Shake Detector")
        stopRecord()
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.START_REGISTER_SHAKE_DETECTOR))
    }

    private fun initRecord(context: Context) {
        recorder = Recorder(Caller(), context).apply { startRecord() }
        context.registerReceiver(innerReceiverForStopRecorder, IntentFilter(STOP_RECORD_ACTION_ON_SHAKE_DETECTOR).apply {
            addAction(SPEAKERPHONE_ON_RECORD_ACTION)
        })
        notificationManager.addAction(STOP_RECORD_ACTION_ON_SHAKE_DETECTOR)
    }

    private fun stopRecord(){
        notificationManager.removeAction()
        recorder.stopRecord()
        stopwatchManager.stop()
        notificationManager.addText(notificationManager.contentText)
        localBroadcastManager.sendBroadcast(Intent(CallForegroundService.STOP_CALL_RECEIVER))
    }

    companion object {
        const val STOP_RECORD_ACTION_ON_SHAKE_DETECTOR = "com.media.dmitry68.callrecorder.service.STOP_RECORD_ACTION_ON_SHAKE_DETECTOR"
        const val SPEAKERPHONE_ON_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.SPEAKERPHONE_ON_RECORD_ACTION"
    }

    inner class ReceiverOfManageRecorder : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action){
                STOP_RECORD_ACTION_ON_SHAKE_DETECTOR -> {
                    stopRecordOnShakeDetector()
                    context?.unregisterReceiver(innerReceiverForStopRecorder)
                }
                SPEAKERPHONE_ON_RECORD_ACTION -> {
                    recorder.setSpeakerphoneInCall() //TODO: test with other audio source and make it feature in pref
                }
            }
        }
    }
}