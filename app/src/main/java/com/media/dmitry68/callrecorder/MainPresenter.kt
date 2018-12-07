package com.media.dmitry68.callrecorder

import android.util.Log
import com.media.dmitry68.callrecorder.permissions.PermissionManager
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.service.ServiceManager

class MainPresenter(
    private val mvpView: MVPView,
    private val serviceManager: ServiceManager,
    private val permissionManager: PermissionManager,
    private val managerPref: ManagerPref
): MVPPresenter {
    private val TAG = "LOG"

    override fun setUp() {
        val initialState = managerPref.getStateService()
        Log.d(TAG, "presenter: Setup in initialState: $initialState")
        if (!permissionManager.checkPermission())
            permissionManager.requestPermission()
        else {
            onCheckPermission(true)
            switchCompatChange(initialState)
        }
    }

    override fun switchCompatChange(modeService: Boolean) {
        Log.d(TAG, "presenter: switchChange to $modeService")
        if (modeService) {
            serviceManager.startCallService()
            mvpView.setSwitchMode(true)
            managerPref.setStateService(true)
        } else {
            serviceManager.stopCallService()
            mvpView.setSwitchMode(false)
            managerPref.setStateService(false)
        }
    }

    override fun onCheckPermission(checkPermssion: Boolean) {
       mvpView.showSwitchMode(checkPermssion)
    }
}