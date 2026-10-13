package com.example.mobile_programming.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["login"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val login: String,
    val password: String,
    val fio: String,
    val gender: String,
    val course: Int,
    val birthDate: String,
    val zodiac: String
)