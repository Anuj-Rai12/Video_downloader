package com.example.videodownloadingline.adaptor.videoquality

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.ItemResolutionLayoutBinding
import com.example.videodownloadingline.model.downloadlink.VideoType

typealias vidSelectorListener = (flag:Boolean,data: VideoType) -> Unit

class VideoQualityAdaptor(private val itemClicked: vidSelectorListener) :
    ListAdapter<VideoType, VideoQualityAdaptor.VidSelectorViewHolder>(diffUtil) {
    private var itemSelected: VideoType? = null

    inner class VidSelectorViewHolder(private val binding: ItemResolutionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkBox = binding.itemResId

        @SuppressLint("SetTextI18n")
        fun makeData(data: VideoType, itemClicked: vidSelectorListener) {
            binding.itemResId.text = "\t${data.height}p"
            binding.txtVidSize.text =
                binding.txtVidSize.context.getString(R.string.num_of_tab, "${data.size}MB")
            binding.itemResId.setOnCheckedChangeListener { _, isChecked ->
                    itemClicked(isChecked,data)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoType>() {
            override fun areItemsTheSame(
                oldItem: VideoType,
                newItem: VideoType
            ) = oldItem.height == newItem.height

            override fun areContentsTheSame(
                oldItem: VideoType,
                newItem: VideoType
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VidSelectorViewHolder {
        val binding =
            ItemResolutionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VidSelectorViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectedItem(videoType: VideoType) {
        itemSelected = videoType
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: VidSelectorViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            itemSelected?.let { sel ->
                holder.checkBox.isChecked =
                    sel.height == it.height && sel.size == it.size && sel.weight == it.weight
            }
            holder.makeData(it, itemClicked)
        }
    }

}