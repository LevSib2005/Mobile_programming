package com.example.mobile_programming.domain.models

data class ScoreRecord(
    val playerName: String,
    val score: Int,
    val difficulty: Int,
    val roundTime: Int,
    val updatedAt: Long
)