package com.example.mobile_programming.domain.models

data class User(
    val id: Long,
    val login: String,
    val fio: String,
    val gender: String,
    val course: Int,
    val birthDate: String,
    val zodiac: String
)
