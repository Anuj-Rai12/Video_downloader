package com.example.videodownloadingline.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.progress_adaptor.ProgressAdaptor
import com.example.videodownloadingline.databinding.ProgressFragmentLayoutBinding
import com.example.videodownloadingline.model.progress.ProgressData
import com.example.videodownloadingline.model.progress.Vid
import com.example.videodownloadingline.utils.showActionBar


class ProgressFragment : Fragment(R.layout.progress_fragment_layout) {
    private lateinit var binding: ProgressFragmentLayoutBinding
    private lateinit var videoDownloadAdaptor: ProgressAdaptor
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProgressFragmentLayoutBinding.bind(view)
        (activity as AppCompatActivity?)?.showActionBar()
        activity?.actionBar?.title = "Progress"
        setRecycle()
        setData()
    }

    private fun setData() {
        val list = listOf(
            ProgressData(1, getString(R.string.sample_txt), 54, 5, Vid.speed),
            ProgressData(2, getString(R.string.sample_txt), 70, 10, Vid.pause)
        )
        videoDownloadAdaptor.submitList(list)
    }

    private fun setRecycle() {
        binding.recycleView.apply {
            videoDownloadAdaptor = ProgressAdaptor {

            }
            adapter = videoDownloadAdaptor
        }
    }
}