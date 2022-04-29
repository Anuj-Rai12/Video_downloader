package com.example.videodownloadingline.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.progress_adaptor.ProgressAdaptor
import com.example.videodownloadingline.databinding.ProgressFragmentLayoutBinding


class ProgressFragment : Fragment(R.layout.progress_fragment_layout) {
    private lateinit var binding: ProgressFragmentLayoutBinding
    private lateinit var videoDownloadAdaptor: ProgressAdaptor
    private var downloadReceiver: BroadcastReceiver? = null
    private val listOfUrls = mutableListOf<String>()
    private var downloadManager: DownloadManager? = null
    private val listOfDownloadIds = mutableListOf<Long>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProgressFragmentLayoutBinding.bind(view)
        setRecycle()



        //  setData()
    }


    /*private fun setData() {

    }*/

    private fun setRecycle() {
        binding.recycleView.apply {
            videoDownloadAdaptor = ProgressAdaptor {

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
}