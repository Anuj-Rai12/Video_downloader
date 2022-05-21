package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.databinding.CustomToolbarLayoutBinding
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.repo.DownloadFragmentRepo
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavController

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFileDir(getString(R.string.file_path_2), this, false).also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

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
        goTo: () -> Unit,
        viewTab: () -> Unit
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
                        if (!searchText.isNullOrEmpty() && isValidUrl(searchText)) {
                            listenForSearch(searchText!!)
                        } else
                            toastMsg("Cannot Incited  Search!!")
                    }
                }
            }
            true
        }
        toolbarBinding.totalTabOp.setOnClickListener {
            Log.i(TAG, "changeToolbar: item Clicked")
            viewTab()
        }
        toolbarBinding.totalTabOp.text = String.format(
            Locale.getDefault(),
            "%d",
            totalTab
        )
    }

    fun setBroadcastReceiver(mainViewModel: MainViewModel?) {
        val downloadReceiver = object : BroadcastReceiver() {
            @SuppressLint("Range")
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                mainViewModel?.let {
                    if (id == null)
                        return@let
                    val index = it.getIDIndex(id)
                    Log.i(TAG, "onReceive: this index  for id is -> $index")
                    if (index != -1) {
                        it.getVideoDataByIndex(index)?.let { res ->
                            Log.i(TAG, "onReceive: Download List --->  $res")
                            val title = res.webViewDownloadUrl.videotitle ?: ""
                            val file = getFileDir(res.thumbnail, this@MainActivity)
                            val time = this@MainActivity.videoDuration(file) ?: ""
                            val len = file.length()
                            val newUrl = File(finPath("/Download/VideoDownload/${res.thumbnail}"))
                            moveFile(file.absolutePath, newUrl.absolutePath)
                            DownloadItems(
                                0,
                                title,
                                res.thumbnail,
                                newUrl.absolutePath,
                                time,
                                res.format,
                                len
                            ).also { value ->
                                file.delete()
                                Log.i(TAG, "onReceive: Download Save Item $value")
                                lifecycleScope.launchWhenCreated {
                                    val saveDownloadItem = async(Dispatchers.IO) {
                                        DownloadFragmentRepo(
                                            RoomDataBaseInstance.getInstance(
                                                applicationContext
                                            )
                                        ).addDownload(
                                            value
                                        )
                                    }
                                    saveDownloadItem.await()
                                }
                            }
                        }
                        it.removeID(index)
                        it.removeVideo(index)
                    }
                }

                Log.i(TAG, "onReceive: Download Completed")
            }
        }

        this.registerReceiver(
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }


}