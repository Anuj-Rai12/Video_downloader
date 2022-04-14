package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdaptor
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.utils.hideActionBar
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var adaptor: ViewPagerAdaptor? = null
    private lateinit var navController: NavController

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
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()
        setUpBottomNav()
    }


    private fun setUpBottomNav() {
        val menuItem = arrayOf(
            CbnMenuItem(R.drawable.ic_loading, R.drawable.avd_anim_loading, R.id.progressFragment),
            CbnMenuItem(R.drawable.ic_homebtn, R.drawable.avd_home_anim, R.id.homeScrFragment),
            CbnMenuItem(
                R.drawable.ic_download,
                R.drawable.avd_anim_downloads,
                R.id.downloadFragment
            )
        )
        binding.curBottomNav.setMenuItems(menuItem, 1)
        binding.curBottomNav.setupWithNavController(navController)
    }

}