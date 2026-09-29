package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile_programming.R
import com.example.mobile_programming.models.GameSettings

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSeekBar(
            view.findViewById(R.id.sbSpeed),
            view.findViewById(R.id.tvSpeedValue),
            "Скорость игры: ",
            GameSettings.speed
        ) { v -> GameSettings.speed = v }

        setupSeekBar(
            view.findViewById(R.id.sbMaxBugs),
            view.findViewById(R.id.tvMaxBugsValue),
            "Максимум тараканов на экране: ",
            GameSettings.maxBugs
        ) { v -> GameSettings.maxBugs = v }

        setupSeekBar(
            view.findViewById(R.id.sbBonusInterval),
            view.findViewById(R.id.tvBonusIntervalValue),
            "Интервал появления бонусов: ",
            GameSettings.bonusInterval,
            " сек"
        ) { v -> GameSettings.bonusInterval = v }

        setupSeekBar(
            view.findViewById(R.id.sbRoundTime),
            view.findViewById(R.id.tvRoundTimeValue),
            "Длительность раунда: ",
            GameSettings.roundTime,
            " сек"
        ) { v -> GameSettings.roundTime = v }
    }

    private fun setupSeekBar(
        seekBar: SeekBar,
        label: TextView,
        prefix: String,
        initialValue: Int,
        suffix: String = "",
        onChange: (Int) -> Unit
    ) {
        seekBar.progress = (initialValue - 1).coerceAtLeast(0)
        label.text = "$prefix${seekBar.progress + 1}$suffix"
        onChange(seekBar.progress)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress
                label.text = "$prefix$value$suffix"
                onChange(value)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }
}