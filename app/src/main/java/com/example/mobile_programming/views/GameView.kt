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



}