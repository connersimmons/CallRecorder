package com.media.dmitry68.callrecorder

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.receiver.IntentActions
import java.lang.RuntimeException


class MainActivity : AppCompatActivity() {
    val MY_PERMISSION_READ_PHONE_STATE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!checkPermission())
            requestPermission()
        else
            startCallReceiver()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode){
            MY_PERMISSION_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startCallReceiver()
                }
                return
            }
            else -> {
                throw RuntimeException("unhandled request permission result: $requestCode")
            }
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
            val message = R.string.message_rationale_permission_phone_state
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show() //TODO: redesign this toast to Snackbar
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),
                MY_PERMISSION_READ_PHONE_STATE)
        }
    }

    private fun checkPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED

    private fun startCallReceiver() {
        val callReceiver = CallReceiver()
        val intentFilterPhoneStateChange = IntentFilter(IntentActions.PHONE_STAGE_CHANGED)

        registerReceiver(callReceiver, intentFilterPhoneStateChange)
    }
}
