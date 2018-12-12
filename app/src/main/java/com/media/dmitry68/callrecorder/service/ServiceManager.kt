package com.media.dmitry68.callrecorder.service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import com.media.dmitry68.callrecorder.R
import java.util.concurrent.TimeUnit

class ServiceManager(val context: Context){
    private lateinit var modeOfWork: ModeOfWork
    private var JOB_ID = 1
    private val TAG = "LOG"
    private val callJobServiceComponentName = ComponentName(context, CallJobService::class.java.name)
    private val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE)
    fun startCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                //TODO: start service on boot device
                manageForegroundCallService(CallForegroundService.START_FOREGROUND_ACTION)
            }
            is ModeOfWork.OnDemand -> {
                scheduleJobCallService()
            }
        }
    }

    fun stopCallService() {
        when(modeOfWork) {
            is ModeOfWork.Background -> {
                manageForegroundCallService(CallForegroundService.STOP_FOREGROUND_ACTION)
            }
            is ModeOfWork.OnDemand -> {
                unScheduleCallJobService()
            }
        }
    }

    fun setModeOfWork(stringModeOfWork: String){
        modeOfWork = when(stringModeOfWork) {
            context.getString(R.string.pref_mode_of_work_default) -> {
                ModeOfWork.Background
            }
            context.getString(R.string.pref_mode_of_work_on_demand) -> {
                ModeOfWork.OnDemand
            }
            else -> {
                ModeOfWork.Background
            }
        }
    }

    private fun manageForegroundCallService(actionStopOrStart: String){
        val intent = Intent().apply {
            setClass(context, CallForegroundService::class.java)
            action = actionStopOrStart
        }
        ContextCompat.startForegroundService(context, intent)
    }

    private fun scheduleJobCallService(){
        if (jobScheduler is JobScheduler) {
            Log.d(TAG, "Schedule Job")
            val callJobBuilder = JobInfo.Builder(JOB_ID, callJobServiceComponentName)
                .setMinimumLatency(TimeUnit.SECONDS.toMillis(1))
                .setOverrideDeadline(TimeUnit.SECONDS.toMillis(5))//TODO: add setPersisted
                .setBackoffCriteria(TimeUnit.SECONDS.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR)
            jobScheduler.schedule(callJobBuilder.build())
        }
    }

    private fun unScheduleCallJobService(){
        if (jobScheduler is JobScheduler){
            Log.d(TAG, "unschedule job")
            jobScheduler.cancel(JOB_ID)
        }
    }
}