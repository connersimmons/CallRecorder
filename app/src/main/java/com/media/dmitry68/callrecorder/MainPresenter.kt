package com.media.dmitry68.callrecorder

import android.util.Log
import com.media.dmitry68.callrecorder.permissions.PermissionManager
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.service.ModeOfWork
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
        serviceManager.setModeOfWork(managerPref.getModeOfWork())
        Log.d(TAG, "presenter: Setup in initialState: $initialState in mode of work: ${managerPref.getModeOfWork()}")
        if (!permissionManager.checkPermission())
            permissionManager.requestPermission()
        else {
            onCheckPermission(true)
            setSwitchCompatState(initialState)
        }
    }

    override fun switchCompatChange(modeService: Boolean) {
        Log.d(TAG, "presenter: switchChange to $modeService")
        if (modeService) {
            serviceManager.startCallService()
            managerPref.setStateService(true)
        } else {
            serviceManager.stopCallService()
            managerPref.setStateService(false)
        }
    }

    override fun onCheckPermission(checkPermission: Boolean) {
       mvpView.showSwitchMode(checkPermission)
    }
    override fun setSwitchCompatState(state: Boolean){
        mvpView.setSwitchMode(state)
    }
}