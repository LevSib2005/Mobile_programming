package com.example.mobile_programming.data.repository

import com.example.mobile_programming.data.dao.ScoreDao
import com.example.mobile_programming.data.entity.ScoreEntity
import com.example.mobile_programming.domain.models.ScoreRecord
import com.example.mobile_programming.domain.repository.ScoreRepository

class ScoreRepositoryImpl(
    private val dao: ScoreDao
) : ScoreRepository {

    override suspend fun save(userId: Long, score: Int, difficulty: Int, roundTime: Int) {
        val current = dao.getByUserId(userId)
        if (current == null || score > current.score) {
            dao.upsert(
                ScoreEntity(
                    id = current?.id ?: 0,
                    userId = userId,
                    score = score,
                    difficulty = difficulty,
                    roundTime = roundTime,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun getTop(limit: Int): List<ScoreRecord> = dao.getTop(limit)

    override suspend fun getTopByDifficulty(limit: Int, difficulty: Int): List<ScoreRecord> =
        dao.getTopByDifficulty(limit, difficulty)

    override suspend fun getPersonal(userId: Long): ScoreRecord? = dao.getPersonal(userId)
}