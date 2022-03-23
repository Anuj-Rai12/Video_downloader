package com.example.videoeditingline.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.videoeditingline.R
import com.example.videoeditingline.databinding.ProgressFragmentLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgressFragment :Fragment(R.layout.progress_fragment_layout){
    private lateinit var binding: ProgressFragmentLayoutBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProgressFragmentLayoutBinding.bind(view)
    }
}