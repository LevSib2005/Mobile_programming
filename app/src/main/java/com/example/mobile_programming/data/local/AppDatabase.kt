package com.example.mobile_programming.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mobile_programming.data.dao.ScoreDao
import com.example.mobile_programming.data.dao.UserDao
import com.example.mobile_programming.data.entity.UserEntity
import com.example.mobile_programming.data.entity.ScoreEntity

@Database(
    entities = [UserEntity::class, ScoreEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scoreDao(): ScoreDao
}