package com.example.mobile_programming.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile_programming.MainActivity
import com.example.mobile_programming.R
import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.domain.repository.UserRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileFragment : Fragment(), KoinComponent {

    private val userRepository: UserRepository by inject()
    private val sessionManager: SessionManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvLogin = view.findViewById<TextView>(R.id.tvProfileLogin)
        val tvFio = view.findViewById<TextView>(R.id.tvProfileFio)
        val tvGender = view.findViewById<TextView>(R.id.tvProfileGender)
        val tvCourse = view.findViewById<TextView>(R.id.tvProfileCourse)
        val tvBirth = view.findViewById<TextView>(R.id.tvProfileBirth)
        val tvZodiac = view.findViewById<TextView>(R.id.tvProfileZodiac)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val userId = sessionManager.getUserId()

        viewLifecycleOwner.lifecycleScope.launch {
            val user = userRepository.getById(userId)
            if (user == null) {
                sessionManager.clear()
                (activity as? MainActivity)?.showAuth()
                return@launch
            }
            tvLogin.text = "Логин: ${user.login}"
            tvFio.text = "ФИО: ${user.fio}"
            tvGender.text = "Пол: ${user.gender}"
            tvCourse.text = "Курс: ${user.course}"
            tvBirth.text = "Дата рождения: ${user.birthDate}"
            tvZodiac.text = "Знак зодиака: ${user.zodiac}"
        }

        btnLogout.setOnClickListener {
            sessionManager.clear()
            (activity as? MainActivity)?.showAuth()
        }
    }
}