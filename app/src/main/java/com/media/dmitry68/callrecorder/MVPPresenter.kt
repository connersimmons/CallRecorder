package com.media.dmitry68.callrecorder

import com.media.dmitry68.callrecorder.service.ModeOfWork

interface MVPPresenter {

    fun setUp()

    fun setSwitchCompatState(state: Boolean)

    fun switchCompatChange(modeService: Boolean)

    fun onCheckPermission(checkPermission: Boolean)

    fun onChangeModeOfWork(newModeOfWork: ModeOfWork)

    fun onStopServiceForHisRestart()

    fun onStartPreferenceFragment()
}