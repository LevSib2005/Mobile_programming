package com.example.mobile_programming

import android.app.Application
import androidx.room.Room
import com.example.mobile_programming.data.local.AppDatabase
import com.example.mobile_programming.data.repository.ScoreRepositoryImpl
import com.example.mobile_programming.data.repository.UserRepositoryImpl
import com.example.mobile_programming.domain.repository.ScoreRepository
import com.example.mobile_programming.domain.repository.UserRepository

class MyApplication : Application() {

    private lateinit var database: AppDatabase

    lateinit var userRepository: UserRepository
        private set

    lateinit var scoreRepository: ScoreRepository      // ← новый
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "app.db"
        )
            .build()

        userRepository = UserRepositoryImpl(database.userDao())
        scoreRepository = ScoreRepositoryImpl(database.scoreDao())   // ← новый
    }
}