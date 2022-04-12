package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.WebSiteFragmentLayoutBinding
import com.example.videodownloadingline.utils.show

class WebViewFragments(private val search: String) : Fragment(R.layout.web_site_fragment_layout) {
    private lateinit var binding: WebSiteFragmentLayoutBinding

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = WebSiteFragmentLayoutBinding.bind(view)
        binding.toolBarMainActivity.searchBoxEd.show()
        binding.toolBarMainActivity.searchBoxEd.setText(search)
        binding.toolBarMainActivity.totalTabOp.text = getString(
            R.string.num_of_tab,
            MainActivity.viewPager2?.currentItem ?: 10
        )
    }
}