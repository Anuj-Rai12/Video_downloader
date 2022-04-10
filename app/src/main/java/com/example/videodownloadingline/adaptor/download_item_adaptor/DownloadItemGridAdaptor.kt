package com.example.videodownloadingline.adaptor.download_item_adaptor

import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFileItemGridLayoutBinding
import com.example.videodownloadingline.model.downloaditem.DownloadItems


typealias ItemClickedListener = (data: DownloadItems) -> Unit

class DownloadItemGridAdaptor(private val itemClicked: ItemClickedListener) :
    ListAdapter<DownloadItems, DownloadItemGridAdaptor.DownloadItemGridViewHolder>(diffUtil) {
    inner class DownloadItemGridViewHolder(private val binding: DownloadFileItemGridLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: DownloadItems, itemClicked: ItemClickedListener) {
            binding.root.setOnClickListener {
                itemClicked(data)
            }
            binding.titleTxt.apply {
                text = this.context.getString(
                    R.string.grid_download_info,
                    data.fileTitle,
                    data.fileSize,
                    data.createdCurrentTimeData
                )
            }
            binding.menuBtn.setOnClickListener {
                Toast.makeText(it.context, "${data.fileTitle} menu Clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DownloadItems>() {
            override fun areItemsTheSame(
                oldItem: DownloadItems,
                newItem: DownloadItems
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: DownloadItems,
                newItem: DownloadItems
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadItemGridViewHolder {
        val binding = DownloadFileItemGridLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DownloadItemGridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadItemGridViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.makeData(it, itemClicked)
        }
    }

}


