package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.bottom_sheets.BottomSheetDialogForDownloadFrag
import com.example.videodownloadingline.databinding.WebSiteFragmentLayoutBinding
import com.example.videodownloadingline.model.downloadlink.VideoType
import com.example.videodownloadingline.model.downloadlink.WebViewDownloadUrl
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.MainViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.lang.Runnable


class WebViewFragments(private val title: String, private val url: String) :
    Fragment(R.layout.web_site_fragment_layout), OnBottomSheetClick {

    private lateinit var binding: WebSiteFragmentLayoutBinding
    private var mainViewModel: MainViewModel? = null
    private var isWebLoaded = false
    private var openBottomSheetDialog: BottomSheetDialogForDownloadFrag? = null

    inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun showHTML(html: String?) {
            daisyChainDownload(html)
        }
    }

    private fun daisyChainDownload(html: String?) {
        if (mainViewModel == null)
            mainViewModel = MainViewModel.getInstance()
        mainViewModel?.daisyChainDownload(html)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = WebSiteFragmentLayoutBinding.bind(view)
        mainViewModel = MainViewModel.getInstance()
        if (requireActivity().applicationContext.isNetworkAvailable()) {
            binding.mainWebView.show()
            setWebSiteData(url, false)
            checkVideoDownloadLink()
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkVideoDownloadLink() {
        mainViewModel?.daisyChannelVideoDownloadLink?.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { data ->
                setData(data)
                Log.i(TAG, "checkVideoDownloadLink: $data")
            }
        }
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

    private fun getAllTab(url: String) {
        mainViewModel?.noOfOpenTab?.observe(this) {
            val item = it ?: 0
            (requireActivity() as MainActivity).changeToolbar(item, url, { _ -> }, {
                findNavController().popBackStack()
            })
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setData(webViewDownloadUrl: WebViewDownloadUrl) {
        val info = if (!webViewDownloadUrl.hdurl.isNullOrEmpty()) {
            changeFab(R.color.Casablanca_color)
            webViewDownloadUrl.hdurl
        } else if (!webViewDownloadUrl.sdurl.isNullOrEmpty()) {
            changeFab(R.color.Surfie_Green_color)
            webViewDownloadUrl.sdurl
        } else
            null
        binding.downloadFloatingBtn.setOnClickListener {
            if (info != null) {
                Toast.makeText(requireActivity(), "Please Wait while checking Video Quality..", Toast.LENGTH_LONG).show()
                urlResolution(info) { height, width, size ->
                    VideoType(height, width, size).also {
                        openBottomSheet(listOf(it))
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun urlResolution(url: String, getRes: (Int, Int, Int) -> Unit) {
        val trackGroupsFuture: ListenableFuture<TrackGroupArray> =
            MetadataRetriever.retrieveMetadata(
                requireActivity(), MediaItem.fromUri(url)
            )
        Futures.addCallback(
            trackGroupsFuture,
            object : FutureCallback<TrackGroupArray?> {
                override fun onSuccess(trackGroups: TrackGroupArray?) {
                    trackGroups?.let {
                        lifecycleScope.launchWhenCreated {
                            val size = async(IO) {
                                getVideoFileSize(url)
                            }
                            val gHW = async {
                                getHeightAndWidth(it)
                            }
                            val gHWRes = gHW.await()
                            getRes(gHWRes.first, gHWRes.second, size.await())
                        }
                    }

                }

                override fun onFailure(t: Throwable) {
                    Log.i(TAG, "onFailure: ${t.message}")
                }
            },
            Runnable::run
        )
    }

    private fun getHeightAndWidth(trackGroups: TrackGroupArray): Pair<Int, Int> {
        var height = 0
        var width = 0
        for (i in 0 until trackGroups.length) {
            val h = trackGroups[i].getFormat(0).height
            val w = trackGroups[i].getFormat(0).width
            val other = trackGroups[i].getFormat(0)
            if (h > -1)
                height = h
            if (w > -1)
                width = w
            Log.i(
                TAG,
                "onSuccess: $h and $w the other format -> $other"
            )
        }
        return Pair(height, width)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeFab(color: Int) {
        binding.downloadFloatingBtn.backgroundTintList =
            ColorStateList.valueOf(requireActivity().getColorInt(color))

        /*binding.downloadFloatingBtn.viewTreeObserver?.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                @SuppressLint("UnsafeOptInUsageError")
                override fun onGlobalLayout() {
                    val badgeDrawable = BadgeDrawable.create(requireActivity())
                    badgeDrawable.number = 12
                    //Important to change the position of the Badge
                    badgeDrawable.horizontalOffset = 30
                    badgeDrawable.verticalOffset = 20
                    BadgeUtils.attachBadgeDrawable(
                        badgeDrawable,
                        binding.downloadFloatingBtn,
                        null
                    )
                    binding.downloadFloatingBtn.viewTreeObserver.removeOnGlobalLayoutListener(
                        this
                    )
                }
            })*/
    }

    private fun openBottomSheet(list: List<VideoType>) {
        openBottomSheetDialog =
            BottomSheetDialogForDownloadFrag(
                "",
                BottomSheetDialogForDownloadFrag.Companion.Bottom.WEB_VIEW_FRAGMENT
            )
        BottomSheetDialogForDownloadFrag.list = list
        openBottomSheetDialog?.onBottomIconClicked = this
        openBottomSheetDialog?.show(childFragmentManager, "Open Bottom Sheet For Choose Download")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSiteData(url: String, flag: Boolean) {
        val extraHeaders: MutableMap<String, String> = HashMap()
        if (flag) extraHeaders["Referer"] = url

        binding.mainWebView.apply {
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                javaScriptCanOpenWindowsAutomatically = true
                addJavascriptInterface(MyJavaScriptInterface(), "HtmlViewer")
                loadsImagesAutomatically = true
            }
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    when {
                        request!!.url.toString().contains(".m3u8") -> {
                            Log.d(TAG, request.url.toString())
                        }
                        request.url.toString().contains(".mp4") -> {
                            Log.d(TAG, request.url.toString())
                        }
                        else -> {
                            Log.d(TAG, request.url.toString())
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }


                override fun onPageFinished(view: WebView?, url: String?) {
                    this@apply.loadUrl(
                        "javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');",
                        extraHeaders
                    )
                    super.onPageFinished(view, url)
                }


                override fun onReceivedHttpAuthRequest(
                    view: WebView?,
                    handler: HttpAuthHandler?,
                    host: String?,
                    realm: String?
                ) {
                    super.onReceivedHttpAuthRequest(view, handler, host, realm)
                }


                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }

            }
            post {
                loadUrl(url, extraHeaders)
            }
        }
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
                    getAllTab(binding.mainWebView.url ?: url)
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
            getAllTab(binding.mainWebView.url ?: url)
        }
    }

    override fun onPause() {
        super.onPause()
        openBottomSheetDialog?.dismiss()
    }

    override fun <T> onItemClicked(type: T) {
        Log.i(TAG, "onItemClicked: $type")
        openBottomSheetDialog?.dismiss()
    }

}