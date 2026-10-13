package com.example.mobile_programming.domain.repository

import com.example.mobile_programming.domain.models.ScoreRecord

interface ScoreRepository {
    suspend fun save(userId: Long, score: Int, difficulty: Int, roundTime: Int)

    suspend fun getTop(limit: Int): List<ScoreRecord>

    suspend fun getTopByDifficulty(limit: Int, difficulty: Int): List<ScoreRecord>

    suspend fun getPersonal(userId: Long): ScoreRecord?
}