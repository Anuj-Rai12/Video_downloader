package com.example.videodownloadingline.adaptor.download_item_adaptor

import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFileItemLinearLayoutBinding
import com.example.videodownloadingline.model.downloaditem.SampleDownloadItem

typealias ItemForLinearLayoutListner = (data: SampleDownloadItem) -> Unit

class DownloadItemLinearAdaptor(private val itemClicked: ItemForLinearLayoutListner) :
    ListAdapter<SampleDownloadItem, DownloadItemLinearAdaptor.DownloadItemLinearViewHolder>(diffUtil) {
    inner class DownloadItemLinearViewHolder(private val binding: DownloadFileItemLinearLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: SampleDownloadItem, itemClicked: ItemForLinearLayoutListner) {
            binding.root.setOnClickListener {
                itemClicked(data)
            }
            binding.menuIcBtn.setOnClickListener {
                Toast.makeText(it.context, "${data.name} Item Clicked", Toast.LENGTH_SHORT).show()
            }
            binding.mainTxtFile.text = data.name
            binding.dataTxtInfo.apply {
                text = context.getString(
                    R.string.linear_download_info,
                    data.size,
                    data.createdCurrentTimeData
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SampleDownloadItem>() {
            override fun areItemsTheSame(
                oldItem: SampleDownloadItem,
                newItem: SampleDownloadItem
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: SampleDownloadItem,
                newItem: SampleDownloadItem
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloadItemLinearViewHolder {
        val binding = DownloadFileItemLinearLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DownloadItemLinearViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadItemLinearViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.makeData(it, itemClicked)
        }
    }

}