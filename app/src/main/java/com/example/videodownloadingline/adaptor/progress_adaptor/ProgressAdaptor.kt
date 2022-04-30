package com.example.videodownloadingline.adaptor.progress_adaptor

import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadVideoProgressItemListViewBinding
import com.example.videodownloadingline.model.downloadlink.DownloadItem
import com.example.videodownloadingline.utils.DownloadProgressLiveData
import com.example.videodownloadingline.utils.DownloadProgressLiveData.Companion.getMb


typealias itemClicked = (data: DownloadItem) -> Unit

class ProgressAdaptor(private val itemClicked: itemClicked) :
    ListAdapter<DownloadItem, ProgressAdaptor.DownloadFileViewHolder>(diffUtil) {

    inner class DownloadFileViewHolder(private val binding: DownloadVideoProgressItemListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: DownloadItem, itemClicked: itemClicked) {
            binding.root.setOnClickListener {
                itemClicked(data)
            }
            binding.titleDownload.text = data.title

            if (data.totalSizeBytes.toInt() <= 0) {
                binding.progressBar.isIndeterminate = true
                binding.totalSize.text = binding.totalSize.context.getString(
                    R.string.total_vid_view,
                    "${getMb(data.bytesDownloadedSoFar)}MB",
                )
            } else {
                binding.progressBar.progress =
                    (data.bytesDownloadedSoFar * 100.0 / data.totalSizeBytes).toInt()
                binding.totalSize.text = binding.totalSize.context.getString(
                    R.string.value_sample_txt,
                    getMb(data.bytesDownloadedSoFar).toString(),
                    getMb(data.totalSizeBytes).toString()
                )
            }
            binding.totalSize.append("\n\n${DownloadProgressLiveData.getStatus(data.status)}\n")
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