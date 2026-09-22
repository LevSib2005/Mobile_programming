package com.example.mobile_programming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mobile_programming.fragments.AuthorsFragment
import com.example.mobile_programming.fragments.RegistrationFragment
import com.example.mobile_programming.fragments.RulesFragment
import com.example.mobile_programming.fragments.SettingsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val tabTitles = listOf("Регистрация", "Правила", "Авторы", "Настройки")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = TabsAdapter()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private inner class TabsAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = tabTitles.size

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RegistrationFragment()
                1 -> RulesFragment()
                2 -> AuthorsFragment()
                3 -> SettingsFragment()
                else -> RegistrationFragment()
            }
        }
    }
}