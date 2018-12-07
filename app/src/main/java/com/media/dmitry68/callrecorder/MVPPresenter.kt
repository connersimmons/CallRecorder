package com.media.dmitry68.callrecorder

interface MVPPresenter {

    fun setUp()

    fun switchCompatChange(modeService: Boolean)

    fun onCheckPermission(checkPermssion: Boolean)
}