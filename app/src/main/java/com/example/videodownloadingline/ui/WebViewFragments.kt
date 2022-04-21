package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.WebSiteFragmentLayoutBinding
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WebViewFragments(private val title: String, private val url: String) :
    Fragment(R.layout.web_site_fragment_layout) {
    private lateinit var binding: WebSiteFragmentLayoutBinding
    private var mainViewModel: MainViewModel? = null
    private var isWebLoaded = false

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = WebSiteFragmentLayoutBinding.bind(view)
        mainViewModel = MainViewModel.getInstance()
        if (requireActivity().applicationContext.isNetworkAvailable()) {
            binding.mainWebView.show()
            setWebSiteData()
            listenForProgress()
        } else {
            binding.tapToDownloadIcon.visibility = View.INVISIBLE
            binding.progressbar.isIndeterminate = true
            binding.root.showSandbar(
                "No Internet Connection Available", Snackbar.LENGTH_INDEFINITE,
                requireActivity().getColorInt(R.color.Alizarin_Crimson_color)
            )
        }
        onBackPress()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.web_frag_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        val newTab = menu.findItem(R.id.new_tab_option_mnu)

        newTab?.setOnMenuItemClickListener {
            mainViewModel?.addMoreTab()
            val size = (parentFragment as BrowserFragment).setFragment(
                HomeScrFragment(true)
            )
            Log.i(TAG, "onCreateOptionsMenu: $size")
            BrowserFragment.viewPager?.currentItem = size!! - 1
            return@setOnMenuItemClickListener true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun onBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.mainWebView.canGoBack()) {
                        binding.mainWebView.goBack()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            })
    }

    private fun getAllTab() {
        mainViewModel?.noOfOpenTab?.observe(this) {
            val item = it ?: 0
            (requireActivity() as MainActivity).changeToolbar(item, url, { _ ->

            }, {
                findNavController().popBackStack()
            })
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSiteData() {
        binding.mainWebView.apply {
            webViewClient = WebViewClient()
            loadUrl(this@WebViewFragments.url)
        }
        val webSettings: WebSettings = binding.mainWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
    }


    private fun listenForProgress() {
        binding.mainWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.i(TAG, "onProgressChanged: $newProgress")
                binding.progressbar.progress = newProgress
                if (newProgress == 100) {
                    setHasOptionsMenu(true)
                    binding.progressbar.progress = 0
                    isWebLoaded = true
                    getAllTab()
                    hideImage()
                }
                super.onProgressChanged(view, newProgress)
            }
        }
    }


    private fun hideImage() {
        lifecycleScope.launch {
            binding.tapToDownloadIcon.show()
            delay(2000)
            binding.tapToDownloadIcon.hide()
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(isWebLoaded)
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title = title
        if (isWebLoaded) {
            getAllTab()
        }
    }

}