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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobile_programming.R
import com.example.mobile_programming.presentation.records.RecordsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecordsFragment : Fragment() {

    private val viewModel: RecordsViewModel by viewModel()

    private lateinit var topContainer: LinearLayout
    private lateinit var tvTopTitle: TextView
    private lateinit var tvPersonal: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_records, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topContainer = view.findViewById(R.id.topContainer)
        tvTopTitle = view.findViewById(R.id.tvTopTitle)
        tvPersonal = view.findViewById(R.id.tvPersonalRecord)
        val spFilter: Spinner = view.findViewById(R.id.spDifficultyFilter)

        val options = mutableListOf("Все сложности")
        for (i in 1..10) options.add("Скорость $i")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFilter.adapter = spinnerAdapter

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                if (position == 0) {
                    tvTopTitle.text = "Топ-3 игроков"
                    viewModel.loadTop3(null)
                } else {
                    tvTopTitle.text = "Топ-3 на скорости $position"
                    viewModel.loadTop3(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.loadPersonal()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.topRecords.collect { top ->
                        topContainer.removeAllViews()
                        if (top.isEmpty()) {
                            val tv = TextView(requireContext()).apply { text = "Пока нет рекордов" }
                            topContainer.addView(tv)
                            return@collect
                        }
                        top.forEachIndexed { index, record ->
                            val item = layoutInflater.inflate(R.layout.item_record, topContainer, false)
                            item.findViewById<TextView>(R.id.tvPlace).text = "${index + 1}."
                            item.findViewById<TextView>(R.id.tvName).text = record.playerName
                            item.findViewById<TextView>(R.id.tvScore).text = "${record.score}"
                            item.findViewById<TextView>(R.id.tvSettings).text = "Скорость ${record.difficulty} · Раунд ${record.roundTime} сек"
                            topContainer.addView(item)
                        }
                    }
                }
                launch {
                    viewModel.personalRecord.collect { personal ->
                        tvPersonal.text = if (personal == null) {
                            "У вас пока нет рекорда"
                        } else {
                            "Ваш лучший результат: ${personal.score} очков\nСкорость ${personal.difficulty} · Раунд ${personal.roundTime} сек"
                        }
                    }
                }
            }
        }
    }
}