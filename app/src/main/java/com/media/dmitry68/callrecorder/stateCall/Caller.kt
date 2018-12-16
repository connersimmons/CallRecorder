package com.media.dmitry68.callrecorder.stateCall

import java.text.SimpleDateFormat
import java.util.*

data class Caller(
    var number: String = "",
    var statePhone: Int = CallStates.IDLE,
    var directCallState: String = DirectionCallState.MISSING,
    var talkState: Int = TalkStates.IDLE,
    var startTalk: Date = Date(),
    var stopTalk: Date = Date()
) {
    private val sdfForDir = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val sdfForFile = SimpleDateFormat("HH-mm-ss", Locale.getDefault())

    fun formatStartTalkForDir() = sdfForDir.format(startTalk)

    fun formatStartTalkForFile() = sdfForFile.format(startTalk)
}