package com.media.dmitry68.callrecorder.recorder

import android.media.MediaRecorder
import android.os.Environment

internal object ConstantsForRecorder {
    val dirName = "CallRecorder"
    val dirPath = Environment.getExternalStorageDirectory().path
    val audioSource = MediaRecorder.AudioSource.VOICE_CALL //TODO: audioSource to preference
    val audioEncoder = MediaRecorder.AudioEncoder.AMR_NB
    val outputFormat = MediaRecorder.OutputFormat.AMR_NB
}