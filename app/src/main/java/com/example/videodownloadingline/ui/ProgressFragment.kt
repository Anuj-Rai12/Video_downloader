package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.progress_adaptor.ProgressAdaptor
import com.example.videodownloadingline.databinding.ProgressFragmentLayoutBinding
import com.example.videodownloadingline.utils.DownloadProgressLiveData
import com.example.videodownloadingline.utils.createdCurrentTimeData
import com.example.videodownloadingline.view_model.MainViewModel


class ProgressFragment : Fragment(R.layout.progress_fragment_layout) {
    private lateinit var binding: ProgressFragmentLayoutBinding
    private var videoDownloadAdaptor: ProgressAdaptor? = null
    private var downloadManager: DownloadManager? = null
    private var viewModel: MainViewModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProgressFragmentLayoutBinding.bind(view)
        viewModel = MainViewModel.getInstance()
        downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        setRecycle()
        viewModel?.downloadId?.observe(viewLifecycleOwner) {
            setData(it)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setData(list: MutableList<Long>) {
        DownloadProgressLiveData(requireActivity(), requestIds = list).observe(
            viewLifecycleOwner
        ) {
            if (!it.isNullOrEmpty()) {
                it.forEachIndexed { index, downloadItem ->
                    downloadItem.id = list[index]//listOfDownloadIds[index]
                    downloadItem.title =
                        viewModel?.getVideoDataByIndex(index)?.webViewDownloadUrl?.videotitle
                            ?: createdCurrentTimeData//getString(R.string.urls_type_title, (index + 1) * 5)
                    downloadItem.des = "please wait while downloading..."
                }
            }
            videoDownloadAdaptor?.submitList(it)
            videoDownloadAdaptor?.notifyDataSetChanged()
        }


    }

    private fun setRecycle() {
        binding.recycleView.apply {
            videoDownloadAdaptor = ProgressAdaptor {
                val index = viewModel?.getIDIndex(it.id!!)!!
                viewModel?.removeID(index)
                viewModel?.removeVideo(index)
                downloadManager?.remove(it.id!!)
            }
            adapter = videoDownloadAdaptor
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title =
            getString(R.string.content_description_pro)
    }

    companion object {
        const val testingTitle = "May 1, 2022 12:22:55 AM"
        const val testingUrl = "https://download.samplelib.com/mp4/sample-5s.mp4"
    }
}