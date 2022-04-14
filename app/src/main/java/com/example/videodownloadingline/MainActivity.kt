package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdaptor
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.databinding.ToolbarLayoutBinding
import com.example.videodownloadingline.ui.*
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentScr: Int = 1
    private var adaptor: ViewPagerAdaptor? = null
    private lateinit var toolbarbinding: ToolbarLayoutBinding
    private lateinit var navHostFragment: NavController

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navHostFragment = navHost.findNavController()

        /*savedInstanceState?.let {
            currentScr = it.getInt(getString(R.string.num_of_tab))
            viewPagerImpl()
            binding.viewPager.currentItem = currentScr
            Log.i(
                TAG, "onCreate: OnSaveInstance $currentScr \n" +
                        "And ViewPager is =>${binding.viewPager.currentItem}"
            )
        }*/
        setUpBottomNav()
        /*if (currentScr == 1) {
            viewPagerImpl()
            setCurrTab(currentScr)
        }
        Log.i(
            TAG,
            "onCreate: Main Activity  With out Save Instance $currentScr \nAnd ViewPager is =>${binding.viewPager.currentItem}"
        )*/
    }

    /*private fun viewPagerImpl() {
        adaptor = ViewPagerAdaptor(this)
        setFragment(ProgressFragment())
        setFragment(HomeScrFragment())
        setFragment(DownloadFragment())
        setFragment(SetPinLayoutFragment())
        //viewPager2 = binding.viewPager
        bottomNavigation = binding.curBottomNav

        binding.viewPager.adapter = adaptor
        binding.viewPager.isUserInputEnabled = false
    }*/

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
        binding.curBottomNav.setMenuItems(menuItem, currentScr)
        binding.curBottomNav.setupWithNavController(navHostFragment)
    }

/*    @SuppressLint("StringFormatMatches")
    private fun setCurrTab(tab: Int) {
        binding.viewPager.currentItem = tab
        currentScr = tab
    }*/


    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar!!.title = "Video Downloader"
    }

    @SuppressLint("RtlHardcoded")
    fun changeToolbar() {
        val toolbarBinding: ToolbarLayoutBinding =
            ToolbarLayoutBinding.inflate(layoutInflater)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        val lp: ActionBar.LayoutParams =
            ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.LEFT
        supportActionBar!!.setCustomView(toolbarBinding.root, lp)

        toolbarBinding.root.setContentInsetsAbsolute(0, 0)

        var searchText: String? = null
        toolbarBinding.searchBoxEd.doOnTextChanged { text, _, _, _ ->
            searchText = if (text.isNullOrEmpty()) {
                null
            } else {
                text.toString()
            }
        }
        toolbarBinding.searchBoxEd.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER -> {
                        if (!searchText.isNullOrEmpty()) {
                            setFragment(WebViewFragments(searchText!!))
                                ?.let {
                                    viewPager2?.currentItem = it - 1
                                }
                        }
                    }
                }
            }
            true
        }
        toolbarBinding.totalTabOp.text = String.format(
            Locale.getDefault(),
            "%d",
            2322
        )
    }

}