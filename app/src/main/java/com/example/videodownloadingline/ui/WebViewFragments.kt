package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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

    private fun onBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.mainWebView.canGoBack()) {
                        binding.mainWebView.goBack()
                    } else {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            })
    }

    private fun getAllTab() {
        mainViewModel?.noOfOpenTab?.observe(this) {
            val item = it ?: 0
            (requireActivity() as WebActivity).changeToolbar(item, url) { _ -> }
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
                    binding.progressbar.progress = 0
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
        (requireActivity() as WebActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as WebActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as WebActivity).supportActionBar!!.title = title
    }

}