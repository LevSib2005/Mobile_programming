package com.example.mobile_programming

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import kotlin.math.max


class MainActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etFio = findViewById<EditText?>(R.id.etFio)
        rgGender = findViewById<RadioGroup?>(R.id.rgGender)
        spCourse = findViewById<Spinner?>(R.id.spCourse)
        sbDifficulty = findViewById<SeekBar?>(R.id.sbDifficulty)
        calendarBirth = findViewById<CalendarView?>(R.id.calendarBirth)
        tvResult = findViewById<TextView?>(R.id.tvResult)
        tvDifficultyLabel = findViewById<TextView?>(R.id.tvDifficultyLabel)
        ivZodiac = findViewById<ImageView?>(R.id.ivZodiac)
        val btnShow = findViewById<Button?>(R.id.btnShow)

        val courses = arrayOf<String?>("1 курс", "2 курс", "3 курс", "4 курс")
        val adapter = ArrayAdapter<String?>(
            this,
            android.R.layout.simple_spinner_item,
            courses
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCourse!!.setAdapter(adapter)

        sbDifficulty!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvDifficultyLabel!!.setText("Уровень сложности: " + max(progress, 1))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        setDateFromMillis(calendarBirth!!.getDate())
        calendarBirth!!.setOnDateChangeListener(OnDateChangeListener { view: CalendarView?, year: Int, month: Int, dayOfMonth: Int ->
            birthYear = year
            birthMonth = month + 1
            birthDay = dayOfMonth
        })

        btnShow.setOnClickListener(View.OnClickListener { v: View? -> showResult() })
    }

    private fun setDateFromMillis(millis: Long) {
        val c = Calendar.getInstance()
        c.setTimeInMillis(millis)
        birthYear = c.get(Calendar.YEAR)
        birthMonth = c.get(Calendar.MONTH) + 1
        birthDay = c.get(Calendar.DAY_OF_MONTH)
    }

    private fun showResult() {
        val player = Player()
        player.fio = etFio!!.getText().toString().trim { it <= ' ' }
        player.gender =
            if (rgGender!!.getCheckedRadioButtonId() == R.id.rbMale) "Мужской" else "Женский"
        player.course = spCourse!!.getSelectedItemPosition() + 1
        player.difficulty = max(sbDifficulty!!.getProgress(), 1)
        player.birthDate = String.format("%02d.%02d.%04d", birthDay, birthMonth, birthYear)
        player.zodiac = getZodiac(birthDay, birthMonth)

        tvResult!!.setText(player.toString())

        val resId = getZodiacImage(player.zodiac!!)
        if (resId != 0) {
            ivZodiac!!.setImageResource(resId)
            ivZodiac!!.setVisibility(ImageView.VISIBLE)
        } else {
            ivZodiac!!.setVisibility(ImageView.GONE)
        }
    }

    private fun getZodiac(day: Int, month: Int): String {
        when (month) {
            1 -> return if (day <= 19) "Козерог" else "Водолей"
            2 -> return if (day <= 18) "Водолей" else "Рыбы"
            3 -> return if (day <= 20) "Рыбы" else "Овен"
            4 -> return if (day <= 19) "Овен" else "Телец"
            5 -> return if (day <= 20) "Телец" else "Близнецы"
            6 -> return if (day <= 20) "Близнецы" else "Рак"
            7 -> return if (day <= 22) "Рак" else "Лев"
            8 -> return if (day <= 22) "Лев" else "Дева"
            9 -> return if (day <= 22) "Дева" else "Весы"
            10 -> return if (day <= 22) "Весы" else "Скорпион"
            11 -> return if (day <= 21) "Скорпион" else "Стрелец"
            12 -> return if (day <= 21) "Стрелец" else "Козерог"
            else -> return "Неизвестно"
        }
    }

    private fun getZodiacImage(zodiac: String): Int {
        when (zodiac) {
            "Овен" -> return R.drawable.oven
//            "Телец" -> return R.drawable.taurus
//            "Близнецы" -> return R.drawable.gemini
//            "Рак" -> return R.drawable.cancer
//            "Лев" -> return R.drawable.leo
//            "Дева" -> return R.drawable.virgo
//            "Весы" -> return R.drawable.libra
//            "Скорпион" -> return R.drawable.scorpio
//            "Стрелец" -> return R.drawable.sagittarius
//            "Козерог" -> return R.drawable.capricorn
//            "Водолей" -> return R.drawable.aquarius
//            "Рыбы" -> return R.drawable.pisces
            else -> return 0
        }
    }
}