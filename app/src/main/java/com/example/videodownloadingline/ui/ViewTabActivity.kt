package com.example.videodownloadingline.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.view_tab_open_adaptor.ViewTabOpenAdaptor
import com.example.videodownloadingline.databinding.ActivityViewTabBinding
import com.example.videodownloadingline.model.tabitem.TabItem
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.view_model.MainViewModel


class ViewTabActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityViewTabBinding.inflate(layoutInflater)
    }

    private lateinit var viewTabAdaptor: ViewTabOpenAdaptor

    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainViewModel = MainViewModel.getInstance()
        val extras = intent.extras
        if (extras != null) {
            val value = extras.getParcelableArrayList<TabItem>("TabItem")
            setRecycleView()
            viewTabAdaptor.submitList(value)
        }
    }

    private fun setRecycleView() {
        binding.recycleViewForTab.apply {
            viewTabAdaptor = ViewTabOpenAdaptor {
                Log.i(TAG, "setRecycleView: $it")
                mainViewModel?.currentNumTab = it.id - 1
                onBackPressed()
            }
            adapter = viewTabAdaptor
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}