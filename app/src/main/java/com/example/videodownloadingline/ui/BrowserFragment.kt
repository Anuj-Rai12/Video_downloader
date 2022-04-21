package com.example.videodownloadingline.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdapter
import com.example.videodownloadingline.databinding.ActivityWebBinding
import com.example.videodownloadingline.view_model.MainViewModel

class BrowserFragment : Fragment(R.layout.activity_web) {
    private lateinit var binding: ActivityWebBinding
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var mainViewModel: MainViewModel? = null
    private val args: BrowserFragmentArgs by navArgs()

    companion object {
        var viewPager: ViewPager2? = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityWebBinding.bind(view)
        viewPager = binding.mainWebViewViewPager
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager,lifecycle)
        setFragment(WebViewFragments(args.name, args.url))
    }

    fun setFragment(fr: Fragment): Int? {
        viewPagerAdapter?.setFragment(fr)
        binding.mainWebViewViewPager.adapter = viewPagerAdapter
        return viewPagerAdapter?.getSize()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel = MainViewModel.getInstance()
        (activity as MainActivity).hideBottomNav(true)
        (activity as MainActivity).supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        (activity as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
    }

    fun removeFragment(position: Int): Int? {
        viewPagerAdapter?.removedFragment(position)
        return viewPagerAdapter?.getSize()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hideBottomNav(false)
    }
}