package com.example.mobile_programming.data.mapper

import com.example.mobile_programming.data.entity.UserEntity
import com.example.mobile_programming.domain.models.User

fun UserEntity.toDomain(): User = User(
    id = id,
    login = login,
    fio = fio,
    gender = gender,
    course = course,
    birthDate = birthDate,
    zodiac = zodiac
)