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
import com.example.mobile_programming.MyApplication
import com.example.mobile_programming.R
import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.domain.repository.UserRepository
import kotlinx.coroutines.launch
import kotlin.text.clear

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

        val session = SessionManager(requireContext())

        val app = requireContext().applicationContext as MyApplication
        val repo: UserRepository = app.userRepository

        val userId = session.getUserId()

        viewLifecycleOwner.lifecycleScope.launch {
            val user = repo.getById(userId)
            if (user == null) {
                session.clear()
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
            session.clear()
            (activity as? MainActivity)?.showAuth()
        }
    }
}