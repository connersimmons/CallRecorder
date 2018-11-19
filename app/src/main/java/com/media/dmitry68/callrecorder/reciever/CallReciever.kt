package com.media.dmitry68.callrecorder.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager


class CallReciever : BroadcastReceiver(){
    private val incomingNumber = TelephonyManager.EXTRA_INCOMING_NUMBER
    private var number: String? = null
    private var statePhone: Int? = null
    private var directCallState: Int = DirectionCallState.MISSING
    private var lastState: Int = TelephonyManager.CALL_STATE_IDLE
    private var talkState: Int = TalkStates.IDLE

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action == IntentActions.PHONE_STAGE_CHANGED && intent.hasExtra(incomingNumber)){
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
                            }
                            DirectionCallState.OUTGOING -> {
                                talkState = TalkStates.STOP
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
                    }
                    CallStates.IDLE -> {
                        directCallState = DirectionCallState.OUTGOING
                        talkState = TalkStates.START
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