package com.example.videoeditingline.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.videoeditingline.R
import com.example.videoeditingline.databinding.HomeSrcFragmentBinding
import com.example.videoeditingline.utils.changeStatusBarColor
import com.example.videoeditingline.utils.hideFullSrc


class HomeScrFragment : Fragment(R.layout.home_src_fragment) {
    private lateinit var binding: HomeSrcFragmentBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeSrcFragmentBinding.bind(view)
        initial()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        activity?.hideFullSrc()
        activity?.changeStatusBarColor()
    }
}