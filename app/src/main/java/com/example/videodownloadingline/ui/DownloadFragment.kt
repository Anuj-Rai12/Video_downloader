package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFragmentLayoutBinding
import com.example.videodownloadingline.utils.*


class DownloadFragment : Fragment(R.layout.download_fragment_layout) {
    private lateinit var binding: DownloadFragmentLayoutBinding
    private var searchRes: SearchView? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        initial()
        menuClickListener()
        binding.toolBarMainActivity.searchBoxEd.doOnTextChanged { text, _, _, _ ->
            Log.i(TAG, "onViewCreated: $text")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun menuClickListener() {

        binding.toolBarMainActivity.srcBtn.setOnClickListener {
            binding.toolBarMainActivity.toolbarHomeBtn.apply {
                setImageResource(R.drawable.ic_arrow_back)
                show()
            }
            binding.toolBarMainActivity.searchBoxEd.apply {
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    rightToLeft = it.id
                }
                binding.toolBarMainActivity.toolBarLayout.title = ""
                show()
            }
            it.hide()
        }


        binding.toolBarMainActivity.toolbarHomeBtn.setOnClickListener {
            it.hide()
            binding.toolBarMainActivity.searchBoxEd.hide()
            binding.toolBarMainActivity.toolBarLayout.title =
                getString(R.string.content_description_down)
            binding.toolBarMainActivity.srcBtn.show()
        }

        binding.toolBarMainActivity.threeBotMnuBtn.setOnClickListener {
            Toast.makeText(
                activity,
                "List icon clicked Icon Clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        /*binding.toolBarMainActivity.toolBarLayout.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.my_search_view -> {
                    /*val searchViews =
                        binding.toolBarMainActivity.toolBarLayout.menu.findItem(R.id.my_search_view)
                    searchRes = searchViews?.actionView as SearchView
                    setUpSearch(searchViews)
                    searchRes?.onQueasyListenerChanged { query ->
                        Log.i(TAG, "onViewCreated: $query")
                    }*/
                }
                R.id.menu_box ->
            }

            return@setOnMenuItemClickListener true
        }*/
    }


    private fun initial() {
        binding.toolBarMainActivity.totalTabOp.hide()
        binding.toolBarMainActivity.toolbarHomeBtn.hide()
        binding.toolBarMainActivity.srcBtn.show()
        binding.toolBarMainActivity.threeBotMnuBtn.setImageResource(R.drawable.ic_new_list_vew)
        binding.toolBarMainActivity.searchBoxEd.hide()
        //binding.toolBarMainActivity.toolBarLayout.inflateMenu(R.menu.main_menu)
        binding.toolBarMainActivity.toolBarLayout.title =
            getString(R.string.content_description_down)
        binding.toolBarMainActivity.toolBarLayout.setTitleTextColor(Color.WHITE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpSearch(searchViews: MenuItem) {
        searchRes?.queryHint = "search video..."
        searchViews.expandActionView()
        searchRes?.maxWidth = Integer.MAX_VALUE
        searchRes?.setIconifiedByDefault(false)
        searchRes?.isIconified = false
        binding.toolBarMainActivity.toolBarLayout.let {
            it.setTitleTextColor(requireActivity().getColorInt(R.color.Silver_color))
            it.setBackgroundColor(requireActivity().getColorInt(R.color.Emperor_color))
        }
        searchRes?.setBackgroundColor(requireActivity().getColorInt(R.color.white))
        searchViews.actionView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            .setImageResource(R.drawable.ic_clear_btn)
    }

}