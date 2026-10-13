package com.example.mobile_programming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.mobile_programming.data.local.SessionManager
import com.example.mobile_programming.presentation.ui.AuthFragment
import com.example.mobile_programming.presentation.ui.MainTabsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        session = SessionManager(this)

        if (savedInstanceState == null) {
            if (session.isLoggedIn()) showMainContent() else showAuth()
        }
    }

    fun showMainContent() {
        supportFragmentManager.commit {
            replace(R.id.mainContainer, MainTabsFragment())
        }
    }

    fun showAuth() {
        supportFragmentManager.commit {
            replace(R.id.mainContainer, AuthFragment())
        }
    }
}