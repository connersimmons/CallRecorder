package com.media.dmitry68.callrecorder.vibrator

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

class VibrateManager(private val context: Context) {
    private lateinit var vibrator: Vibrator
    private val TAG = "LOG"

    fun vibrate(){
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()){
            Log.d(TAG, "Start vibrator")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val vibratePatterns = longArrayOf(0L, 400L, 800L, 600L, 800L, 800L, 800L, 1000L)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
                val effect = VibrationEffect.createWaveform(vibratePatterns, amplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(1000)
            }
        }
    }
}