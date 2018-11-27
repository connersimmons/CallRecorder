package com.media.dmitry68.callrecorder.recorder

import android.util.Log

class Recorder {
    companion object {
        var flagStarted = false
    }

    private val TAG
    private fun startRecord(){
        if (flagStarted){
            stopRecord()
            Log.d(TAG, "RuntimeException: stop() is called immediately after start()")
        }
    }

    private fun stopRecord(){}
}