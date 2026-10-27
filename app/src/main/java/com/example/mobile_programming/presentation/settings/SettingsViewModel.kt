package com.example.mobile_programming.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.mobile_programming.domain.models.GameSettings

class SettingsViewModel : ViewModel() {
    fun updateSpeed(value: Int) { GameSettings.speed = value }
    fun updateMaxBugs(value: Int) { GameSettings.maxBugs = value }
    fun updateBonusInterval(value: Int) { GameSettings.bonusInterval = value }
    fun updateRoundTime(value: Int) { GameSettings.roundTime = value }
    fun updateGoldBugInterval(value: Int) { GameSettings.goldBugInterval = value }
}