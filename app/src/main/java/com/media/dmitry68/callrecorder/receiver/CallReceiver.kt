package com.media.dmitry68.callrecorder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import java.util.*


class CallReceiver : BroadcastReceiver(){
    private val incomingNumber = TelephonyManager.EXTRA_INCOMING_NUMBER
    private var number: String? = null
    private var statePhone: Int? = null
    private var directCallState: Int = DirectionCallState.MISSING
    private var lastState: Int = TelephonyManager.CALL_STATE_IDLE
    private var talkState: Int = TalkStates.IDLE
    lateinit var startTalk: Date
    lateinit var stopTalk: Date

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action == IntentActions.PHONE_STAGE_CHANGED && intent.hasExtra(String)){
            val telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE)
            if (telephonyManager is TelephonyManager) {
                number = intent.getStringExtra(incomingNumber)
                statePhone = telephonyManager.callState
                onCallStateChanged(statePhone!!)
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
                        directCallState = DirectionCallState.MISSING
                    }
                    CallStates.OFFHOOK -> {
                        when(directCallState){
                            DirectionCallState.INCOMING -> { //TODO: Collapse two cases
                                talkState = TalkStates.STOP
                                stopTalk = Date()
                            }
                            DirectionCallState.OUTGOING -> {
                                talkState = TalkStates.STOP
                                stopTalk = Date()
                            }
                        }
                    }
                }
            }
            CallStates.OFFHOOK -> {
                when(lastState) {
                    CallStates.RINGING -> {
                        directCallState = DirectionCallState.INCOMING
                        talkState = TalkStates.ANSWER
                        startTalk = Date()
                    }
                    CallStates.IDLE -> {
                        directCallState = DirectionCallState.OUTGOING
                        talkState = TalkStates.START
                        startTalk = Date()
                    }
                }
            }
            CallStates.RINGING -> {
                directCallState = DirectionCallState.INCOMING
                talkState = TalkStates.IDLE
            }
        }
        lastState = statePhone
    }

}