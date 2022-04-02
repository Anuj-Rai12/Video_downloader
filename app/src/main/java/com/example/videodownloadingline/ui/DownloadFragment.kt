package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videodownloadingline.R
import com.example.videodownloadingline.RecycleViewForDownload
import com.example.videodownloadingline.databinding.DownloadFragmentLayoutBinding
import com.example.videodownloadingline.utils.list.DownloadProgressLiveData


class DownloadFragment : Fragment(R.layout.download_fragment_layout) {
    private lateinit var binding: DownloadFragmentLayoutBinding
    private val listOfUrls = mutableListOf<String>()
    private var downloadManager: DownloadManager? = null
    private val listOfDownloadIds = mutableListOf<Long>()
    private lateinit var adapter: RecycleViewForDownload
    private var downloadReceiver: BroadcastReceiver? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        listOfUrls.addAll(resources.getStringArray(R.array.all_test_urls))
        downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        setUpRecycleView()

        listOfUrls.forEachIndexed { index, url ->
            val id = downloadManager?.enqueue(
                requestDownload(
                    DownloadManager.Request(Uri.parse(url)),
                    index + 1
                )
            )
            if (id != null)
                listOfDownloadIds.add(id)
        }
        Log.i(TAG, "onViewCreated:This is List of Ids $listOfDownloadIds")
        Log.i(TAG, "onViewCreated:This is List Size of URLs ${listOfUrls.size}")
        setBroadcastReceiver()
        DownloadProgressLiveData(requireActivity(), requestIds = listOfDownloadIds).observe(
            viewLifecycleOwner
        ) {

            if (!it.isNullOrEmpty()) {
                it.forEachIndexed { index, downloadItem ->
                    downloadItem.id = listOfDownloadIds[index]
                    downloadItem.title = getString(R.string.urls_type_title, (index + 1) * 5)
                    downloadItem.des = getString(R.string.urls_type_desc, (index + 1) * 5)
                }
            }
            adapter.submitList(it)
        }
    }

    private fun setUpRecycleView() {
        binding.mainRecycleView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            this@DownloadFragment.adapter = RecycleViewForDownload {
                Toast.makeText(activity, "$it", Toast.LENGTH_SHORT).show()
            }
            adapter = this@DownloadFragment.adapter
        }
    }

    private fun requestDownload(
        request: DownloadManager.Request,
        position: Int
    ): DownloadManager.Request {
        return request.setDescription(getString(R.string.urls_type_desc, position * 5))
            .setTitle(getString(R.string.urls_type_title, position * 5))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "Video_File_${position * 5}.mp4"
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    }

    private fun setBroadcastReceiver() {
        downloadReceiver = object : BroadcastReceiver() {
            @SuppressLint("Range")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                //startDownloading(TrueId, manage)
                Toast.makeText(
                    activity,
                    "Downloaded Completed",
                    Toast.LENGTH_SHORT
                ).show()
                /*val id = intent?.getLongArrayExtra(DownloadManager.EXTRA_DOWNLOAD_ID)
                if (id != null && id.equals(listOfDownloadIds)) {

                }*/
            }
        }

        activity?.registerReceiver(
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

}