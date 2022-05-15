package com.example.videodownloadingline.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
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
            Log.i(TAG, "onCreate: $value")
            setRecycleView()
            viewTabAdaptor.submitList(value)
        }
    }

    private fun setRecycleView() {
        binding.recycleViewForTab.apply {
            layoutManager = GridLayoutManager(this@ViewTabActivity, 2)
            setHasFixedSize(true)
            viewTabAdaptor = ViewTabOpenAdaptor { data, flag ->
                Log.i(TAG, "setRecycleView: $data and $flag")
                if (!flag)
                    mainViewModel?.currentNumTab = data.id - 1
                else
                    mainViewModel?.removeTab = Pair(true, data.id - 1)

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
        supportActionBar!!.title = "New Tab"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_add)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mainViewModel?.changeStateForCreateNewTB(flag = true)
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}