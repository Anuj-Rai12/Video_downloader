package com.example.videodownloadingline

import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.databinding.DownloadVideoProgressItemListViewBinding
import com.example.videodownloadingline.ui.HomeScrFragment
import com.example.videodownloadingline.utils.DownloadProgressLiveData
import com.example.videodownloadingline.utils.list.DownloadItem

typealias itemCliked = (data: DownloadItem) -> Unit

class RecycleViewForDownload(private val itemClicked: itemCliked) :
    ListAdapter<DownloadItem, RecycleViewForDownload.DownloadFileViewHolder>(diffUtil) {
    inner class DownloadFileViewHolder(private val binding: DownloadVideoProgressItemListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: DownloadItem, itemClicked: itemCliked) {
            binding.root.setOnClickListener {
                itemClicked(data)
            }

            binding.downloadStatusVid.text = DownloadProgressLiveData.getStatus(data.status)

            binding.totalSize.text = binding.totalSize.context.getString(
                R.string.size_test_tab,
                HomeScrFragment.getMb(data.bytesDownloadedSoFar),
                HomeScrFragment.getMb(data.totalSizeBytes)
            )

            binding.progressBar.progress =
                (data.bytesDownloadedSoFar * 100.0 / data.totalSizeBytes).toInt()

        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DownloadItem>() {
            override fun areItemsTheSame(
                oldItem: DownloadItem,
                newItem: DownloadItem
            ) = oldItem.uri == newItem.uri

            override fun areContentsTheSame(
                oldItem: DownloadItem,
                newItem: DownloadItem
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadFileViewHolder {
        val binding = DownloadVideoProgressItemListViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DownloadFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadFileViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.makeData(it, itemClicked)
        }
    }

}