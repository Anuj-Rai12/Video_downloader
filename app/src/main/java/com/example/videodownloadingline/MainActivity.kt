package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdaptor
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.hideActionBar
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentScr: Int = 1

    companion object {
        var bottomNavigation: CurvedBottomNavigationView? = null//BottomNavigationView? = null
        var viewPager2: ViewPager2? = null
    }

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.hideActionBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savedInstanceState?.let {
            currentScr = it.getInt(getString(R.string.num_of_tab))
            Log.i(TAG, "onCreate: Main Activity $currentScr")
        }
        setUpBottomNav()
        val adaptor = ViewPagerAdaptor(this)
        viewPager2 = binding.viewPager
        bottomNavigation = binding.curBottomNav

        binding.viewPager.adapter = adaptor
        binding.viewPager.isUserInputEnabled = false
        setCurrTab(currentScr)
    }

    private fun setUpBottomNav() {
        val menuItem = arrayOf(
            CbnMenuItem(R.drawable.ic_newloading, R.drawable.avd_loading),
            CbnMenuItem(R.drawable.ic_homebtn, R.drawable.avd_home_anim),
            CbnMenuItem(R.drawable.ic_newdownload, R.drawable.avd_downloading)
        )
        binding.curBottomNav.setMenuItems(menuItem, 1)

        binding.curBottomNav.setOnMenuItemClickListener { _, index ->
            binding.viewPager.currentItem = index
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(getString(R.string.num_of_tab), currentScr)
    }
}