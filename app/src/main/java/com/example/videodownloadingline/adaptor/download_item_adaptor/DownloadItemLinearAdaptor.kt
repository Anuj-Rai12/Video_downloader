package com.example.videodownloadingline.adaptor.download_item_adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFileItemLinearLayoutBinding
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloaditem.TypeOfDownload
import com.example.videodownloadingline.utils.getThumbNail
import com.example.videodownloadingline.utils.hide


typealias ItemForLinearLayoutListener = (data: DownloadItems) -> Unit

class DownloadItemLinearAdaptor(
    private val type: String,
    private val context: Context,
    private val itemClicked: ItemForLinearLayoutListener
) :
    ListAdapter<DownloadItems, DownloadItemLinearAdaptor.DownloadItemLinearViewHolder>(diffUtil) {
    inner class DownloadItemLinearViewHolder(private val binding: DownloadFileItemLinearLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun makeData(data: DownloadItems, itemClicked: ItemForLinearLayoutListener) {
            binding.root.setOnClickListener {
                itemClicked(data)
            }
            binding.menuIcBtn.hide()
            when (TypeOfDownload.valueOf(type)) {
                TypeOfDownload.IsFolder -> binding.fileThumbNail.setImageResource(R.drawable.ic_folder)
                TypeOfDownload.IsFiles -> {
                    val bitmap = getThumbNail(data.fileLoc)
                    bitmap?.let {
                        binding.fileThumbNail.apply {
                            setPadding(0, 0, 0, 0)
                            scaleType = ImageView.ScaleType.FIT_XY
                            setImageBitmap(it)
                        }
                        return@let
                    } ?: binding.fileThumbNail.setImageResource(R.drawable.ic_viedoapplogo)
                }
                TypeOfDownload.SecureFolder -> binding.fileThumbNail.setImageResource(R.drawable.ic_video_pin)
            }
            binding.menuIcBtn.setOnClickListener {
                Toast.makeText(it.context, "${data.fileTitle} Item Clicked", Toast.LENGTH_SHORT)
                    .show()
            }
            binding.mainTxtFile.text = data.fileTitle
            binding.dataTxtInfo.apply {
                text = context.getString(
                    R.string.linear_download_info,
                    DownloadItemGridAdaptor.getSizeKbOrMb(data.fileSize).first,
                    data.createdCurrentTimeData
                )
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