package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdaptor
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.hideActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentScr: Int = 1

    companion object {
        var bottomNavigation: BottomNavigationView? = null
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
        val adaptor = ViewPagerAdaptor(this)
        viewPager2 = binding.viewPager
        bottomNavigation = binding.bottomNavigation

        binding.viewPager.adapter = adaptor
        binding.viewPager.isUserInputEnabled = false
        setCurrTab(currentScr)
        binding.bottomNavigation.selectedItemId = R.id.homeScrFragment
        bottomNav()
    }

    private fun bottomNav() {
        binding.bottomNavigation.selectedItemId = R.id.homeScrFragment
        binding.bottomNavigation.getOrCreateBadge(R.id.progressFragment).apply {
            isVisible = true
            number = 12
        }
        binding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.homeScrFragment -> {
                    setCurrTab(1)
                }
                R.id.progressFragment -> {
                    binding.bottomNavigation.getOrCreateBadge(item.itemId).apply {
                        clearNumber()
                        isVisible = false
                    }
                    setCurrTab(0)
                }
                R.id.downloadFragment -> {
                    setCurrTab(2)
                }
            }
            return@setOnItemSelectedListener true
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