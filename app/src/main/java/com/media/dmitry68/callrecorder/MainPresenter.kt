package com.media.dmitry68.callrecorder

import android.util.Log
import com.media.dmitry68.callrecorder.permissions.PermissionManager
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.service.ServiceManager
import java.beans.PropertyChangeListener

class MainPresenter(
    private val mvpView: MVPView,
    private val serviceManager: ServiceManager,
    private val permissionManager: PermissionManager,
    private val managerPref: ManagerPref
): MVPPresenter {
    private val TAG = "LOG"
    private val model = MVPModel()


    override fun setUp() {
        val initialState = managerPref.getStateService()
        model.stateOfService = initialState
        serviceManager.presenter = this
        managerPref.registerListenerOnSharedPref()
        if (!permissionManager.checkPermission())
            permissionManager.requestPermission()
        else {
            onCheckPermission(true)
        }
    }

    override fun onCheckPermission(checkPermission: Boolean) {
        mvpView.showSwitchVisibility(checkPermission)
        if (checkPermission){
            initialSetModeOfWork()
            setSwitchCompatState(model.stateOfService)
        } else {
            setSwitchCompatState(false)
        }
    }

    override fun setSwitchCompatState(state: Boolean){
        mvpView.setSwitchMode(state)
    }

    override fun switchCompatChange(modeService: Boolean) {
        if (managerPref.getStateService() != modeService) {
            Log.d(TAG, "presenter: switchChange to $modeService")
            if (modeService) {
                serviceManager.startCallService()
            } else {
                serviceManager.stopCallService()
            }
            managerPref.setStateService(modeService)
            model.stateOfService = modeService
        }
    }

    override fun onChangeModeOfWork(newModeOfWork: String) {
        Log.d(TAG, "presenter: onChangeModeOfWork stateOfService ${model.stateOfService} newModeOfWork $newModeOfWork")
        if (model.stateOfService){
            serviceManager.registerReceiverForRestartService()
            setSwitchCompatState(false)
        }
        serviceManager.setModeOfWork(newModeOfWork)
    }

    override fun onStopServiceForHisRestart(){
        setSwitchCompatState(true)
    }

    private fun initialSetModeOfWork(){
        serviceManager.setModeOfWork(managerPref.propertyModeOfWork)
        Log.d(TAG, "presenter: Setup in initialState: ${model.stateOfService} in mode of work: ${managerPref.propertyModeOfWork}")
        managerPref.addPropertyChangeListener(PropertyChangeListener { event ->
            Log.d(TAG, "${event.propertyName} changed from ${event.oldValue} to ${event.newValue}")
            onChangeModeOfWork(event.newValue.toString())
        })
    }


}