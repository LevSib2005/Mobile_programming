package com.example.mobile_programming.fragments

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.mobile_programming.MainActivity
import com.example.mobile_programming.views.GameView

class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var startOverlay: LinearLayout
    private lateinit var resultOverlay: LinearLayout
    private lateinit var tvResultScore: TextView
    private lateinit var tvResultHits: TextView
    private lateinit var tvResultMisses: TextView
    private lateinit var tvResultAccuracy: TextView

    private var score = 0
    private var hits = 0
    private var misses = 0
    private var timeLeft = 0
    private var roundActive = false

    private val handler = Handler(Looper.getMainLooper())

    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            endRound()
        }
    }

    private val spawnTask = object : Runnable {
        override fun run() {
            if (!roundActive) return
            gameView.spawnBug()
            handler.postDelayed(this, 700)
        }
    }

    private val timerTask = object : Runnable {
        override fun run() {
            if (!roundActive) return
            timeLeft--
            tvTimer.text = "$timeLeft сек"
            if (timeLeft <= 0) endRound() else handler.postDelayed(this, 1000)
        }
    }

    private fun endRound() {
        roundActive = false
        handler.removeCallbacks(spawnTask)
        handler.removeCallbacks(timerTask)
        gameView.stop()

        (activity as? MainActivity)?.setGameMode(false)

        backCallback.isEnabled = false

        val total = hits + misses
        val accuracy = if (total == 0) 0 else (hits * 100 / total)

        tvResultScore.text = "Очки: $score"
        tvResultHits.text = "Попаданий: $hits"
        tvResultMisses.text = "Промахов: $misses"
        tvResultAccuracy.text = "Точность: $accuracy%"

        resultOverlay.visibility = View.VISIBLE
    }
}