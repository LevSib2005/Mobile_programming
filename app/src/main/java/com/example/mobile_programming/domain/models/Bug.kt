package com.example.mobile_programming.domain.models
data class Bug(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float,
    val type: BugType,
    val points: Int,
    val drawableRes: Int
)
