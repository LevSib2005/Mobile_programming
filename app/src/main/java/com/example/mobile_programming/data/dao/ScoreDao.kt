package com.example.mobile_programming.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobile_programming.data.entity.ScoreEntity
import com.example.mobile_programming.domain.models.ScoreRecord

@Dao
interface ScoreDao {

    @Query("SELECT * FROM scores WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): ScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ScoreEntity): Long

    @Query(
        """
        SELECT u.fio AS playerName,
               s.score AS score,
               s.difficulty AS difficulty,
               s.roundTime AS roundTime,
               s.updatedAt AS updatedAt
        FROM scores s
        INNER JOIN users u ON u.id = s.userId
        ORDER BY s.score DESC, s.updatedAt ASC, s.id DESC
        LIMIT :limit
        """
    )
    suspend fun getTop(limit: Int): List<ScoreRecord>

    @Query(
        """
        SELECT u.fio AS playerName,
               s.score AS score,
               s.difficulty AS difficulty,
               s.roundTime AS roundTime,
               s.updatedAt AS updatedAt
        FROM scores s
        INNER JOIN users u ON u.id = s.userId
        WHERE s.userId = :userId
        LIMIT 1
        """
    )
    suspend fun getPersonal(userId: Long): ScoreRecord?

    @Query(
        """
    SELECT u.fio AS playerName,
           s.score AS score,
           s.difficulty AS difficulty,
           s.roundTime AS roundTime,
           s.updatedAt AS updatedAt
    FROM scores s
    INNER JOIN users u ON u.id = s.userId
    WHERE s.difficulty = :difficulty
    ORDER BY s.score DESC, s.updatedAt ASC, s.id DESC
    LIMIT :limit
    """
    )
    suspend fun getTopByDifficulty(limit: Int, difficulty: Int): List<ScoreRecord>
}