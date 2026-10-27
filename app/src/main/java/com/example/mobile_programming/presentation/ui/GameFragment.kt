package com.example.mobile_programming.presentation.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobile_programming.R
import com.example.mobile_programming.domain.models.Bug
import com.example.mobile_programming.presentation.game.GamePhase
import com.example.mobile_programming.presentation.game.GameUiState
import com.example.mobile_programming.presentation.game.GameViewModel
import com.example.mobile_programming.presentation.ui.views.GameView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModel()

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

    private lateinit var soundPool: SoundPool
    private var screamSoundId = 0
    private var screamStreamId = 0

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var currentPhase: GamePhase? = null

    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (viewModel.state.value.phase == GamePhase.Paused) viewModel.resumeRound()
            else viewModel.pauseRound()
        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                gameView.setGravity(-event.values[0], -event.values[1])
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val spawnRunnable = object : Runnable {
        override fun run() {
            if (viewModel.state.value.phase != GamePhase.Running) return
            viewModel.spawnBug()
            gameView.postDelayed(this, 700L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        initSound()
        initSensor()

        gameView.setBugs(viewModel.bugs)

        gameView.onBugKilled = { bug: Bug ->
            viewModel.onBugKilled(bug.points)
        }
        gameView.onMiss = { viewModel.onMiss() }
        gameView.onBonusClicked = {
            screamStreamId = soundPool.play(screamSoundId, 1f, 1f, 1, -1, 1f)
            Toast.makeText(requireContext(), "Гравитация на 5 секунд!", Toast.LENGTH_SHORT).show()
        }
        gameView.onGravityEnded = { stopScream() }

        btnStart.setOnClickListener { startNewRound() }
        btnPlayAgain.setOnClickListener { startNewRound() }
        btnPause.setOnClickListener { viewModel.pauseRound() }
        btnResume.setOnClickListener { viewModel.resumeRound() }
        btnRestart.setOnClickListener { startNewRound() }
        btnExit.setOnClickListener { exitToMenu() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { renderState(it) }
            }
        }
    }

    private fun initSound() {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attrs).build()
        screamSoundId = soundPool.load(requireContext(), R.raw.bug_scream, 1)
    }

    private fun initSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun renderState(state: GameUiState) {
        tvScore.text = "Очки: ${state.score}"
        tvTimer.text = "${state.timeLeft} сек"
        gameView.setGoldRate(state.goldRate)

        if (state.phase != currentPhase) {
            val prev = currentPhase
            currentPhase = state.phase
            onPhaseChanged(prev, state.phase, state)
        }
    }

    private fun onPhaseChanged(prev: GamePhase?, new: GamePhase, state: GameUiState) {
        when (new) {
            GamePhase.Idle -> {
                gameView.stop()
                gameView.removeCallbacks(spawnRunnable)
                gameView.resetBonusAndGravity()
                startOverlay.visibility = View.VISIBLE
                resultOverlay.visibility = View.GONE
                pauseOverlay.visibility = View.GONE
                stopScream()
                (parentFragment as? MainTabsFragment)?.setGameMode(false)
                backCallback.isEnabled = false
            }
            GamePhase.Running -> {
                startOverlay.visibility = View.GONE
                resultOverlay.visibility = View.GONE
                pauseOverlay.visibility = View.GONE
                gameView.start()
                gameView.removeCallbacks(spawnRunnable)
                gameView.post(spawnRunnable)
                (parentFragment as? MainTabsFragment)?.setGameMode(true)
                backCallback.isEnabled = true
            }
            GamePhase.Paused -> {
                gameView.stop()
                gameView.removeCallbacks(spawnRunnable)
                startOverlay.visibility = View.GONE
                resultOverlay.visibility = View.GONE
                pauseOverlay.visibility = View.VISIBLE
                stopScream()
                (parentFragment as? MainTabsFragment)?.setGameMode(true)
                backCallback.isEnabled = true
            }
            GamePhase.Ended -> {
                gameView.stop()
                gameView.removeCallbacks(spawnRunnable)
                startOverlay.visibility = View.GONE
                pauseOverlay.visibility = View.GONE
                resultOverlay.visibility = View.VISIBLE
                tvResultScore.text = "Очки: ${state.score}"
                tvResultHits.text = "Попаданий: ${state.hits}"
                tvResultMisses.text = "Промахов: ${state.misses}"
                tvResultAccuracy.text = "Точность: ${state.accuracy}%"
                stopScream()
                (parentFragment as? MainTabsFragment)?.setGameMode(false)
                backCallback.isEnabled = false
            }
        }
    }

    private fun startNewRound() {
        gameView.removeCallbacks(spawnRunnable)
        gameView.resetBonusAndGravity()
        viewModel.startRound()
    }

    private fun exitToMenu() {
        gameView.removeCallbacks(spawnRunnable)
        gameView.resetBonusAndGravity()
        viewModel.exitToMenu()
    }

    private fun stopScream() {
        if (screamStreamId != 0) {
            soundPool.stop(screamStreamId)
            screamStreamId = 0
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)

        if (!requireActivity().isChangingConfigurations) {
            viewModel.pauseRound()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameView.stop()
        gameView.removeCallbacks(spawnRunnable)
        stopScream()
        soundPool.release()
    }
}