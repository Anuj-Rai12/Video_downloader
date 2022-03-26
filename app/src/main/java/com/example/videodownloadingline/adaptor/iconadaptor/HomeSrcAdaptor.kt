package com.example.videodownloadingline.adaptor.iconadaptor

import android.content.res.ColorStateList
import android.os.Build
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.databinding.HomeSrcIconsLayoutBinding
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show

typealias ItemClicked = (data: HomeSrcIcon, isAddIcon: Boolean) -> Unit

class HomeSrcAdaptor(private val itemClicked: ItemClicked) :
    ListAdapter<HomeSrcIcon, HomeSrcAdaptor.HomeSrcIconViewHolder>(diffUtil) {
    inner class HomeSrcIconViewHolder(private val binding: HomeSrcIconsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun makeData(data: HomeSrcIcon, itemClicked: ItemClicked) {

            if (data.name != null) {
                binding.homeSrcName.apply {
                    show()
                    text = data.name
                }
                binding.addHomeSrcBtn.hide()
                binding.nameTxt.apply {
                    show()
                    backgroundTintList = getTintColor(this, data.bg)
                    text = data.name.first().uppercaseChar().toString()
                }
            } else {
                binding.homeSrcName.hide()
                binding.nameTxt.hide()
                binding.addHomeSrcBtn.apply {
                    show()
                    backgroundTintList = getTintColor(this, data.bg)
                }
            }
            binding.addHomeSrcBtn.setOnClickListener {
                itemClicked(data, true)
            }
            binding.nameTxt.setOnClickListener {
                itemClicked(data, false)
            }

        }

        @RequiresApi(Build.VERSION_CODES.M)
        private fun getTintColor(it: View, color: Int): ColorStateList {
            return ColorStateList.valueOf(
                it.resources.getColor(
                    color,
                    null
                )
            )
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HomeSrcIcon>() {
            override fun areItemsTheSame(
                oldItem: HomeSrcIcon,
                newItem: HomeSrcIcon
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: HomeSrcIcon,
                newItem: HomeSrcIcon
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeSrcIconViewHolder {
        val binding =
            HomeSrcIconsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeSrcIconViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: HomeSrcIconViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.makeData(it, itemClicked)
        }
    }

}