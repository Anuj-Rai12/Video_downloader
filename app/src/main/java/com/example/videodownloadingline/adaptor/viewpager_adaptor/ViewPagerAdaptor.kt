package com.example.videodownloadingline.adaptor.viewpager_adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdaptor(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    private val getTotalFragment: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int {
        return getTotalFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return getTotalFragment[position]
        /*return when (position) {
            0 -> ProgressFragment()
            1 -> HomeScrFragment()
            2 -> DownloadFragment()
            else -> throw  IllegalArgumentException("There is Not Fragment At $position")
        }*/
    }

    fun setFragment(fragment: Fragment) {
        getTotalFragment.add(fragment)
    }
}