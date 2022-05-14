package com.example.videodownloadingline.adaptor.viewpager_adaptor

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videodownloadingline.model.tabitem.TabItem
import com.example.videodownloadingline.utils.TAG

class ViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val getTotalFragment: MutableList<Fragment> = mutableListOf()
    private val getTabList: MutableList<TabItem> = mutableListOf()

    companion object {

        private var INSTANCE: ViewPagerAdapter? = null
        fun getInstance(fm: FragmentManager, lifecycle: Lifecycle): ViewPagerAdapter? {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = ViewPagerAdapter(fm, lifecycle)
                    Log.i(TAG, "getInstance: instance is Null $INSTANCE")
                    return INSTANCE
                }
                return INSTANCE
            }
        }
    }

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


    fun addTab(url: String?): Int {
        val size = getTabList.size
        getTabList.add(TabItem(size + 1, url))
        return getTabList.size
    }

    fun getTabList() = getTabList.toList()

    fun removedFragment(position: Int) {
        getTotalFragment.removeAt(position)
        Log.i(TAG, "removedFragment: $getTotalFragment")
    }


    fun getSize(): Int {
        Log.i(TAG, "getSize: ${getTotalFragment.size}")
        return getTotalFragment.size
    }
}