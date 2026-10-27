package com.example.mobile_programming.di

import com.example.mobile_programming.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel() }
}