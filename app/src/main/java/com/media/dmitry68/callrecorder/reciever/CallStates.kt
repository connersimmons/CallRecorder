package com.media.dmitry68.callrecorder.reciever

import android.telephony.TelephonyManager

internal object CallStates {
    const val IDLE = TelephonyManager.CALL_STATE_IDLE
    const val OFFHOOK = TelephonyManager.CALL_STATE_OFFHOOK
    const val RINGING = TelephonyManager.CALL_STATE_RINGING
}