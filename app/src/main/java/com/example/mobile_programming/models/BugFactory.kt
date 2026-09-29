package com.example.mobile_programming.models

import com.example.mobile_programming.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object BugFactory {

    private const val BASE_SPEED_NORMAL = 100f
    private const val BASE_SPEED_FAST = 220f
    private const val BASE_SPEED_RARE = 70f

    private const val SIZE_NORMAL = 110f
    private const val SIZE_FAST = 75f
    private const val SIZE_RARE = 55f

    private const val POINTS_NORMAL = 1
    private const val POINTS_FAST = 2
    private const val POINTS_RARE = 5

    fun createNormal(id: Long, x: Float, y: Float): Bug {
        val (dx, dy) = randomDir()
        val m = speedMultiplier()
        return Bug(
            id = id, x = x, y = y,
            vx = dx * BASE_SPEED_NORMAL * m,
            vy = dy * BASE_SPEED_NORMAL * m,
            size = SIZE_NORMAL,
            type = BugType.NORMAL,
            points = POINTS_NORMAL,
            drawableRes = R.drawable.bug_normal
        )
    }
    fun createFast(id: Long, x: Float, y: Float): Bug {
        val (dx, dy) = randomDir()
        val m = speedMultiplier()
        return Bug(
            id = id, x = x, y = y,
            vx = dx * BASE_SPEED_FAST * m,
            vy = dy * BASE_SPEED_FAST * m,
            size = SIZE_FAST,
            type = BugType.FAST,
            points = POINTS_FAST,
            drawableRes = R.drawable.bug_fast
        )
    }

    fun createRare(id: Long, x: Float, y: Float): Bug {
        val (dx, dy) = randomDir()
        val m = speedMultiplier()
        return Bug(
            id = id, x = x, y = y,
            vx = dx * BASE_SPEED_RARE * m,
            vy = dy * BASE_SPEED_RARE * m,
            size = SIZE_RARE,
            type = BugType.RARE,
            points = POINTS_RARE,
            drawableRes = R.drawable.bug_rare
        )
    }

    fun createRandom(id: Long, x: Float, y: Float): Bug {
        val r = Random.nextInt(100)
        return when {
            r < 60 -> createNormal(id, x, y)
            r < 90 -> createFast(id, x, y)
            else   -> createRare(id, x, y)
        }
    }

    private fun randomDir(): Pair<Float, Float> {
        val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
        return cos(angle) to sin(angle)
    }

    private fun speedMultiplier(): Float {
        val s = GameSettings.speed.coerceIn(0, 10)
        return s / 5f
    }
}

