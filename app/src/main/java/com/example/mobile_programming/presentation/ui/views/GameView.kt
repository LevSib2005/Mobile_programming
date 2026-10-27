package com.example.mobile_programming.presentation.ui.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mobile_programming.R
import com.example.mobile_programming.domain.models.Bug
import com.example.mobile_programming.domain.models.BugFactory
import com.example.mobile_programming.domain.models.GameSettings
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val FIELD_W = 1000f
        const val FIELD_H = 1000f
        private const val FRAME_MS = 16L
    }

    // Список жуков теперь приходит из ViewModel
    private lateinit var bugs: MutableList<Bug>
    private var lastFrameTime = 0L
    private var isRunning = false

    private var goldRate: Double = 0.0
    private var timeSinceLastGoldBug = 0f

    // Переменные для бонуса
    private var isBonusVisible = false
    private var bonusX = 0f
    private var bonusY = 0f
    private val bonusSize = 80f
    private var timeSinceLastBonus = 0f
    private var bonusVisibleTimer = 0f
    private val bonusLifeTimeSec = 2f

    // Переменные для гравитации
    private var isGravityActive = false
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityTimer = 0f
    private val gravityDurationSec = 5f
    private val originalVelocities = mutableMapOf<Long, Pair<Float, Float>>()

    var onBugKilled: ((Bug) -> Unit)? = null
    var onMiss: (() -> Unit)? = null
    var onBonusClicked: (() -> Unit)? = null
    var onGravityEnded: (() -> Unit)? = null

    private val loop = object : Runnable {
        override fun run() {
            if (!isRunning) return
            val now = System.currentTimeMillis()
            val dt = if (lastFrameTime == 0L) 0f else (now - lastFrameTime) / 1000f
            lastFrameTime = now
            update(dt)
            invalidate()
            postDelayed(this, FRAME_MS)
        }
    }

    fun setBugs(newBugs: MutableList<Bug>) {
        this.bugs = newBugs
    }

    fun setGoldRate(rate: Double) {
        this.goldRate = rate
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        lastFrameTime = 0L
        post(loop)
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false
        removeCallbacks(loop)
    }

    fun resetBonusAndGravity() {
        isBonusVisible = false
        isGravityActive = false
        timeSinceLastBonus = 0f
        bonusVisibleTimer = 0f
        gravityTimer = 0f
        timeSinceLastGoldBug = 0f
        originalVelocities.clear()
    }

    // Передача данных с акселерометра
    fun setGravity(x: Float, y: Float) {
        if (isGravityActive) {
            gravityX = x
            gravityY = y
        }
    }

    // Вкл/выкл гравитации
    private fun setGravityActive(active: Boolean) {
        if (isGravityActive == active) return
        isGravityActive = active

        if (isGravityActive) {
            originalVelocities.clear()
            bugs.forEach { originalVelocities[it.id] = Pair(it.vx, it.vy) }
        } else {
            gravityX = 0f
            gravityY = 0f
            bugs.forEach { bug ->
                originalVelocities[bug.id]?.let { (vx, vy) ->
                    bug.vx = vx
                    bug.vy = vy
                }
            }
            originalVelocities.clear()
            onGravityEnded?.invoke()
        }
    }

    private fun update(dt: Float) {
        if (dt <= 0f || !::bugs.isInitialized) return

        // Логика таймеров бонуса
        if (isGravityActive) {
            gravityTimer += dt
            if (gravityTimer >= gravityDurationSec) {
                setGravityActive(false)
                gravityTimer = 0f
                timeSinceLastBonus = 0f
            }
        } else if (isBonusVisible) {
            bonusVisibleTimer += dt
            if (bonusVisibleTimer >= bonusLifeTimeSec) {
                isBonusVisible = false
                bonusVisibleTimer = 0f
                timeSinceLastBonus = 0f
            }
        } else {
            timeSinceLastBonus += dt
            if (timeSinceLastBonus >= GameSettings.bonusInterval) {
                bonusX = Random.nextFloat() * (FIELD_W - 300f) + 150f
                bonusY = Random.nextFloat() * (FIELD_H - 300f) + 150f
                isBonusVisible = true
                bonusVisibleTimer = 0f
                timeSinceLastBonus = 0f
            }
        }

        val sx = width / FIELD_W
        val sy = height / FIELD_H
        if (sx <= 0f || sy <= 0f) return

        // Золотой таракан
        if (goldRate > 0.0) {
            timeSinceLastGoldBug += dt
            if (timeSinceLastGoldBug >= GameSettings.goldBugInterval) {
                timeSinceLastGoldBug = 0f
                if (bugs.size < GameSettings.maxBugs) {
                    val x = Random.nextFloat() * (FIELD_W - 200f) + 100f
                    val y = Random.nextFloat() * (FIELD_H - 200f) + 100f
                    val id = System.currentTimeMillis() + Random.nextLong()
                    val goldBug = BugFactory.createGold(id, x, y, goldRate)
                    bugs.add(goldBug)
                }
            }
        } else {
            timeSinceLastGoldBug = 0f
        }

        for (bug in bugs) {
            // Физика гравитации
            if (isGravityActive) {
                bug.vx += gravityX * 400f * dt
                bug.vy += gravityY * 400f * dt
                val maxSpeed = 800f
                bug.vx = bug.vx.coerceIn(-maxSpeed, maxSpeed)
                bug.vy = bug.vy.coerceIn(-maxSpeed, maxSpeed)
            }

            bug.x += bug.vx * dt
            bug.y += bug.vy * dt

            val halfX = bug.size / 2f
            val halfY = (bug.size * sx) / (2f * sy)

            // Затухание скорости при гравитации
            val damping = if (isGravityActive) 0.7f else 1.0f

            if (bug.x < halfX) { bug.x = halfX; bug.vx = -bug.vx * damping }
            if (bug.x > FIELD_W - halfX) { bug.x = FIELD_W - halfX; bug.vx = -bug.vx * damping }
            if (bug.y < halfY) { bug.y = halfY; bug.vy = -bug.vy * damping }
            if (bug.y > FIELD_H - halfY) { bug.y = FIELD_H - halfY; bug.vy = -bug.vy * damping }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!::bugs.isInitialized) return

        val sx = width / FIELD_W
        val sy = height / FIELD_H

        for (bug in bugs) {
            val px = bug.x * sx
            val py = bug.y * sy
            val psize = bug.size * sx

            val drawable = ContextCompat.getDrawable(context, bug.drawableRes) ?: continue
            drawable.setBounds(
                (px - psize / 2f).toInt(),
                (py - psize / 2f).toInt(),
                (px + psize / 2f).toInt(),
                (py + psize / 2f).toInt()
            )
            drawable.draw(canvas)
        }

        // Отрисовка бонуса
        if (isBonusVisible) {
            val px = bonusX * sx
            val py = bonusY * sy
            val psize = bonusSize * sx
            val bonusDrawable = ContextCompat.getDrawable(context, R.drawable.bonus)
            bonusDrawable?.setBounds(
                (px - psize / 2f).toInt(),
                (py - psize / 2f).toInt(),
                (px + psize / 2f).toInt(),
                (py + psize / 2f).toInt()
            )
            bonusDrawable?.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!::bugs.isInitialized) return super.onTouchEvent(event)
        if (event.action != MotionEvent.ACTION_DOWN) return super.onTouchEvent(event)

        val sx = width / FIELD_W
        val sy = height / FIELD_H
        val lx = event.x / sx
        val ly = event.y / sy

        // Проверка клика по бонусу
        if (isBonusVisible) {
            val dx = lx - bonusX
            val dy = ly - bonusY
            val r = bonusSize / 2f
            if (dx * dx + dy * dy <= r * r) {
                isBonusVisible = false
                timeSinceLastBonus = 0f
                setGravityActive(true)
                gravityTimer = 0f
                invalidate()
                onBonusClicked?.invoke()
                return true
            }
        }

        for (i in bugs.indices.reversed()) {
            val bug = bugs[i]
            val dx = lx - bug.x
            val dy = ly - bug.y
            val r = bug.size / 2f
            if (dx * dx + dy * dy <= r * r) {
                bugs.removeAt(i)
                invalidate()
                onBugKilled?.invoke(bug)
                return true
            }
        }
        onMiss?.invoke()
        return true
    }
}