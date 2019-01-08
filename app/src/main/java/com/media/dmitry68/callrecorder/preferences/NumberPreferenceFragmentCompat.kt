package com.media.dmitry68.callrecorder.preferences

import android.os.Bundle
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import com.github.stephenvinouze.materialnumberpickercore.MaterialNumberPicker
import com.media.dmitry68.callrecorder.R


class NumberPreferenceFragmentCompat : PreferenceDialogFragmentCompat() {
    private lateinit var numberPicker : MaterialNumberPicker

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        numberPicker = view!!.findViewById(R.id.number_picker) as MaterialNumberPicker

        val dialogPreference = preference
        if (dialogPreference is NumberPreference){
            numberPicker.value = dialogPreference.number
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult){
            val number = numberPicker.value
            val numberPreference = preference
            if (numberPreference is NumberPreference){
                if (numberPreference.callChangeListener(number)){
                    numberPreference.number = number
                }
            }
        }
    }

    companion object {
        fun newInstance(key: String) : NumberPreferenceFragmentCompat{
            val fragment = NumberPreferenceFragmentCompat()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }
}