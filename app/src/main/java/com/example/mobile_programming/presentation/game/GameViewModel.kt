package com.example.mobile_programming.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.domain.models.Bug
import com.example.mobile_programming.domain.models.BugFactory
import com.example.mobile_programming.domain.models.GameSettings
import com.example.mobile_programming.domain.repository.GoldRateRepository
import com.example.mobile_programming.domain.repository.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val scoreRepository: ScoreRepository,
    private val goldRateRepository: GoldRateRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    val bugs = mutableListOf<Bug>()

    private var timerJob: Job? = null

    init { loadGoldRate() }

    private fun loadGoldRate() {
        viewModelScope.launch {
            try {
                val rate = goldRateRepository.loadRate()
                _state.update { it.copy(goldRate = rate.valueRubPerGram) }
            } catch (_: Exception) { }
        }
    }

    fun spawnBug() {
        if (bugs.size >= GameSettings.maxBugs) return
        val id = System.currentTimeMillis() + Random.nextLong()
        val x = Random.nextFloat() * (1000f - 200f) + 100f
        val y = Random.nextFloat() * (1000f - 200f) + 100f
        val bug = BugFactory.createRandom(id, x, y)
        bugs.add(bug)
    }

    fun startRound() {
        timerJob?.cancel()
        bugs.clear()
        _state.value = GameUiState(
            timeLeft = GameSettings.roundTime,
            phase = GamePhase.Running,
            goldRate = _state.value.goldRate
        )
        startTimer()
    }

    fun pauseRound() {
        if (_state.value.phase != GamePhase.Running) return
        timerJob?.cancel()
        _state.update { it.copy(phase = GamePhase.Paused) }
    }

    fun resumeRound() {
        if (_state.value.phase != GamePhase.Paused) return
        _state.update { it.copy(phase = GamePhase.Running) }
        startTimer()
    }

    fun exitToMenu() {
        timerJob?.cancel()
        bugs.clear()
        _state.update { it.copy(phase = GamePhase.Idle) }
    }

    fun onBugKilled(points: Int) {
        if (_state.value.phase != GamePhase.Running) return
        _state.update { it.copy(score = it.score + points, hits = it.hits + 1) }
    }

    fun onMiss() {
        if (_state.value.phase != GamePhase.Running) return
        _state.update {
            it.copy(misses = it.misses + 1, score = (it.score - 1).coerceAtLeast(0))
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.phase == GamePhase.Running && _state.value.timeLeft > 0) {
                delay(1000)
                if (_state.value.phase != GamePhase.Running) return@launch
                val newTime = _state.value.timeLeft - 1
                _state.update { it.copy(timeLeft = newTime) }
                if (newTime <= 0) { endRound(); return@launch }
            }
        }
    }

    private fun endRound() {
        timerJob?.cancel()
        val final = _state.value
        _state.update { it.copy(phase = GamePhase.Ended) }
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId > 0) {
                scoreRepository.save(userId, final.score, GameSettings.speed, GameSettings.roundTime)
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}