package com.example.videoeditingline.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.videoeditingline.R
import com.example.videoeditingline.databinding.DownloadFragmentLayoutBinding


class DownloadFragment : Fragment(R.layout.download_fragment_layout) {
    private lateinit var binding: DownloadFragmentLayoutBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
    }
}