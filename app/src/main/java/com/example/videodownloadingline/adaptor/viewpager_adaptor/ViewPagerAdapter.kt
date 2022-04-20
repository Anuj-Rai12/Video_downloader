package com.example.videodownloadingline.adaptor.viewpager_adaptor

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videodownloadingline.utils.TAG

class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {

    private val getTotalFragment: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int {
        return getTotalFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return getTotalFragment[position]
    }

    fun setFragment(fragment: Fragment) {
        getTotalFragment.add(fragment)
        Log.i(TAG, "setFragment: $getTotalFragment")
    }

    fun removedFragment(position: Int) {
        getTotalFragment.removeAt(position)
        Log.i(TAG, "removedFragment: $getTotalFragment")
    }


    fun getSize(): Int {
        Log.i(TAG, "getSize: ${getTotalFragment.size}")
        return getTotalFragment.size
    }
}