package com.example.mobile_programming.di

import androidx.room.Room
import com.example.mobile_programming.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().scoreDao() }
}