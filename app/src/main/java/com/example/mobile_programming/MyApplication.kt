package com.example.mobile_programming

import android.app.Application
import androidx.room.Room
import com.example.mobile_programming.data.api.CbrApi
import com.example.mobile_programming.data.local.AppDatabase
import com.example.mobile_programming.data.local.GoldRateCache
import com.example.mobile_programming.data.repository.GoldRateRepositoryImpl
import com.example.mobile_programming.data.repository.ScoreRepositoryImpl
import com.example.mobile_programming.data.repository.UserRepositoryImpl
import com.example.mobile_programming.domain.repository.GoldRateRepository
import com.example.mobile_programming.domain.repository.ScoreRepository
import com.example.mobile_programming.domain.repository.UserRepository
import retrofit2.Retrofit

class MyApplication : Application() {

    private lateinit var database: AppDatabase
    private lateinit var cbrApi: CbrApi
    
    private lateinit var goldRateCache: GoldRateCache

    lateinit var userRepository: UserRepository
        private set
    lateinit var scoreRepository: ScoreRepository
        private set
    lateinit var goldRateRepository: GoldRateRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "app.db"
        ).fallbackToDestructiveMigration().build()

        cbrApi = Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/")
            .build()
            .create(CbrApi::class.java)

        goldRateCache = GoldRateCache(this)

        userRepository = UserRepositoryImpl(database.userDao())
        scoreRepository = ScoreRepositoryImpl(database.scoreDao())
        goldRateRepository = GoldRateRepositoryImpl(cbrApi, goldRateCache)
    }
}