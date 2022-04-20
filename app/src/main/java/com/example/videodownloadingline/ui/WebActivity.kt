package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdapter
import com.example.videodownloadingline.databinding.ActivityWebBinding
import com.example.videodownloadingline.databinding.CustomToolbarLayoutBinding
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.view_model.MainViewModel
import java.util.*

class WebActivity : AppCompatActivity() {
    private val args: WebActivityArgs by navArgs()
    private lateinit var binding: ActivityWebBinding
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private val mainViewModel: MainViewModel? by lazy {
        MainViewModel.getInstance()
    }

    companion object {
        var viewPager: ViewPager2? = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = binding.mainWebViewViewPager
        viewPagerAdapter = ViewPagerAdapter(this)
        setFragment(WebViewFragments(args.name, args.url))
        //binding.mainWebViewViewPager.adapter = viewPagerAdapter
        binding.mainWebViewViewPager.isUserInputEnabled = false
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.web_frag_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        val newTab = menu?.findItem(R.id.new_tab_option_mnu)

        newTab?.setOnMenuItemClickListener {
            mainViewModel?.addMoreTab()
            val size = setFragment(HomeScrFragment(true))
            Log.i(TAG, "onCreateOptionsMenu: $size")
            binding.mainWebViewViewPager.currentItem = size!! - 1
            return@setOnMenuItemClickListener true
        }
        return true
    }


    fun setFragment(fragment: Fragment): Int? {
        viewPagerAdapter?.setFragment(fragment)
        binding.mainWebViewViewPager.adapter = viewPagerAdapter
        return viewPagerAdapter?.getSize()
    }

    fun removeFragment(position:Int): Int? {
        viewPagerAdapter?.removedFragment(position)
        return viewPagerAdapter?.getSize()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
    }

    @SuppressLint("RtlHardcoded")
    fun changeToolbar(totalTab: Int, url: String, listenForSearch: (txt: String) -> Unit) {
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
        toolbarBinding.searchBoxEd.setText(url)
        var searchText: String? = url
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
                            listenForSearch(searchText!!)
                        }
                    }
                }
            }
            true
        }
        toolbarBinding.toolbarHomeBtn.setOnClickListener {
            onBackPressed()
        }
        toolbarBinding.totalTabOp.text = String.format(
            Locale.getDefault(),
            "%d",
            totalTab
        )
    }
}