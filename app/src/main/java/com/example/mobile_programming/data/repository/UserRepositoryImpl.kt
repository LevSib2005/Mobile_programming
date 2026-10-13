package com.example.mobile_programming.data.repository

import com.example.mobile_programming.data.dao.UserDao
import com.example.mobile_programming.data.entity.UserEntity
import com.example.mobile_programming.data.mapper.toDomain
import com.example.mobile_programming.domain.models.User
import com.example.mobile_programming.domain.repository.UserRepository

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {

    override suspend fun register(
        login: String,
        password: String,
        fio: String,
        gender: String,
        course: Int,
        birthDate: String,
        zodiac: String
    ): Result<Long> {
        val existing = dao.findByLogin(login)
        if (existing != null) {
            return Result.failure(Exception("Логин занят"))
        }
        return try {
            val id = dao.insert(
                UserEntity(
                    login = login,
                    password = password,
                    fio = fio,
                    gender = gender,
                    course = course,
                    birthDate = birthDate,
                    zodiac = zodiac
                )
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(login: String, password: String): Result<User> {
        val entity = dao.login(login, password)
            ?: return Result.failure(Exception("Неверный логин или пароль"))
        return Result.success(entity.toDomain())
    }

    override suspend fun getById(id: Long): User? = dao.findById(id)?.toDomain()
}