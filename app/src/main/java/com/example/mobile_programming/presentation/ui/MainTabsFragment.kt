package com.example.mobile_programming.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mobile_programming.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainTabsFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val tabTitles = listOf("Профиль", "Правила", "Игра", "Авторы", "Настройки", "Рекорды")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main_tabs, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        viewPager.adapter = TabsAdapter()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    fun setGameMode(gameMode: Boolean) {
        tabLayout.visibility = if (gameMode) View.GONE else View.VISIBLE
        viewPager.isUserInputEnabled = !gameMode
    }

    private inner class TabsAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = tabTitles.size

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ProfileFragment()
                1 -> RulesFragment()
                2 -> GameFragment()
                3 -> AuthorsFragment()
                4 -> SettingsFragment()
                5 -> RecordsFragment()
                else -> ProfileFragment()
            }
        }
    }
}