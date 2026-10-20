package com.example.mobile_programming.presentation.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile_programming.MyApplication
import com.example.mobile_programming.R
import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.domain.models.Bug
import com.example.mobile_programming.presentation.ui.views.GameView
import com.example.mobile_programming.domain.models.GameSettings
import com.example.mobile_programming.domain.repository.ScoreRepository
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var tvScore: TextView
    private lateinit var tvTimer: TextView
    private lateinit var startOverlay: LinearLayout
    private lateinit var resultOverlay: LinearLayout
    private lateinit var pauseOverlay: LinearLayout
    private lateinit var tvResultScore: TextView
    private lateinit var tvResultHits: TextView
    private lateinit var tvResultMisses: TextView
    private lateinit var tvResultAccuracy: TextView

    private lateinit var scoreRepo: ScoreRepository
    private lateinit var session: SessionManager

    private lateinit var soundPool: SoundPool
    private var screamSoundId = 0
    private var screamStreamId = 0

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var score = 0
    private var hits = 0
    private var misses = 0
    private var timeLeft = 0

    private var roundActive = false
    private var roundPaused = false

    private val handler = Handler(Looper.getMainLooper())

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                gameView.setGravity(-event.values[0], -event.values[1])
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (roundPaused) resumeRound() else pauseRound()
        }
    }

    private val spawnTask = object : Runnable {
        override fun run() {
            if (!roundActive || roundPaused) return
            gameView.spawnBug()
            handler.postDelayed(this, 700)
        }
    }

    private val timerTask = object : Runnable {
        override fun run() {
            if (!roundActive || roundPaused) return
            timeLeft--
            tvTimer.text = "$timeLeft сек"
            if (timeLeft <= 0) endRound()
            else handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireContext().applicationContext as MyApplication
        scoreRepo = app.scoreRepository
        session = SessionManager(requireContext())

        gameView = view.findViewById(R.id.gameView)
        tvScore = view.findViewById(R.id.tvScore)
        tvTimer = view.findViewById(R.id.tvTimer)
        startOverlay = view.findViewById(R.id.startOverlay)
        resultOverlay = view.findViewById(R.id.resultOverlay)
        pauseOverlay = view.findViewById(R.id.pauseOverlay)
        tvResultScore = view.findViewById(R.id.tvResultScore)
        tvResultHits = view.findViewById(R.id.tvResultHits)
        tvResultMisses = view.findViewById(R.id.tvResultMisses)
        tvResultAccuracy = view.findViewById(R.id.tvResultAccuracy)

        val btnStart: Button = view.findViewById(R.id.btnStart)
        val btnPlayAgain: Button = view.findViewById(R.id.btnPlayAgain)
        val btnPause: Button = view.findViewById(R.id.btnPause)
        val btnResume: Button = view.findViewById(R.id.btnResume)
        val btnRestart: Button = view.findViewById(R.id.btnRestart)
        val btnExit: Button = view.findViewById(R.id.btnExit)

        // Инициализация SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
        screamSoundId = soundPool.load(requireContext(), R.raw.bug_scream, 1)

        // Инициализация акселерометра
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        gameView.onBugKilled = { bug -> onBugKilled(bug) }
        gameView.onMiss = { onMiss() }

        // Обработка клика по бонусу
        gameView.onBonusClicked = {
            screamStreamId = soundPool.play(screamSoundId, 1f, 1f, 1, -1, 1f) // -1 = зациклить звук
            Toast.makeText(requireContext(), "Гравитация на 5 секунд!", Toast.LENGTH_SHORT).show()
        }

        gameView.onBugKilled = { bug -> onBugKilled(bug) }
        gameView.onMiss = { onMiss() }

        btnStart.setOnClickListener { startRound() }
        btnPlayAgain.setOnClickListener { startRound() }
        btnPause.setOnClickListener { pauseRound() }
        btnResume.setOnClickListener { resumeRound() }
        btnRestart.setOnClickListener { startRound() }
        btnExit.setOnClickListener { exitToMenu() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backCallback
        )
    }

    private fun startRound() {
        stopAllTasks()
        gameView.stop()

        score = 0
        hits = 0
        misses = 0
        timeLeft = GameSettings.roundTime
        roundActive = true
        roundPaused = false

        tvScore.text = "Очки: 0"
        tvTimer.text = "$timeLeft сек"

        startOverlay.visibility = View.GONE
        resultOverlay.visibility = View.GONE
        pauseOverlay.visibility = View.GONE

        (parentFragment as? MainTabsFragment)?.setGameMode(true)
        backCallback.isEnabled = true

        gameView.clearBugs()
        gameView.start()

        gameView.spawnBug()
        handler.postDelayed(spawnTask, 700)
        handler.postDelayed(timerTask, 1000)
    }

    private fun pauseRound() {
        if (!roundActive || roundPaused) return
        roundPaused = true
        stopAllTasks()
        gameView.stop()
        pauseOverlay.visibility = View.VISIBLE
    }

    private fun resumeRound() {
        if (!roundActive || !roundPaused) return
        roundPaused = false
        pauseOverlay.visibility = View.GONE
        gameView.start()
        handler.postDelayed(spawnTask, 700)
        handler.postDelayed(timerTask, 1000)
    }

    private fun exitToMenu() {
        roundActive = false
        roundPaused = false
        stopAllTasks()
        gameView.stop()
        gameView.clearBugs()

        pauseOverlay.visibility = View.GONE
        resultOverlay.visibility = View.GONE
        startOverlay.visibility = View.VISIBLE

        (parentFragment as? MainTabsFragment)?.setGameMode(false)
        backCallback.isEnabled = false
    }

    private fun endRound() {
        roundActive = false
        roundPaused = false
        stopAllTasks()
        gameView.stop()

        (parentFragment as? MainTabsFragment)?.setGameMode(false)
        backCallback.isEnabled = false

        val total = hits + misses
        val accuracy = if (total == 0) 0 else (hits * 100 / total)

        tvResultScore.text = "Очки: $score"
        tvResultHits.text = "Попаданий: $hits"
        tvResultMisses.text = "Промахов: $misses"
        tvResultAccuracy.text = "Точность: $accuracy%"

        pauseOverlay.visibility = View.GONE
        resultOverlay.visibility = View.VISIBLE

        val userId = session.getUserId()
        if (userId > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                scoreRepo.save(userId, score, GameSettings.speed, GameSettings.roundTime)
            }
        }
    }

    private fun stopAllTasks() {
        handler.removeCallbacks(spawnTask)
        handler.removeCallbacks(timerTask)
    }

    private fun onBugKilled(bug: Bug) {
        if (roundPaused) return
        score += bug.points
        hits++
        tvScore.text = "Очки: $score"
    }

    private fun onMiss() {
        if (roundPaused) return
        misses++
        score = (score - 1).coerceAtLeast(0)
        tvScore.text = "Очки: $score"
    }

    override fun onPause() {
        super.onPause()
        if (roundActive && !roundPaused) pauseRound()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        roundActive = false
        roundPaused = false
        gameView.stop()
        handler.removeCallbacksAndMessages(null)
    }
}