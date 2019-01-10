package com.media.dmitry68.callrecorder.preferences

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import com.media.dmitry68.callrecorder.R

class NumberPreference: DialogPreference {
    var number: Int = 0
        set(value) {
            field = value
            if (shouldPersist()){
                persistInt(value)
            }
        }
    private val numberPrefLayoutResId = R.layout.preference_number_picker

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogPreferenceStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes){
        number = 3
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int) = a?.getString(index) is Any

    override fun onSetInitialValue(defaultValue: Any?) {
        number = if (defaultValue == null)
                getPersistedInt(number)
            else
                defaultValue as Int
    }

    override fun getDialogLayoutResource() = numberPrefLayoutResId
}