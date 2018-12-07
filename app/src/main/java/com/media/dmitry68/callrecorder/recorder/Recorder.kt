package com.media.dmitry68.callrecorder.recorder

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.MediaRecorder
import android.util.Log
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.stateCall.Caller
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException

class Recorder(
    private val caller: Caller,
    private val context: Context
)
{
    private val managerPref = ManagerPref(context)
    private val dirName = ConstantsForRecorder.dirName
    private val dirPath = ConstantsForRecorder.dirPath
    private val audioEncoder = ConstantsForRecorder.audioEncoder
    private val outputFormat = ConstantsForRecorder.outputFormat

    companion object {
        private var recorder: MediaRecorder? = null
        private var flagStarted = false
    }
    private var audioFile: File? = null
    private val TAG = "LOG"
    private var flagVoiceCall = false

    fun startRecord(){
        if (flagStarted){
            stopRecord()
            audioFile?.delete()
            Log.d(TAG, "RuntimeException: stop() is called immediately after start()")
        } else {
            createAudioFile()
            prepareRecorder()
            Thread.sleep(2000)
            try {
                recorder!!.start()
            } catch (e: Exception){
                e.printStackTrace()
                audioFile?.delete()
                return
            }
            flagStarted = true
            Log.d(TAG, "Record start")
        }
    }

    fun stopRecord(){
        try{
            if (recorder != null && flagStarted){
                releaseRecorder()
                flagStarted = false
                Log.d(TAG, "Record stop")
            }
        } catch (e: Exception){
            releaseRecorder()
            e.printStackTrace()
        }
    }

    private fun createAudioFile() : Boolean {
        try {
            val audioDir = File("$dirPath/$dirName/${caller.formatStartTalkForDir()}")
            if (!audioDir.exists())
                audioDir.mkdirs()

            val flagShowDirection = managerPref.getFlagShowDirectionCall()
            val flagShowNumber = managerPref.getFlagShowNumber()
            val suffix = ".amr"

            val fileNameBuilder = StringBuilder().apply {
                append(managerPref.getFileName())
                append("_")
                if (flagShowNumber && caller.number != null) {
                    append(caller.number)
                    append("_")
                }
                if (flagShowDirection) {
                    append(caller.directCallState)
                    append("_")
                }
                append(caller.formatStartTalkForFile())
                append(suffix)
            }.toString()
            audioFile = File(audioDir, fileNameBuilder)
            audioFile!!.createNewFile()

            return true
        }catch (e: Exception){
            Log.d(TAG, "unknown exception on prepare file")

            return false
        }
    }

    private fun prepareRecorder(): Boolean {
        try {
            recorder = MediaRecorder()
            val stringAudioSource = managerPref.getAudioSource()
            val audioSource = resolveAudioSource(stringAudioSource)
            if (audioSource == MediaRecorder.AudioSource.MIC){
                val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
                audioManager.mode = AudioManager.MODE_IN_CALL
                audioManager.isSpeakerphoneOn = true
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0)
            }
            recorder?.apply {
                setAudioSource(audioSource)
                setOutputFormat(outputFormat)
                setAudioEncoder(audioEncoder)
                setOutputFile(audioFile!!.absolutePath)
                setOnErrorListener { _, what, _ -> Log.d(TAG, "error while recording: $what") }
            }
            try {
                recorder?.prepare()
            } catch (e: IllegalStateException) {
                Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.message)
                releaseRecorder()
                return false
            } catch (e: IOException) {
                Log.d(TAG, "IOException preparing MediaRecorder: " + e.message)
                releaseRecorder()
                return false
            }
            return true
        } catch (e: Exception){
            Log.d(TAG, "unknown exception on prepare recorder")
            return false
        }
    }

    private fun resolveAudioSource(stringAudioSource: String): Int {
        when (stringAudioSource) {
            context.getString(com.media.dmitry68.callrecorder.R.string.pref_audio_source_voice_communication) -> {
                flagVoiceCall = false
                return MediaRecorder.AudioSource.VOICE_COMMUNICATION
            }
            context.getString(com.media.dmitry68.callrecorder.R.string.pref_audio_source_mic) -> {
                flagVoiceCall = false
                return MediaRecorder.AudioSource.MIC
            }
            context.getString(com.media.dmitry68.callrecorder.R.string.pref_audio_source_voice_call) -> {
                flagVoiceCall = true
                return MediaRecorder.AudioSource.VOICE_CALL
            }
            context.getString(com.media.dmitry68.callrecorder.R.string.pref_audio_source_default) -> {
                flagVoiceCall = true
                return MediaRecorder.AudioSource.DEFAULT
            }
            else -> {
                Log.d(TAG, "unresolved audioSource: $stringAudioSource")
                return MediaRecorder.AudioSource.VOICE_COMMUNICATION
            }
        }
    }

    private fun releaseRecorder() {
        recorder?.apply {
            reset()
            release()
        }
        recorder = null
    }


}