package com.media.dmitry68.callrecorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.media.dmitry68.callrecorder.permissions.PermissionManager
import com.media.dmitry68.callrecorder.preferences.SettingsActivity
import com.media.dmitry68.callrecorder.service.CallService


class MainActivity : AppCompatActivity() {
    private val TAG = "LOG"
    private val permissionManager = PermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Create MainActivity")
        if (!permissionManager.checkPermission())
            permissionManager.requestPermission()
        else
            startCallService()
    }

    override fun onResume() {
        super.onResume() //TODO: requestPermission on preference change
        Log.d(TAG, "Resume MainActivity")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Pause MainActivity")
    }

    private fun startCallService() {
        val intent = Intent().apply { setClass(applicationContext, CallService::class.java) }
        ContextCompat.startForegroundService(applicationContext, intent)
    }

    private fun stopCallService() {
        val intent = Intent().apply { setClass(applicationContext, CallService::class.java) }
        stopService(intent)
    }

    //TODO: move requestpermission result to permissionManager
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            PermissionManager.REQUEST_CODE_ASK_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                with(perms){
                    put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED)
                    put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)
                    put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED)
                    if (permissionManager.flagPermissionAudioSettings)
                        put(Manifest.permission.MODIFY_AUDIO_SETTINGS, PackageManager.PERMISSION_GRANTED)
                }
                for (i in 0 until permissions.size)
                    perms[permissions[i]] = grantResults[i]
                if (perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.RECORD_AUDIO] == PackageManager.PERMISSION_GRANTED) {
                    if (permissionManager.flagPermissionAudioSettings) {
                        if (perms[Manifest.permission.MODIFY_AUDIO_SETTINGS] == PackageManager.PERMISSION_GRANTED)
                            startCallService()
                    } else
                        startCallService()
                } else {
                    Toast.makeText(this, R.string.message_problem_with_permission, Toast.LENGTH_LONG)
                        .show()
                    stopCallService()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, REQUEST_SETTINGS)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val REQUEST_SETTINGS = 100
    }

}
