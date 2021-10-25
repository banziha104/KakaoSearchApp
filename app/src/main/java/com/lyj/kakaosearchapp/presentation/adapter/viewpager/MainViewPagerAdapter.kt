package com.lyj.kakaosearchapp.presentation.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lyj.kakaosearchapp.presentation.activity.MainTabType

class MainViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager,lifecycle) {
    private val tabTypes: Array<MainTabType> = MainTabType.values()

    override fun getItemCount(): Int = tabTypes.size

    override fun createFragment(position: Int): Fragment = tabTypes[position].fragment
}