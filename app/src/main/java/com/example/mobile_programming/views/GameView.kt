package com.example.mobile_programming.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mobile_programming.models.Bug
import com.example.mobile_programming.models.BugFactory
import com.example.mobile_programming.models.GameSettings
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

    private val bugs = mutableListOf<Bug>()
    private var lastFrameTime = 0L

    var onBugKilled: ((Bug) -> Unit)? = null
    var onMiss: (() -> Unit)? = null

    private val loop = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            val dt = if (lastFrameTime == 0L) 0f else (now - lastFrameTime) / 1000f
            lastFrameTime = now
            update(dt)
            invalidate()
            postDelayed(this, FRAME_MS)
        }
    }

    fun start() {
        lastFrameTime = 0L
        post(loop)
    }

    fun stop() {
        removeCallbacks(loop)
    }

    fun clearBugs() {
        bugs.clear()
        invalidate()
    }

    fun spawnBug(): Bug? {
        if (bugs.size >= GameSettings.maxBugs) return null

        val id = System.currentTimeMillis() + Random.nextLong()
        val x = Random.nextFloat() * (FIELD_W - 200f) + 100f
        val y = Random.nextFloat() * (FIELD_H - 200f) + 100f
        val bug = BugFactory.createRandom(id, x, y)
        bugs.add(bug)
        return bug
    }

    private fun update(dt: Float) {
        if (dt <= 0f) return
        for (bug in bugs) {
            bug.x += bug.vx * dt
            bug.y += bug.vy * dt

            val half = bug.size / 2f
            if (bug.x < half) { bug.x = half; bug.vx = -bug.vx }
            if (bug.x > FIELD_W - half) { bug.x = FIELD_W - half; bug.vx = -bug.vx }
            if (bug.y < half) { bug.y = half; bug.vy = -bug.vy }
            if (bug.y > FIELD_H - half) { bug.y = FIELD_H - half; bug.vy = -bug.vy }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
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
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return super.onTouchEvent(event)

        val sx = width / FIELD_W
        val sy = height / FIELD_H
        val lx = event.x / sx
        val ly = event.y / sy

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