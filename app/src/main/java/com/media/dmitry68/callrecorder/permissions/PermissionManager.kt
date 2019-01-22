package com.media.dmitry68.callrecorder.permissions

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.media.dmitry68.callrecorder.R


class PermissionManager(private val activity: AppCompatActivity) {
    private val permissionsNeeded = ArrayList<String>()
    private val permissionsList = ArrayList<String>()

    fun checkPermission() : Boolean {
        if (!addPermission(Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add(
                activity.getString(com.media.dmitry68.callrecorder.R.string.message_permission_phone_state)
            )
        if (!addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add(
                activity.getString(com.media.dmitry68.callrecorder.R.string.message_permission_write_external_storage)
            )
        if (!addPermission(Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add(
                activity.getString(com.media.dmitry68.callrecorder.R.string.message_permission_record_audio)
            )
        return permissionsList.size == 0
    }

    fun requestPermission(){
        if (permissionsNeeded.size > 0){
            val message = StringBuilder().apply {
                append(activity.getString(com.media.dmitry68.callrecorder.R.string.start_message_permission))
                append(" ")
                append(permissionsNeeded[0])
            }
            for (i in 1 until permissionsNeeded.size) {
                message.append(", ")
                message.append(permissionsNeeded[i])
            }
            showMessage(message,
                DialogInterface
                    .OnClickListener { _, _ -> ActivityCompat.requestPermissions(activity,
                        permissionsList.toArray(arrayOfNulls<String>(permissionsList.size)),
                        REQUEST_CODE_ASK_PERMISSIONS) })
            return
        }
        ActivityCompat.requestPermissions(activity, permissionsList
            .toArray(arrayOfNulls<String>(permissionsList.size)),
            REQUEST_CODE_ASK_PERMISSIONS)
    }


    private fun showMessage(message: StringBuilder, onClickListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(activity).
                setMessage(message).
                setPositiveButton(activity.getString(R.string.permission_positive_button), onClickListener).
                setNegativeButton(activity.getString(R.string.permission_negative_button), null).
                create().
                show()
    }

    private fun addPermission(permission: String) : Boolean{
        if(ContextCompat.checkSelfPermission(activity.applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))//TODO: think about !
                return false
        }
        return true
    }

    companion object {
        const val REQUEST_CODE_ASK_PERMISSIONS = 100
    }
}