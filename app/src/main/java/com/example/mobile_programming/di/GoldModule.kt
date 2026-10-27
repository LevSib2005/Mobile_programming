package com.example.mobile_programming.di

import com.example.mobile_programming.data.local.GoldRateCache
import com.example.mobile_programming.data.repository.GoldRateRepositoryImpl
import com.example.mobile_programming.domain.repository.GoldRateRepository
import com.example.mobile_programming.presentation.game.GameViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val goldModule = module {
    single { GoldRateCache(androidContext()) }
    single<GoldRateRepository> { GoldRateRepositoryImpl(get(), get()) }
    viewModel { GameViewModel(get(), get(), get()) }
}