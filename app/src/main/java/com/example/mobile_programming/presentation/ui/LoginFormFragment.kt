package com.example.mobile_programming.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile_programming.MyApplication
import com.example.mobile_programming.R
import com.example.mobile_programming.data.local.SessionManager
import kotlinx.coroutines.launch

class LoginFormFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_login_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireContext().applicationContext as MyApplication
        val repo = app.userRepository
        val session = SessionManager(requireContext())

        val etLogin = view.findViewById<EditText>(R.id.etLoginLogin)
        val etPassword = view.findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                toast("Введите логин и пароль")
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repo.login(login, password)
                    .onSuccess { user ->
                        session.saveUserId(user.id)
                        toast("Добро пожаловать, ${user.fio}")
                        (parentFragment as? AuthFragment)?.onAuthSuccess()
                    }
                    .onFailure { e ->
                        toast(e.message ?: "Ошибка входа")
                    }
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}