package com.media.dmitry68.callrecorder.vibrator

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

class VibrateManager(private val context: Context) {
    private lateinit var vibrator: Vibrator
    private val TAG = "LOG"

    fun vibrate(mills: Long){
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()){
            Log.d(TAG, "Start vibrator")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val effect = VibrationEffect.createOneShot(mills, 10)
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(mills)
            }
        }
    }
}