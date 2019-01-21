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
    private val model = MVPModel()

    override fun setUp() {
        val initialState = managerPref.getStateService()
        model.stateOfService = initialState
        serviceManager.presenter = this
        managerPref.presenter = this
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
            managerPref.setStateService(modeService)//TODO: test this state in pref
            model.stateOfService = modeService
        }
    }

    override fun onChangeModeOfWork(newModeOfWork: ModeOfWork) {
        Log.d(TAG, "Presenter: onChangeModeOfWork stateOfService ${model.stateOfService} newModeOfWork $newModeOfWork")
        if (model.stateOfService){
            initRestartService()
        }
        serviceManager.modeOfWork = newModeOfWork
    }

    override fun onStopServiceForHisRestart(){
        setSwitchCompatState(true)
    }

    override fun onStartPreferenceFragment() {
        managerPref.registerListenerOnSharedPref()
    }

    private fun initialSetModeOfWork(){
        val initialModeOfWork = managerPref.getModeOfWorkInSharedPref()
        serviceManager.modeOfWork = initialModeOfWork
        Log.d(TAG, "presenter: Setup in initialState: ${model.stateOfService} in mode of work: $initialModeOfWork")
    }

    private fun initRestartService(){
        serviceManager.registerReceiverForRestartService()
        setSwitchCompatState(false)
    }
}