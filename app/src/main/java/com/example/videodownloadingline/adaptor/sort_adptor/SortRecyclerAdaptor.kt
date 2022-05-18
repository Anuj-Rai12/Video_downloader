package com.example.videodownloadingline.adaptor.sort_adptor

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.SortedItemLayoutBinding

typealias ListenerSortItemClicked = (data: String, position: Int) -> Unit

class SortRecyclerAdaptor(private val itemClicked: ListenerSortItemClicked) :
    ListAdapter<String, SortRecyclerAdaptor.SortAdaptorViewHolder>(diffUtil) {
    private var isFolder: Boolean = false

    inner class SortAdaptorViewHolder(private val binding: SortedItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val btnClick=binding.btnClickCheck
        fun makeData(data: String, itemClicked: ListenerSortItemClicked, position: Int) {
            binding.txtInfo.text = data
            binding.root.setOnClickListener {
                binding.btnClickCheck.setImageResource(R.drawable.ic_check)
                itemClicked(data, position)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortAdaptorViewHolder {
        val binding =
            SortedItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SortAdaptorViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getPublicFolder(flag: Boolean) {
        isFolder = flag
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: SortAdaptorViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            if (isFolder){
                holder.btnClick.setImageResource(R.drawable.ic_folder)
            }
            holder.makeData(it, itemClicked, position)
        }
    }

}