package com.example.videodownloadingline.adaptor.setting_adaptor

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.SettingItemLayoutBinding
import com.example.videodownloadingline.utils.show

typealias itemClicked = (data: SettingDataHolder) -> Unit

class SettingAdaptorAdaptor(private val context: Context, private val itemClicked: itemClicked) :
    ListAdapter<SettingDataHolder, SettingAdaptorAdaptor.SettingViewHolder>(diffUtil) {
    inner class SettingViewHolder(private val binding: SettingItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: SettingDataHolder, itemClicked: itemClicked, context: Context) {
            binding.textText.text = data.title
            var flag = false
            if (context.getString(R.string.download_with_wi_fi) == data.title || context.getString(R.string.save_password) == data.title) {
                binding.checkSwitch.show()
            }
            binding.checkSwitch.setOnCheckedChangeListener { _, _ ->
                if (context.getString(R.string.save_password) == data.title) {
                    binding.checkSwitch.isChecked = true
                    return@setOnCheckedChangeListener
                }
                flag = binding.checkSwitch.isChecked
            }
            binding.root.setOnClickListener {
                itemClicked(SettingDataHolder(data.title, flag))
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SettingDataHolder>() {
            override fun areItemsTheSame(
                oldItem: SettingDataHolder,
                newItem: SettingDataHolder
            ) = oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: SettingDataHolder,
                newItem: SettingDataHolder
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val binding =
            SettingItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.makeData(it, itemClicked, context)
        }
    }
}

data class SettingDataHolder(
    val title: String,
    val flag: Boolean = false
)