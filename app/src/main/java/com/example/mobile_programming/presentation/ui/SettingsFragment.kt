package com.example.mobile_programming.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile_programming.R
import com.example.mobile_programming.domain.models.GameSettings
import com.example.mobile_programming.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

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
            GameSettings.speed,
            viewModel::updateSpeed
        )

        setupSeekBar(
            view.findViewById(R.id.sbMaxBugs),
            view.findViewById(R.id.tvMaxBugsValue),
            "Максимум тараканов на экране: ",
            GameSettings.maxBugs,
            viewModel::updateMaxBugs
        )

        setupSeekBar(
            view.findViewById(R.id.sbBonusInterval),
            view.findViewById(R.id.tvBonusIntervalValue),
            "Интервал появления бонусов: ",
            GameSettings.bonusInterval,
            viewModel::updateBonusInterval,
            " сек"
        )

        setupSeekBar(
            view.findViewById(R.id.sbRoundTime),
            view.findViewById(R.id.tvRoundTimeValue),
            "Длительность раунда: ",
            GameSettings.roundTime,
            viewModel::updateRoundTime,
            " сек"
        )

        setupSeekBar(
            view.findViewById(R.id.sbGoldBugInterval),
            view.findViewById(R.id.tvGoldBugIntervalValue),
            "Интервал золотого жука: ",
            GameSettings.goldBugInterval,
            viewModel::updateGoldBugInterval,
            " сек"
        )
    }

    private fun setupSeekBar(
        seekBar: SeekBar,
        label: TextView,
        prefix: String,
        initialValue: Int,
        onChange: (Int) -> Unit,
        suffix: String = ""
    ) {
        seekBar.progress = initialValue
        label.text = "$prefix$initialValue$suffix"
        onChange(initialValue)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = if (progress == 0 && seekBar.id == R.id.sbMaxBugs) 1 else progress

                label.text = "$prefix$value$suffix"
                onChange(value)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }
}