package com.example.mobile_programming.domain.repository

import com.example.mobile_programming.domain.models.User

interface UserRepository {
    suspend fun register(
        login: String,
        password: String,
        fio: String,
        gender: String,
        course: Int,
        birthDate: String,
        zodiac: String
    ): Result<Long>

    suspend fun login(login: String, password: String): Result<User>

    suspend fun getById(id: Long): User?
}