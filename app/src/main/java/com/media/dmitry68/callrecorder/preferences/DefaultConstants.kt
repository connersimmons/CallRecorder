package com.media.dmitry68.callrecorder.preferences

import android.media.MediaRecorder
import android.os.Environment

internal object DefaultConstants {
    val fileName = "Default file"
    val dirName = "CallRecorder"
    val dirPath = Environment.getExternalStorageDirectory().path
    val audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION
    val audioEncoder = MediaRecorder.AudioEncoder.AMR_NB
    val outputFormat = MediaRecorder.OutputFormat.AMR_NB
    val flagShowDirectionCall = true
    val flagShowPhoneNumber = true
}