package com.example.mobile_programming.fragments

import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
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

}