package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdaptor
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.utils.hideActionBar
import com.example.videodownloadingline.utils.show


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.hideActionBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adaptor = ViewPagerAdaptor(this) {
            binding.toolBarMainActivity.searchBoxEd.show()
        }

        binding.viewPager.adapter = adaptor
        binding.viewPager.isUserInputEnabled = false
        setCurrTab(1)
        bottomNav()
        binding.toolBarMainActivity.toolbarHomeBtn.setOnClickListener {
            setCurrTab(1)
            binding.bottomNavigation.selectedItemId = R.id.homeScrFragment
        }
    }

    private fun bottomNav() {
        binding.bottomNavigation.selectedItemId = R.id.homeScrFragment
        binding.bottomNavigation.getOrCreateBadge(R.id.progressFragment).apply {
            isVisible = true
            number = 12
        }
        binding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.homeScrFragment -> setCurrTab(1)
                R.id.progressFragment -> {
                    binding.bottomNavigation.getOrCreateBadge(item.itemId).apply {
                        clearNumber()
                        isVisible = false
                    }
                    setCurrTab(0)
                }
                R.id.downloadFragment -> setCurrTab(2)
            }
            return@setOnItemSelectedListener true
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun setCurrTab(tab: Int) {
        binding.viewPager.currentItem = tab
        binding.toolBarMainActivity.totalTabOp.apply {
            text = getString(
                R.string.num_of_tab,
                binding.viewPager.currentItem + 1
            )
        }
    }

    override fun onPause() {
        super.onPause()
        binding.viewPager.adapter = null
    }
}