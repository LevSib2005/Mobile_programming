package com.example.mobile_programming.di

import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.data.repository.UserRepositoryImpl
import com.example.mobile_programming.domain.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val userModule = module {
    single { SessionManager(androidContext()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}