package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.ActivityWebBinding
import com.example.videodownloadingline.databinding.CustomToolbarLayoutBinding
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class WebActivity : AppCompatActivity() {
    private val args: WebActivityArgs by navArgs()
    private lateinit var binding: ActivityWebBinding
    private var goBack: Boolean = true
    private var mainViewModel: MainViewModel? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = MainViewModel.getInstance()
        if (applicationContext.isNetworkAvailable()) {
            binding.mainWebView.show()
            setWebSiteData()
            listenForProgress()
        } else {
            binding.tapToDownloadIcon.visibility = View.INVISIBLE
            binding.progressbar.isIndeterminate = true
            binding.root.showSandbar(
                "No Internet Connection Available", Snackbar.LENGTH_INDEFINITE,
                getColorInt(R.color.Alizarin_Crimson_color)
            )
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.web_frag_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
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
                    getAllTab()
                    hideImage()
                }
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    private fun getAllTab() {
        mainViewModel?.noOfOpenTab?.observe(this) {
            val item = it ?: 0
            changeToolbar(item)
        }
    }

    private fun hideImage() {
        lifecycleScope.launch {
            binding.tapToDownloadIcon.show()
            delay(2000)
            binding.tapToDownloadIcon.hide()
        }
    }

    override fun onBackPressed() {
        if (binding.mainWebView.canGoBack() && goBack) {
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
    fun changeToolbar(totalTab: Int) {
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
        toolbarBinding.searchBoxEd.setText(args.url)
        var searchText: String? = args.url
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
        toolbarBinding.toolbarHomeBtn.setOnClickListener {
            goBack = false
            onBackPressed()
        }
        toolbarBinding.totalTabOp.text = String.format(
            Locale.getDefault(),
            "%d",
            totalTab
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        binding.mainWebView.restoreState(savedInstanceState)
        super.onRestoreInstanceState(savedInstanceState)
    }
}