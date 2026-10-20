package com.example.mobile_programming.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile_programming.MyApplication
import com.example.mobile_programming.R
import com.example.mobile_programming.data.local.SessionManager
import kotlinx.coroutines.launch

class RecordsFragment : Fragment() {

    private lateinit var topContainer: LinearLayout
    private lateinit var tvTopTitle: TextView
    private lateinit var tvPersonal: TextView

    private lateinit var app: MyApplication
    private var userId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_records, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app = requireContext().applicationContext as MyApplication
        val session = SessionManager(requireContext())
        userId = session.getUserId()

        topContainer = view.findViewById(R.id.topContainer)
        tvTopTitle = view.findViewById(R.id.tvTopTitle)
        tvPersonal = view.findViewById(R.id.tvPersonalRecord)
        val spFilter: Spinner = view.findViewById(R.id.spDifficultyFilter)

        val options = mutableListOf("Все сложности")
        for (i in 1..10) options.add("Скорость $i")

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            options
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFilter.adapter = spinnerAdapter

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, v: View?, position: Int, id: Long
            ) {
                if (position == 0) {
                    tvTopTitle.text = "Топ-3 игроков"
                    loadTop3(null)
                } else {
                    tvTopTitle.text = "Топ-3 на скорости $position"
                    loadTop3(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        loadPersonal()
    }

    private fun loadTop3(difficulty: Int?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val top = if (difficulty == null) {
                app.scoreRepository.getTop(3)
            } else {
                app.scoreRepository.getTopByDifficulty(3, difficulty)
            }

            topContainer.removeAllViews()
            if (top.isEmpty()) {
                val tv = TextView(requireContext())
                tv.text = "Пока нет рекордов"
                topContainer.addView(tv)
                return@launch
            }
            top.forEachIndexed { index, record ->
                val item = layoutInflater.inflate(R.layout.item_record, topContainer, false)
                item.findViewById<TextView>(R.id.tvPlace).text = "${index + 1}."
                item.findViewById<TextView>(R.id.tvName).text = record.playerName
                item.findViewById<TextView>(R.id.tvScore).text = "${record.score}"
                item.findViewById<TextView>(R.id.tvSettings).text =
                    "Скорость ${record.difficulty} · Раунд ${record.roundTime} сек"
                topContainer.addView(item)
            }
        }
    }

    private fun loadPersonal() {
        if (userId <= 0) {
            tvPersonal.text = "Вы не авторизованы"
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val personal = app.scoreRepository.getPersonal(userId)
            tvPersonal.text = if (personal == null) {
                "У вас пока нет рекорда"
            } else {
                "Ваш лучший результат: ${personal.score} очков\n" +
                        "Скорость ${personal.difficulty} · Раунд ${personal.roundTime} сек"
            }
        }
    }
}