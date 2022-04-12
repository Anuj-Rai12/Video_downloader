package com.example.videodownloadingline.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.WebSiteFragmentLayoutBinding

class WebViewFragments : Fragment(R.layout.web_site_fragment_layout) {
    private lateinit var binding: WebSiteFragmentLayoutBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = WebSiteFragmentLayoutBinding.bind(view)

    }
}