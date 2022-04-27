package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.databinding.CustomToolbarLayoutBinding
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavController
    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navHostFragment = navHost.findNavController()
        setUpBottomNav()
    }


    private fun setUpBottomNav() {
        val menuItem = arrayOf(
            CbnMenuItem(R.drawable.ic_loading, R.drawable.avd_anim_loading, R.id.progressFragment),
            CbnMenuItem(R.drawable.ic_homebtn, R.drawable.avd_home_anim, R.id.homeScrFragment),
            CbnMenuItem(
                R.drawable.ic_download,
                R.drawable.avd_anim_downloads,
                R.id.mainDownloadFragment
            )
        )
        binding.curBottomNav.setMenuItems(menuItem, 1)
        binding.curBottomNav.setupWithNavController(navHostFragment)
    }


    fun hideBottomNav(isHide: Boolean) {
        if (isHide)
            binding.curBottomNav.hide()
        else
            binding.curBottomNav.show()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar!!.title = getString(R.string.app_name)
    }

    @SuppressLint("RtlHardcoded")
    fun changeToolbar(
        totalTab: Int,
        url: String = "",
        listenForSearch: (txt: String) -> Unit,
        goTo: () -> Unit
    ) {
        val toolbarBinding: CustomToolbarLayoutBinding =
            CustomToolbarLayoutBinding.inflate(layoutInflater)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        val lp: ActionBar.LayoutParams =
            ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
        lp.gravity = Gravity.LEFT
        supportActionBar!!.setCustomView(toolbarBinding.root, lp)

        toolbarBinding.root.setContentInsetsAbsolute(0, 0)

        toolbarBinding.toolbarHomeBtn.setOnClickListener {
            goTo()
        }
        var searchText: String? = null
        toolbarBinding.searchBoxEd.doOnTextChanged { text, _, _, _ ->
            searchText = if (text.isNullOrEmpty()) {
                null
            } else {
                text.toString()
            }
        }
        toolbarBinding.searchBoxEd.setText(url)
        toolbarBinding.searchBoxEd.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER -> {
                        Log.i(TAG, "changeToolbar: $searchText")
                        if (!searchText.isNullOrEmpty()) {
                            listenForSearch(searchText!!)
                        }
                    }
                }
            }
            true
        }
        toolbarBinding.totalTabOp.text = String.format(
            Locale.getDefault(),
            "%d",
            totalTab
        )
    }
}