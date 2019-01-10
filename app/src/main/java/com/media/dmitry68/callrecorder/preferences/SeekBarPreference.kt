package com.media.dmitry68.callrecorder.preferences

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import com.media.dmitry68.callrecorder.R

class SeekBarPreference : DialogPreference {
    var value = DEFAULT_VALUE
        set(value) {
            field = validate(value)
            if (shouldPersist()){
                persistInt(value)
            }
            summary = getValueString(value)
        }
    var minValue = DEFAULT_MIN
    var maxValue = DEFAULT_MAX
    var stepSize = DEFAULT_STEP
    private var measurementUnit: String? = DEFAULT_MEASUREMENT_UNIT

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference).apply {
                try {
                    minValue = getInteger(R.styleable.SeekBarPreference_minimumValue, DEFAULT_MIN)
                    maxValue = getInteger(R.styleable.SeekBarPreference_maximumValue, DEFAULT_MAX)
                    stepSize = getInteger(R.styleable.SeekBarPreference_stepSize, DEFAULT_STEP)
                    measurementUnit = getString(R.styleable.SeekBarPreference_units)
                    if (measurementUnit == null) {
                        measurementUnit = DEFAULT_MEASUREMENT_UNIT
                    }
                } finally {
                    recycle()
                }
            }
        }
        dialogLayoutResource = R.layout.preference_seek_bar
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int) = a?.getInteger(index, DEFAULT_VALUE) is Any

    override fun onSetInitialValue(defaultValue: Any?) {
        value = if (defaultValue == null)
            getPersistedInt(value)
        else
            defaultValue as Int
    }

    override fun onAttached() {
        summary = getValueString(value)
        super.onAttached()
    }

    fun getValueString(value: Int) : String =
        context.getString(R.string.seekBar_pref_summary, value, measurementUnit)

    fun validate(value: Int) : Int{
        var newValue = Math.round(value / stepSize.toFloat()) * stepSize
        if (value == minValue || newValue < minValue){
            newValue = minValue
        }
        if (value == maxValue || newValue > maxValue){
            newValue = maxValue
        }
        return newValue
    }

    companion object {
        const val DEFAULT_VALUE = 70
        const val DEFAULT_MIN = 1
        const val DEFAULT_MAX = 100
        const val DEFAULT_STEP = 10
        const val DEFAULT_MEASUREMENT_UNIT = "%"
    }
}