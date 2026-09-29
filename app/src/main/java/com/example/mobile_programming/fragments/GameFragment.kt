package com.example.mobile_programming.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.mobile_programming.MainActivity
import com.example.mobile_programming.R
import com.example.mobile_programming.models.Bug
import com.example.mobile_programming.views.GameView
import com.example.mobile_programming.models.GameSettings

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameView = view.findViewById(R.id.gameView)
        tvScore = view.findViewById(R.id.tvScore)
        tvTimer = view.findViewById(R.id.tvTimer)
        startOverlay = view.findViewById(R.id.startOverlay)
        resultOverlay = view.findViewById(R.id.resultOverlay)
        tvResultScore = view.findViewById(R.id.tvResultScore)
        tvResultHits = view.findViewById(R.id.tvResultHits)
        tvResultMisses = view.findViewById(R.id.tvResultMisses)
        tvResultAccuracy = view.findViewById(R.id.tvResultAccuracy)

        val btnStart: Button = view.findViewById(R.id.btnStart)
        val btnPlayAgain: Button = view.findViewById(R.id.btnPlayAgain)

        gameView.onBugKilled = { bug -> onBugKilled(bug) }
        gameView.onMiss = { onMiss() }

        btnStart.setOnClickListener { startRound() }
        btnPlayAgain.setOnClickListener { startRound() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backCallback
        )
    }

    private fun startRound() {
        score = 0
        hits = 0
        misses = 0
        timeLeft = GameSettings.roundTime

        tvScore.text = "Очки: 0"
        tvTimer.text = "$timeLeft сек"

        startOverlay.visibility = View.GONE
        resultOverlay.visibility = View.GONE

        (activity as? MainActivity)?.setGameMode(true)

        backCallback.isEnabled = true

        gameView.clearBugs()
        gameView.start()

        roundActive = true
        gameView.spawnBug()
        handler.postDelayed(spawnTask, 700)
        handler.postDelayed(timerTask, 1000)
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

    private fun onBugKilled(bug: Bug) {
        score += bug.points
        hits++
        tvScore.text = "Очки: $score"
    }

    private fun onMiss() {
        misses++
        score = (score - 1).coerceAtLeast(0)
        tvScore.text = "Очки: $score"
    }

    override fun onPause() {
        super.onPause()
        if (roundActive) endRound()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameView.stop()
        handler.removeCallbacksAndMessages(null)
    }
}