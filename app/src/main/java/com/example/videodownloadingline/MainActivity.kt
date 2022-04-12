package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdaptor
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.ui.DownloadFragment
import com.example.videodownloadingline.ui.HomeScrFragment
import com.example.videodownloadingline.ui.ProgressFragment
import com.example.videodownloadingline.ui.SetPinLayoutFragment
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.hideActionBar
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentScr: Int = 1
    private var adaptor: ViewPagerAdaptor? = null

    companion object {
        var bottomNavigation: CurvedBottomNavigationView? = null//BottomNavigationView? = null
        var viewPager2: ViewPager2? = null
    }

    fun setFragment(fragment: Fragment): Int? {
        adaptor?.setFragment(fragment)
        return adaptor?.getSize()
    }

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.hideActionBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savedInstanceState?.let {
            currentScr = it.getInt(getString(R.string.num_of_tab))
            viewPagerImpl()
            binding.viewPager.currentItem = currentScr
            Log.i(
                TAG, "onCreate: OnSaveInstance $currentScr \n" +
                        "And ViewPager is =>${binding.viewPager.currentItem}"
            )
        }
        setUpBottomNav()
        if (currentScr == 1) {
            viewPagerImpl()
            setCurrTab(currentScr)
        }
        Log.i(
            TAG,
            "onCreate: Main Activity  With out Save Instance $currentScr \nAnd ViewPager is =>${binding.viewPager.currentItem}"
        )
    }

    private fun viewPagerImpl() {
        adaptor=ViewPagerAdaptor(this)
        setFragment(ProgressFragment())
        setFragment(HomeScrFragment())
        setFragment(DownloadFragment())
        setFragment(SetPinLayoutFragment())
        viewPager2 = binding.viewPager
        bottomNavigation = binding.curBottomNav

        binding.viewPager.adapter = adaptor
        binding.viewPager.isUserInputEnabled = false
    }

    private fun setUpBottomNav() {
        val menuItem = arrayOf(
            CbnMenuItem(R.drawable.ic_loading, R.drawable.avd_anim_loading),
            CbnMenuItem(R.drawable.ic_homebtn, R.drawable.avd_home_anim),
            CbnMenuItem(R.drawable.ic_download, R.drawable.avd_anim_downloads)
        )
        binding.curBottomNav.setMenuItems(menuItem, currentScr)

        binding.curBottomNav.setOnMenuItemClickListener { _, index ->
            binding.viewPager.currentItem = index
            currentScr = index
        }

    }

    @SuppressLint("StringFormatMatches")
    private fun setCurrTab(tab: Int) {
        binding.viewPager.currentItem = tab
        currentScr = tab
    }

    override fun onPause() {
        super.onPause()
        binding.viewPager.adapter = null
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        viewPagerImpl()
        binding.viewPager.currentItem =
            savedInstanceState.getInt(getString(R.string.num_of_tab))
        Log.i(TAG, "onRestoreInstanceState: ViewPager is => ${binding.viewPager.currentItem}")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(getString(R.string.num_of_tab), currentScr)
        super.onSaveInstanceState(outState)
    }
}