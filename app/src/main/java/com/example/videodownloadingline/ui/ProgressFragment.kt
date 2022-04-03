package com.example.videodownloadingline.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.progress_adaptor.ProgressAdaptor
import com.example.videodownloadingline.databinding.ProgressFragmentLayoutBinding
import com.example.videodownloadingline.model.progress.ProgressData
import com.example.videodownloadingline.model.progress.Vid
import com.example.videodownloadingline.utils.hide


class ProgressFragment : Fragment(R.layout.progress_fragment_layout) {
    private lateinit var binding: ProgressFragmentLayoutBinding
    private lateinit var videoDownloadAdaptor: ProgressAdaptor
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProgressFragmentLayoutBinding.bind(view)
        initial()
        setRecycle()
        setData()
    }

    private fun initial() {
        binding.toolBarMainActivity.totalTabOp.hide()
        binding.toolBarMainActivity.toolbarHomeBtn.hide()
        binding.toolBarMainActivity.threeBotMnuBtn.hide()
        binding.toolBarMainActivity.searchBoxEd.hide()
        binding.toolBarMainActivity.toolBarLayout.title = getString(R.string.content_description_pro)
        binding.toolBarMainActivity.toolBarLayout.setTitleTextColor(Color.WHITE)
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