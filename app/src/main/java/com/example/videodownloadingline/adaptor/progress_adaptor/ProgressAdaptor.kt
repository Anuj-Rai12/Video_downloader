package com.example.videodownloadingline.adaptor.progress_adaptor

import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadVideoProgressItemListViewBinding
import com.example.videodownloadingline.model.progress.ProgressData
import com.example.videodownloadingline.model.progress.Vid

typealias Listener = (data: ProgressData) -> Unit

class ProgressAdaptor(private val itemClicked: Listener) :
    ListAdapter<ProgressData, ProgressAdaptor.DownloadVideoItemViewHolder>(diffUtil) {
    inner class DownloadVideoItemViewHolder(private val binding: DownloadVideoProgressItemListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: ProgressData, itemClicked: Listener) {
            binding.titleDownload.text = data.title
            binding.progressBar.progress = data.progress
            binding.root.setOnClickListener {
                itemClicked(data)
            }
            binding.totalSize.text =
                binding.totalSize.context.getString(
                    R.string.value_sample_txt,
                    (data.size / 2).toString(),
                    data.size.toString()
                )

            binding.downloadStatusVid.text = data.status.name

            binding.btnPauseOrPlay.setOnClickListener {
                when (data.status) {
                    Vid.pause -> it.setBackgroundResource(R.drawable.ic_play)
                    Vid.speed -> it.setBackgroundResource(R.drawable.ic_pause)
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ProgressData>() {
            override fun areItemsTheSame(
                oldItem: ProgressData,
                newItem: ProgressData
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProgressData,
                newItem: ProgressData
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadVideoItemViewHolder {
        val binding = DownloadVideoProgressItemListViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DownloadVideoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadVideoItemViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.makeData(it, itemClicked)
        }
    }

}