package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.navigation.navArgs
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.ActivityWebBinding
import com.example.videodownloadingline.databinding.CustomToolbarLayoutBinding
import com.example.videodownloadingline.utils.*
import com.google.android.material.snackbar.Snackbar
import java.util.*

class WebActivity : AppCompatActivity() {
    private val args: WebActivityArgs by navArgs()
    private lateinit var binding: ActivityWebBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (applicationContext.isNetworkAvailable()) {
            binding.mainWebView.show()
            setWebSiteData()
            listenForProgress()
        } else {
            binding.progressbar.isIndeterminate = true
            binding.root.showSandbar(
                "No Internet Connection Available", Snackbar.LENGTH_INDEFINITE,
                getColorInt(R.color.Alizarin_Crimson_color)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSiteData() {
        binding.mainWebView.apply {
            webViewClient = WebViewClient()
            loadUrl(args.url)
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
                    changeToolbar()
                }
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.mainWebView.canGoBack()) {
            binding.mainWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar!!.title = args.name
    }

    @SuppressLint("RtlHardcoded")
    fun changeToolbar() {
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
        toolbarBinding.searchBoxEd.setText(args.name)
        var searchText: String? = args.name
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
                        Log.i(TAG, "changeToolbar: $searchText")
                        /*if (!searchText.isNullOrEmpty()) {
                            Working on it
                        }*/
                    }
                }
            }
            true
        }
        toolbarBinding.totalTabOp.text = String.format(
            Locale.getDefault(),
            "%d",
            1243
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        binding.mainWebView.restoreState(savedInstanceState)
        super.onRestoreInstanceState(savedInstanceState)
    }
}