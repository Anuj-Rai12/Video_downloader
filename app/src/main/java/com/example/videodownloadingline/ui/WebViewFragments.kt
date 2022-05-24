package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URL


class WebViewFragments(private val title: String, private val mainUrl: String) :
    Fragment(R.layout.web_site_fragment_layout), OnBottomSheetClick {

    private lateinit var binding: WebSiteFragmentLayoutBinding
    private var mainViewModel: MainViewModel? = null
    private var isWebLoaded = false
    private var url = mainUrl
    private var openBottomSheetDialog: BottomSheetDialogForDownloadFrag? = null
    private var downloadManager: DownloadManager? = null
    private var isShowDialogOnce: Boolean = true
    private var showVideoPlayDialog: AlertDialog? = null

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
        downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        if (requireActivity().applicationContext.isNetworkAvailable()) {
            binding.mainWebView.show()
            setWebSiteData(url, false)
            (requireActivity() as MainActivity).setBroadcastReceiver(mainViewModel)
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
        val closeTab = menu.findItem(R.id.close_tab_mnu)

        newTab?.setOnMenuItemClickListener {
            createNewTB(HomeScrFragment(true), null)
            return@setOnMenuItemClickListener true
        }
        closeTab?.setOnMenuItemClickListener {
            val way = (parentFragment as BrowserFragment)
            if (way.getTbList().isNullOrEmpty()) {
                findNavController().popBackStack()
            }
            way.getTbList()?.let {
                mainViewModel?.removeTab()
                val index = it.last().id - 1
                way.removeFragment(index, true)
                BrowserFragment.viewPager?.currentItem = index
            }
            if (way.getTbList().isNullOrEmpty()) {
                findNavController().popBackStack()
            }
            return@setOnMenuItemClickListener true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun createNewTB(fragment: Fragment, url: String?) {
        val size = (parentFragment as BrowserFragment?)?.setFragment(
            fragment, url
        )
        Log.i(TAG, "onCreateOptionsMenu: $size")
        size?.let {
            mainViewModel?.addMoreTab()
            BrowserFragment.viewPager?.currentItem = size - 1
            return
        }
        Log.i(TAG, "createNewTB: error while creating")
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
            (requireActivity() as MainActivity).changeToolbar(item, url, { url ->
                mainViewModel?.removeOldDownloadLink()
                setWebSiteData(url, false)
            }, {
                findNavController().popBackStack()
            }, viewTab = {
                requireActivity().goToTbActivity<ViewTabActivity>((parentFragment as BrowserFragment).getTbList())
            })
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setData(webViewDownloadUrl: WebViewDownloadUrl) {
        var info = if (!webViewDownloadUrl.hdurl.isNullOrEmpty()) {
            changeFab(R.color.Casablanca_color)
            webViewDownloadUrl.hdurl
        } else if (!webViewDownloadUrl.sdurl.isNullOrEmpty()) {
            changeFab(R.color.Surfie_Green_color)
            webViewDownloadUrl.sdurl
        } else
            null

        var click = false


        binding.downloadFloatingBtn.setOnClickListener {
            if (info != null) {
                if (info!!.startsWith("blob:")) {
                    info = info!!.substring(5)
                    //Log.i(TAG, "setData: $info for testing value")
                }
                if (info!!.contains("http", true) && !info!!.contains("https", true)) {
                    info = "https:$info"
                }
                Log.i(TAG, "setData: check Video quality $info")
                requireActivity().toastMsg("Please Wait while checking Video Quality..")
            } else {
                requireActivity().toastMsg("Could not find download url")
                return@setOnClickListener
            }
            if (!click) {
                click = true
                if (info!!.endsWith(".m3u8") || webViewDownloadUrl.videotype == "video/.m3u8") {
                    findWidthAndHeight(info!!).also {
                        VideoType(
                            it.second.last(),
                            it.second.first(),
                            it.first,
                            "video/.m3u8",
                            webViewDownloadUrl,
                            info!!
                        ).also { video ->
                            click = false
                            openBottomSheet(listOf(video))
                        }
                    }
                } else {
                    urlResolution(info!!) { height, width, type, size ->
                        VideoType(height, width, size, type, webViewDownloadUrl, info!!).also {
                            click = false
                            openBottomSheet(listOf(it))
                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun urlResolution(url: String, getRes: (Int, Int, String, Long) -> Unit) {
        try {
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
                                getRes(
                                    gHWRes.first.first,
                                    gHWRes.first.second,
                                    gHWRes.second,
                                    size.await()
                                )
                            }
                        }

                    }

                    override fun onFailure(t: Throwable) {
                        Log.i(TAG, "onFailure: ${t.message}")
                    }
                },
                Runnable::run
            )
        } catch (e: Exception) {
            findWidthAndHeight(url).also {
                getRes(
                    it.second.last(),
                    it.second.first(),
                    "video/.m3u8",
                    it.first
                )

            }
        }
    }

    private fun getHeightAndWidth(trackGroups: TrackGroupArray): Pair<Pair<Int, Int>, String> {
        var height = 0
        var width = 0
        var type = ""
        for (i in 0 until trackGroups.length) {
            val h = trackGroups[i].getFormat(0).height
            val w = trackGroups[i].getFormat(0).width
            val other = trackGroups[i].getFormat(0).sampleMimeType
            if (h > -1) {
                height = h
                type = other!!
            }
            if (w > -1)
                width = w
            Log.i(
                TAG,
                "onSuccess: $h and $w the other format -> $other"
            )
        }
        return Pair(Pair(height, width), type)
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
            BottomSheetDialogForDownloadFrag(enum = BottomSheetDialogForDownloadFrag.Companion.Bottom.WEB_VIEW_FRAGMENT)
        BottomSheetDialogForDownloadFrag.list = list
        openBottomSheetDialog?.onBottomIconClicked = this
        openBottomSheetDialog?.show(childFragmentManager, "Open Bottom Sheet For Choose Download")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebSiteData(url: String, flag: Boolean) {
        var onlyOnce = true
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
                setSupportMultipleWindows(true)
            }
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    when {
                        request!!.url.toString().contains(".m3u8") -> {
                            if (onlyOnce) {
                                onlyOnce = mainViewModel?.getM3U8Url(request.url.toString())!!
                            }
                        }
                        request.url.toString().contains(".mp4") -> {
                            Log.i(TAG, "shouldInterceptRequest: the Url is -> ${request.url}")
                            Log.i(
                                TAG,
                                "shouldInterceptRequest: the Url is -> ${request.requestHeaders}"
                            )
                        }
                        request.requestHeaders.containsValue("video/") -> {
                            Log.i(TAG, "shouldInterceptRequest: ${request.url}")
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
                    return request?.let { req ->
                        val domain: String?
                        try {
                            domain = getHostDomainName(req.url.host!!)
                        } catch (e: Exception) {
                            Log.i(
                                TAG,
                                "shouldOverrideUrlLoading: domain Exception ${e.localizedMessage}"
                            )
                            return@let false
                        }

                        if (req.url.toString() == mainUrl || domain == getHostDomainName(URL(mainUrl).host)) {
                            return@let false
                        }
                        return@let if (req.url != null && this@WebViewFragments.url.contains(req.url.host!!) || domain == getHostDomainName(
                                URL(this@WebViewFragments.url).host
                            )
                        ) {
                            false
                        } else {
<<<<<<< HEAD
                            mainViewModel?.addMoreTab()
                            val size = (parentFragment as BrowserFragment).setFragment(
                                WebViewFragments(
                                    "Loading...",
                                    req.url.toString()
                                )
                            )
                            Log.i(TAG, "shouldOverrideUrlLoading: $size")
                            BrowserFragment.viewPager?.currentItem = size!! - 1
                            /*val intent = Intent(Intent.ACTION_VIEW, req.url)
                            startActivity(intent)*/
=======
                            createNewTB(
                                WebViewFragments("Loading...", req.url.toString()),
                                req.url.toString()
                            )
>>>>>>> setting_branch
                            true
                        }
                    } ?: false
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
                    url = binding.mainWebView.url ?: url
                    getAllTab(url)
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
            if (isShowDialogOnce) {
                isShowDialogOnce = false
                parentFragment?.let {
                    showVideoPlayDialog = activity?.showDialogBox(
                        title = getString(R.string.download_msg_title),
                        desc = getString(R.string.download_msg_desc),
                        flag = true
                    ) {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(isWebLoaded)
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title = title

        mainViewModel?.currentNumTab?.let {
            Log.i(TAG, "onResume: Current Tab --> $it")
            BrowserFragment.viewPager?.currentItem = it
            mainViewModel?.currentNumTab = null
        }

        if (mainViewModel?.removeTab?.first == true) {
            mainViewModel?.removeTab?.second?.let {
                (parentFragment as BrowserFragment).removeFragment(it, true)
            }
            mainViewModel?.removeTab = Pair(false, null)
        }

        var parent = parentFragment
        val uiHandler = Handler(Looper.getMainLooper())
        uiHandler.post {
            parent?.let {
                val flag = mainViewModel?.createNewTab?.value
                Log.i(TAG, "onViewCreated: Main View Model ---> $flag")
                if (flag == true) {
                    mainViewModel?.addMoreTab()
                    val size = (it as BrowserFragment).setFragment(HomeScrFragment(true), null)
                    Log.i(TAG, "onCreateOptionsMenu: $size")
                    BrowserFragment.viewPager?.currentItem = size!! - 1
                    parent = null
                    mainViewModel?.changeStateForCreateNewTB(false)
                }
            }
        }


        if (isWebLoaded) {
            url = binding.mainWebView.url ?: url
            getAllTab(url)
        }
    }

    override fun onPause() {
        super.onPause()
        openBottomSheetDialog?.dismiss()
        showVideoPlayDialog?.dismiss()
        mainViewModel?.removeOldDownloadLink()
    }

    override fun <T> onItemClicked(type: T) {
        (type as VideoType).also { response ->
            response.webViewDownloadUrl.videotitle =
                response.webViewDownloadUrl.videotitle ?: createdCurrentTimeData
            response.thumbnail = if (response.format == "video/.m3u8")
                "Video_" + System.currentTimeMillis().toString() + ".m3u8"
            else
                "Video_" + System.currentTimeMillis().toString() + ".mp4"

            Log.i(TAG, "onItemClicked: ${response.webViewDownloadUrl.videotitle}")
            val id = downloadManager?.enqueue(
                requestDownload(
                    requireContext(),
                    DownloadManager.Request(Uri.parse(response.url)),
                    title = response.thumbnail,
                    response.url
                )
            )
            Log.i(TAG, "onItemClicked: $id and $response")
            if (id != null) {
                mainViewModel?.addID(id)
                mainViewModel?.addVideo(response)
            }
        }
        openBottomSheetDialog?.dismiss()
    }

}