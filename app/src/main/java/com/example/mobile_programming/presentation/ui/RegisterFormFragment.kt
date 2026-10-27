package com.example.mobile_programming.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile_programming.R
import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.domain.repository.UserRepository
import com.example.mobile_programming.domain.util.Zodiac
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

class RegisterFormFragment : Fragment(), KoinComponent {

    private val userRepository: UserRepository by inject()
    private val sessionManager: SessionManager by inject()

    private var birthDay = 1
    private var birthMonth = 1
    private var birthYear = 2000
    private lateinit var tvZodiac: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_register_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etLogin = view.findViewById<EditText>(R.id.etRegLogin)
        val etPassword = view.findViewById<EditText>(R.id.etRegPassword)
        val etFio = view.findViewById<EditText>(R.id.etRegFio)
        val rgGender = view.findViewById<RadioGroup>(R.id.rgRegGender)
        val spCourse = view.findViewById<Spinner>(R.id.spRegCourse)
        val calendarBirth = view.findViewById<CalendarView>(R.id.calendarRegBirth)
        tvZodiac = view.findViewById(R.id.tvRegZodiac)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        val courses = arrayOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCourse.adapter = adapter

        setDateFromMillis(calendarBirth.date)
        updateZodiacLabel()
        calendarBirth.setOnDateChangeListener { _, year, month, day ->
            birthYear = year
            birthMonth = month + 1
            birthDay = day
            updateZodiacLabel()
        }

        btnRegister.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val fio = etFio.text.toString().trim()
            val gender = if (rgGender.checkedRadioButtonId == R.id.rbRegMale) "Мужской" else "Женский"
            val course = spCourse.selectedItemPosition + 1
            val birthDate = String.format("%02d.%02d.%04d", birthDay, birthMonth, birthYear)
            val zodiac = Zodiac.fromDate(birthDay, birthMonth)

            if (login.isEmpty() || password.isEmpty() || fio.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                userRepository.register(login, password, fio, gender, course, birthDate, zodiac)
                    .onSuccess { id ->
                        sessionManager.saveUserId(id)
                        Toast.makeText(requireContext(), "Успешно", Toast.LENGTH_SHORT).show()
                        (parentFragment as? AuthFragment)?.onAuthSuccess()
                    }
                    .onFailure { e ->
                        Toast.makeText(requireContext(), e.message ?: "Ошибка", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setDateFromMillis(millis: Long) {
        val c = Calendar.getInstance().apply { timeInMillis = millis }
        birthYear = c.get(Calendar.YEAR)
        birthMonth = c.get(Calendar.MONTH) + 1
        birthDay = c.get(Calendar.DAY_OF_MONTH)
    }

    private fun updateZodiacLabel() {
        tvZodiac.text = "Знак зодиака: ${Zodiac.fromDate(birthDay, birthMonth)}"
    }
}