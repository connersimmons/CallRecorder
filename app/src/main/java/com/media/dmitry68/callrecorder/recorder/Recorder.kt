package com.media.dmitry68.callrecorder.recorder

import android.media.MediaRecorder
import android.util.Log
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.stateCall.Caller
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException

class Recorder(
    private val managerPref: ManagerPref,
    private val caller: Caller
)
{
    private val dirName = ConstantsForRecorder.dirName
    private val dirPath = ConstantsForRecorder.dirPath
    private val audioSource = ConstantsForRecorder.audioSource
    private val audioEncoder = ConstantsForRecorder.audioEncoder
    private val outputFormat = ConstantsForRecorder.outputFormat

    companion object {
        private var recorder: MediaRecorder? = null
        private var flagStarted = false
    }
    private var audioFile: File? = null
    private val TAG = "LOG_Receiver"

    fun startRecord(){
        if (flagStarted){
            stopRecord()
            audioFile?.delete()
            Log.d(TAG, "RuntimeException: stop() is called immediately after start()")
        } else {
            createAudioFile()
            prepareRecorder()
            recorder!!.start()
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

    private fun createAudioFile() {
        val audioDir = File("$dirPath/$dirName/${caller.formatStartTalk()}")
        if(!audioDir.exists())
            audioDir.mkdirs()

        val flagShowDirection = managerPref.getFlagShowDirectionCall()
        val flagShowNumber = managerPref.getFlagShowNumber()
        val fileNameBuilder = StringBuilder()
        fileNameBuilder.append(managerPref.getFileName())
        fileNameBuilder.append("_")
        if(flagShowNumber && caller.number != null){
            fileNameBuilder.append(caller.number)
            fileNameBuilder.append("_")
        }
        if(flagShowDirection)
            fileNameBuilder.append(caller.directCallState)
        val fileName = fileNameBuilder.toString()

        val suffix = ".amr"

        audioFile = File.createTempFile(fileName, suffix, audioDir)
    }

    private fun prepareRecorder(): Boolean {
        recorder = MediaRecorder()
        recorder?.apply {
            setAudioSource(audioSource)
            setOutputFormat(outputFormat)
            setAudioEncoder(audioEncoder)
            setOutputFile(audioFile!!.absolutePath)
            setOnErrorListener { _, what, _ -> Log.d(TAG, "error while recording: $what")}
        }
        try {
            recorder?.prepare()
        } catch (e: IllegalStateException){
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.message)
            releaseRecorder()
            return false
        } catch (e: IOException){
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.message)
            releaseRecorder()
            return false
        }
        return true
    }

    private fun releaseRecorder() {
        recorder?.apply {
            reset()
            release()
        }
        recorder = null
    }


}