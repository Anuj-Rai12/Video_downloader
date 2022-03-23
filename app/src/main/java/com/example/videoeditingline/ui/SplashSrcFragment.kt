package com.example.videoeditingline.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.videoeditingline.R
import com.example.videoeditingline.databinding.SplashScreenFragmentBinding
import com.example.videoeditingline.utils.hideStatusBar
import com.example.videoeditingline.utils.showFullSrc
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashSrcFragment : Fragment(R.layout.splash_screen_fragment) {
    private lateinit var binding: SplashScreenFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashScreenFragmentBinding.bind(view)
        initial()

        lifecycleScope.launch {
            delay(3000)
            val action = SplashSrcFragmentDirections
                .actionSplashSrcFragmentToHomeScrFragment()
            findNavController().navigate(action)
        }

    }

    private fun initial() {
        activity?.showFullSrc()
    }
}