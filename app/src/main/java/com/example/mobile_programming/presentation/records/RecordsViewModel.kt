package com.example.mobile_programming.presentation.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.domain.models.ScoreRecord
import com.example.mobile_programming.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordsViewModel(
    private val scoreRepository: ScoreRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _topRecords = MutableStateFlow<List<ScoreRecord>>(emptyList())
    val topRecords: StateFlow<List<ScoreRecord>> = _topRecords

    private val _personalRecord = MutableStateFlow<ScoreRecord?>(null)
    val personalRecord: StateFlow<ScoreRecord?> = _personalRecord

    fun loadTop3(difficulty: Int?) {
        viewModelScope.launch {
            _topRecords.value = if (difficulty == null) {
                scoreRepository.getTop(3)
            } else {
                scoreRepository.getTopByDifficulty(3, difficulty)
            }
        }
    }

    fun loadPersonal() {
        val userId = sessionManager.getUserId()
        if (userId > 0) {
            viewModelScope.launch {
                _personalRecord.value = scoreRepository.getPersonal(userId)
            }
        }
    }
}