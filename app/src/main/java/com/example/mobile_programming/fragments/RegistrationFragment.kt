package com.example.mobile_programming.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile_programming.R
import com.example.mobile_programming.models.Player
import java.util.Calendar
import kotlin.math.max

class RegistrationFragment : Fragment() {

    private var etFio: EditText? = null
    private var rgGender: RadioGroup? = null
    private var spCourse: Spinner? = null
    private var sbDifficulty: SeekBar? = null
    private var calendarBirth: CalendarView? = null
    private var tvResult: TextView? = null
    private var tvDifficultyLabel: TextView? = null
    private var ivZodiac: ImageView? = null

    private var birthDay = 0
    private var birthMonth = 0
    private var birthYear = 0

    // onCreateView — вызывается, чтобы фрагмент "надул" свой XML.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    // onViewCreated — когда View уже создан, тут находим элементы.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFio = view.findViewById(R.id.etFio)
        rgGender = view.findViewById(R.id.rgGender)
        spCourse = view.findViewById(R.id.spCourse)
        sbDifficulty = view.findViewById(R.id.sbDifficulty)
        calendarBirth = view.findViewById(R.id.calendarBirth)
        tvResult = view.findViewById(R.id.tvResult)
        tvDifficultyLabel = view.findViewById(R.id.tvDifficultyLabel)
        ivZodiac = view.findViewById(R.id.ivZodiac)
        val btnShow = view.findViewById<Button>(R.id.btnShow)

        val courses = arrayOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCourse!!.adapter = adapter

        sbDifficulty!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvDifficultyLabel!!.text = "Уровень сложности: ${max(progress, 1)}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        setDateFromMillis(calendarBirth!!.date)
        calendarBirth!!.setOnDateChangeListener { _, year, month, dayOfMonth ->
            birthYear = year
            birthMonth = month + 1
            birthDay = dayOfMonth
        }

        btnShow.setOnClickListener { showResult() }
    }

    private fun setDateFromMillis(millis: Long) {
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        birthYear = c.get(Calendar.YEAR)
        birthMonth = c.get(Calendar.MONTH) + 1
        birthDay = c.get(Calendar.DAY_OF_MONTH)
    }

    private fun showResult() {
        val player = Player()
        player.fio = etFio!!.text.toString().trim()
        player.gender = if (rgGender!!.checkedRadioButtonId == R.id.rbMale) "Мужской" else "Женский"
        player.course = spCourse!!.selectedItemPosition + 1
        player.difficulty = max(sbDifficulty!!.progress, 1)
        player.birthDate = String.format("%02d.%02d.%04d", birthDay, birthMonth, birthYear)
        player.zodiac = getZodiac(birthDay, birthMonth)

        tvResult!!.text = player.toString()

        val resId = getZodiacImage(player.zodiac!!)
        if (resId != 0) {
            ivZodiac!!.setImageResource(resId)
            ivZodiac!!.visibility = View.VISIBLE
        } else {
            ivZodiac!!.visibility = View.GONE
        }
    }

    private fun getZodiac(day: Int, month: Int): String = when (month) {
        1 -> if (day <= 19) "Козерог" else "Водолей"
        2 -> if (day <= 18) "Водолей" else "Рыбы"
        3 -> if (day <= 20) "Рыбы" else "Овен"
        4 -> if (day <= 19) "Овен" else "Телец"
        5 -> if (day <= 20) "Телец" else "Близнецы"
        6 -> if (day <= 20) "Близнецы" else "Рак"
        7 -> if (day <= 22) "Рак" else "Лев"
        8 -> if (day <= 22) "Лев" else "Дева"
        9 -> if (day <= 22) "Дева" else "Весы"
        10 -> if (day <= 22) "Весы" else "Скорпион"
        11 -> if (day <= 21) "Скорпион" else "Стрелец"
        12 -> if (day <= 21) "Стрелец" else "Козерог"
        else -> "Неизвестно"
    }

    private fun getZodiacImage(zodiac: String): Int {
        when (zodiac) {
            "Овен" -> return R.drawable.oven
            "Телец" -> return R.drawable.telets
            "Близнецы" -> return R.drawable.twin
            "Рак" -> return R.drawable.rak
            "Лев" -> return R.drawable.lev
            "Дева" -> return R.drawable.deva
            "Весы" -> return R.drawable.vesi
            "Скорпион" -> return R.drawable.skorpion
            "Стрелец" -> return R.drawable.strelets
            "Козерог" -> return R.drawable.kozerog
            "Водолей" -> return R.drawable.vodolaz
            "Рыбы" -> return R.drawable.mister_fish
            else -> return 0
        }
    }
}