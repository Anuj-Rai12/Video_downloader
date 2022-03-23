package com.example.videoeditingline.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.videoeditingline.R
import com.example.videoeditingline.databinding.VideoFragmentLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : Fragment(R.layout.video_fragment_layout) {
    private lateinit var binding: VideoFragmentLayoutBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = VideoFragmentLayoutBinding.bind(view)
    }
}