package com.media.dmitry68.callrecorder.stateCall

import java.util.*

data class Caller(
    var number: String? = null,
    var statePhone: Int = CallStates.IDLE,
    var directCallState: Int = DirectionCallState.MISSING,
    var talkState: Int = TalkStates.IDLE,
    var startTalk: Date = Date(),
    var stopTalk: Date = Date()
) {
}