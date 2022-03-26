package com.example.videodownloadingline.adaptor.viewpager_adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videodownloadingline.ui.DownloadFragment
import com.example.videodownloadingline.ui.HomeScrFragment
import com.example.videodownloadingline.ui.ProgressFragment

class ViewPagerAdaptor(
    fm: FragmentActivity,
    private val item: () -> Unit
) : FragmentStateAdapter(fm) {
    val getTotalFragment: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        //return getTotalFragment[position]
        return when (position) {
            0 -> HomeScrFragment {
                item()
            }
            1 -> ProgressFragment()
            2 -> DownloadFragment()
            else -> throw  IllegalArgumentException("There is Not Fragment At $position")
        }
    }

}