package com.media.dmitry68.callrecorder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.Call
import android.telephony.TelephonyManager
import android.util.Log
import com.media.dmitry68.callrecorder.stateCall.CallStates
import com.media.dmitry68.callrecorder.stateCall.Caller
import com.media.dmitry68.callrecorder.stateCall.DirectionCallState
import com.media.dmitry68.callrecorder.stateCall.TalkStates
import java.util.*

class CallReceiver : BroadcastReceiver(){
    companion object {
        private var number: String? = null
        private var statePhone: Int = CallStates.IDLE
        private var lastState: Int = CallStates.IDLE
        private var directCallState: Int = DirectionCallState.MISSING
        private var talkState: Int = TalkStates.IDLE
        lateinit var startTalk: Date
        lateinit var stopTalk: Date

        private val caller: Caller = Caller()
    }
    private val TAG = "LOG_Receiver"
    private val incomingNumber = TelephonyManager.EXTRA_INCOMING_NUMBER

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "On Receive")
        if(intent!!.action == IntentActions.PHONE_STAGE_CHANGED && intent.hasExtra(incomingNumber)){
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE)
            if (telephonyManager is TelephonyManager) {
                number = intent.getStringExtra(incomingNumber)
                statePhone = telephonyManager.callState
                onCallStateChanged(statePhone)

            }
        }
    }

    //INCOMING - IDLE -> RINGING -> OFFHOOK -> IDLE
    //OUTGOING - IDLE -> OFFHOOK -> IDLE
    //MISSING - IDLE -> RINGING -> IDLE
    private fun onCallStateChanged(statePhone: Int) {
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
                        when(directCallState){
                            DirectionCallState.INCOMING -> { //TODO: Collapse two cases
                                with(caller){
                                    talkState = TalkStates.STOP
                                    stopTalk = Date()
                                }
                                Log.d(TAG, "stop incoming call")
                            }
                            DirectionCallState.OUTGOING -> {
                                with(caller){
                                    talkState = TalkStates.STOP
                                    stopTalk = Date()
                                }
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
                        Log.d(TAG, "offhook incoming call $number")
                    }
                    CallStates.IDLE -> {
                        with(caller) {
                            directCallState = DirectionCallState.OUTGOING
                            talkState = TalkStates.START
                            startTalk = Date()
                        }
                        Log.d(TAG, "offhook outgoing call $number")
                    }
                }
            }
            CallStates.RINGING -> {
                with(caller) {
                    directCallState = DirectionCallState.INCOMING
                    talkState = TalkStates.IDLE
                }
                Log.d(TAG, "ringing incoming call $number")
            }
        }
        lastState = statePhone
    }

}