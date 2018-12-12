package com.media.dmitry68.callrecorder.preferences

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

open class PreferenceChangeAware {
    protected val propertyChangeSupport = PropertyChangeSupport(this)

    fun addPropertyChangeListener(listener: PropertyChangeListener){
        propertyChangeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener){
        propertyChangeSupport.removePropertyChangeListener(listener)
    }
}