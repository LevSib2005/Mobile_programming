package com.example.mobile_programming.presentation.game

enum class GamePhase { Idle, Running, Paused, Ended }

data class GameUiState(
    val score: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0,
    val timeLeft: Int = 0,
    val phase: GamePhase = GamePhase.Idle,
    val goldRate: Double = 0.0
) {
    val accuracy: Int
        get() {
            val total = hits + misses
            return if (total == 0) 0 else hits * 100 / total
        }
}