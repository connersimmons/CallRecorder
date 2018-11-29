package com.media.dmitry68.callrecorder.stateCall

import java.text.SimpleDateFormat
import java.util.*

data class Caller(
    var number: String? = null,
    var statePhone: Int = CallStates.IDLE,
    var directCallState: String = DirectionCallState.MISSING,
    var talkState: Int = TalkStates.IDLE,
    var startTalk: Date = Date(),
    var stopTalk: Date = Date()
) {
    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun formatStartTalk() = sdf.format(startTalk)

    fun formatStopTalk() = sdf.format(stopTalk)
}