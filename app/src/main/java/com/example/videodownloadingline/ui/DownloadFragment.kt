package com.example.videodownloadingline.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFragmentLayoutBinding
import com.example.videodownloadingline.utils.hideActionBar



class DownloadFragment : Fragment(R.layout.download_fragment_layout) {
    private lateinit var binding: DownloadFragmentLayoutBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        (activity as AppCompatActivity?)?.hideActionBar()
    }
}