package com.media.dmitry68.callrecorder.service

sealed class ModeOfWork {
    object Background : ModeOfWork()
    object OnDemandShake : ModeOfWork()
    object OnDemandButton : ModeOfWork()
}