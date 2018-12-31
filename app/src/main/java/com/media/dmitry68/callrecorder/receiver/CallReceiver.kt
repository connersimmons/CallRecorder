package com.media.dmitry68.callrecorder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.recorder.Recorder
import com.media.dmitry68.callrecorder.service.ServiceOnDemandManager
import com.media.dmitry68.callrecorder.stateCall.CallStates
import com.media.dmitry68.callrecorder.stateCall.Caller
import com.media.dmitry68.callrecorder.stateCall.DirectionCallState
import com.media.dmitry68.callrecorder.stateCall.TalkStates
import java.util.*

class CallReceiver : BroadcastReceiver(){
    private val TAG = "LOG"
    private val incomingNumber = TelephonyManager.EXTRA_INCOMING_NUMBER
    private lateinit var receiverContext: Context
    private lateinit var recorder: Recorder
    private lateinit var managerPref: ManagerPref

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "On receive")
        if (intent?.action == IntentActions.PHONE_STAGE_CHANGED) {
            if (intent.hasExtra(incomingNumber)) {
                caller.number = intent.getStringExtra(incomingNumber)
            }
            receiverContext = context
            val telephonyManager = receiverContext.getSystemService(Context.TELEPHONY_SERVICE)
            if (telephonyManager is TelephonyManager)
                caller.statePhone = telephonyManager.callState
            Log.d(TAG, "On Receive ${caller.number} ${caller.statePhone} $lastState")
            managerPref = ManagerPref(receiverContext)
            when (managerPref.getModeOfWorkInSharedPref()){
                managerPref.getPrefModeOfWorkDefault() -> {
                    onCallStateChanged(caller.statePhone, this::initRecord, this::stopRecord)
                }
                managerPref.getPrefModeOfWorkOnDemand() -> {
                   onCallStateChanged(caller.statePhone, this::messageOnDemandManagerOnCallStateChanged)
                }
            }
        }
    }

    //INCOMING - IDLE -> RINGING -> OFFHOOK -> IDLE
    //OUTGOING - IDLE -> OFFHOOK -> IDLE
    //MISSING - IDLE -> RINGING -> IDLE
    private inline fun onCallStateChanged(statePhone: Int, onCallStateOffHook: () -> Unit, onCallStop: () -> Unit = {}) {
        if (lastState == statePhone)
            return //no change
        when(statePhone){
            CallStates.IDLE -> {
                when(lastState) {
                    CallStates.RINGING -> {
                        caller.directCallState = DirectionCallState.MISSING
                        Log.d(TAG, "missing incoming call")
                    }
                    CallStates.OFFHOOK -> {
                        when(caller.directCallState){
                            DirectionCallState.INCOMING -> { //TODO: Collapse two cases
                                with(caller){
                                    talkState = TalkStates.STOP
                                    stopTalk = Date()
                                }
                                onCallStop()
                                Log.d(TAG, "stop incoming call")
                            }
                            DirectionCallState.OUTGOING -> {
                                with(caller){
                                    talkState = TalkStates.STOP
                                    stopTalk = Date()
                                }
                                onCallStop()
                                Log.d(TAG, "stop outgoing call")
                            }
                        }
                    }
                }
            }
            CallStates.OFFHOOK -> {
                when(lastState) {
                    CallStates.RINGING -> {
                        with(caller){
                            directCallState = DirectionCallState.INCOMING
                            talkState = TalkStates.ANSWER
                            startTalk = Date()
                        }
                        onCallStateOffHook()
                        Log.d(TAG, "offHook incoming call ${caller.number}")
                    }
                    CallStates.IDLE -> {
                        with(caller) {
                            directCallState = DirectionCallState.OUTGOING
                            talkState = TalkStates.START
                            startTalk = Date()
                        }
                        onCallStateOffHook()
                        Log.d(TAG, "offHook outgoing call ${caller.number}")
                    }
                }
            }
            CallStates.RINGING -> {
                with(caller) {
                    directCallState = DirectionCallState.INCOMING
                    talkState = TalkStates.IDLE
                }
                Log.d(TAG, "ringing incoming call ${caller.number}")
            }
        }
        lastState = statePhone
    }

    private fun initRecord() {
        recorder = Recorder(caller, receiverContext).apply { startRecord() }
    }

    private fun stopRecord() = recorder.stopRecord()

    private fun messageOnDemandManagerOnCallStateChanged(){
        val intent = Intent(ServiceOnDemandManager.ON_CALL_STATE_CHANGED).apply {
            putExtra(CALL_NUMBER, caller.number)
            putExtra(DIRECT_CALL, caller.directCallState)
        }
        receiverContext.sendBroadcast(intent)
    }

    companion object {
        private var lastState: Int = CallStates.IDLE
        private val caller: Caller = Caller()

        const val CALL_NUMBER = "CALL_NUMBER"
        const val DIRECT_CALL = "DIRECT_CALL"
    }
}