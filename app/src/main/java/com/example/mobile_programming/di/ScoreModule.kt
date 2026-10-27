package com.example.mobile_programming.di

import com.example.mobile_programming.data.repository.ScoreRepositoryImpl
import com.example.mobile_programming.domain.repository.ScoreRepository
import com.example.mobile_programming.presentation.records.RecordsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scoreModule = module {
    single<ScoreRepository> { ScoreRepositoryImpl(get()) }
    viewModel { RecordsViewModel(get(), get()) }
}