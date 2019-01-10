package com.media.dmitry68.callrecorder.preferences

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.media.dmitry68.callrecorder.R

class SeekBarPreferenceFragmentCompat : PreferenceDialogFragmentCompat(), DialogPreference.TargetFragment, SeekBar.OnSeekBarChangeListener {
    private lateinit var seekBar: SeekBar
    private lateinit var textView: TextView

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        seekBar = view!!.findViewById(R.id.seekBarSensitivity)
        textView = view.findViewById(R.id.textValueSensitivity)
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder?) {
        super.onPrepareDialogBuilder(builder)
        val seekBarPreference = preference
        if (seekBarPreference is SeekBarPreference){
            seekBar.max = seekBarPreference.maxValue - seekBarPreference.minValue
            seekBar.setOnSeekBarChangeListener(this)
            seekBar.progress = seekBarPreference.value - seekBarPreference.minValue
            textView.text = seekBarPreference.getValueString(seekBarPreference.value)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult){
            val seekBarPreference = preference
            if (seekBarPreference is SeekBarPreference){
                val value = seekBar.progress + seekBarPreference.minValue
                seekBarPreference.value = value
            }
        }
    }

    override fun findPreference(p0: CharSequence?): Preference = preference

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val seekBarPreference = preference
        if (seekBarPreference is SeekBarPreference){
            val value = seekBarPreference.validate(progress + seekBarPreference.minValue)
            if (value != (progress + seekBarPreference.minValue)){
                seekBar?.progress = value - seekBarPreference.minValue
            }
            textView.text = seekBarPreference.getValueString(value)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    companion object {
        fun newInstance(key: String) : SeekBarPreferenceFragmentCompat{
            val fragment = SeekBarPreferenceFragmentCompat()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }
}