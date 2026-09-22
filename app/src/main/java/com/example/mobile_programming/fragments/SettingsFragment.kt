package com.example.mobile_programming.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile_programming.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSeekBar(
            view.findViewById(R.id.sbSpeed),
            view.findViewById(R.id.tvSpeedValue),
            "Скорость игры: "
        )
        setupSeekBar(
            view.findViewById(R.id.sbMaxBugs),
            view.findViewById(R.id.tvMaxBugsValue),
            "Максимум тараканов на экране: "
        )
        setupSeekBar(
            view.findViewById(R.id.sbBonusInterval),
            view.findViewById(R.id.tvBonusIntervalValue),
            "Интервал появления бонусов: ",
            " сек"
        )
        setupSeekBar(
            view.findViewById(R.id.sbRoundTime),
            view.findViewById(R.id.tvRoundTimeValue),
            "Длительность раунда: ",
            " сек"
        )
    }

    // Общий метод
    private fun setupSeekBar(
        seekBar: SeekBar,
        label: TextView,
        prefix: String,
        suffix: String = ""
    ) {
        label.text = "$prefix${seekBar.progress}$suffix"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                label.text = "$prefix$progress$suffix"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }
}