package com.media.dmitry68.callrecorder

interface MVPPresenter {

    fun setUp()

    fun setSwitchCompatState(state: Boolean)

    fun switchCompatChange(modeService: Boolean)

    fun onCheckPermission(checkPermission: Boolean)

    fun onChangeModeOfWork(newModeOfWork: String)
}