package com.example.mobile_programming.domain.repository

import com.example.mobile_programming.domain.models.GoldRate

interface GoldRateRepository {
    suspend fun loadRate(): GoldRate
}
