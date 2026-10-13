package com.example.mobile_programming.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mobile_programming.MainActivity
import com.example.mobile_programming.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabAuth: TabLayout = view.findViewById(R.id.tabAuth)
        val viewPager: ViewPager2 = view.findViewById(R.id.authViewPager)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int): Fragment =
                if (position == 0) LoginFormFragment() else RegisterFormFragment()
        }

        TabLayoutMediator(tabAuth, viewPager) { tab, position ->
            tab.text = if (position == 0) "Вход" else "Регистрация"
        }.attach()
    }

    fun onAuthSuccess() {
        (activity as? MainActivity)?.showMainContent()
    }
}