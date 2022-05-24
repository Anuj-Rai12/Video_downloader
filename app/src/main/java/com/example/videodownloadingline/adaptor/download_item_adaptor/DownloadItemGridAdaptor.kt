package com.example.videodownloadingline.adaptor.download_item_adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFileItemGridLayoutBinding
import com.example.videodownloadingline.model.downloaditem.Category
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloaditem.TypeOfDownload
import com.example.videodownloadingline.ui.whatsapp.WhatsappActivity
import com.example.videodownloadingline.utils.DownloadProgressLiveData
import com.example.videodownloadingline.utils.getThumbNail
import com.example.videodownloadingline.utils.hide


typealias ItemClickedListener = (data: DownloadItems, bitmap: Bitmap?) -> Unit

class DownloadItemGridAdaptor(
    private val type: String,
    private val context: Context,
    private val itemClicked: ItemClickedListener
) :
    ListAdapter<DownloadItems, DownloadItemGridAdaptor.DownloadItemGridViewHolder>(diffUtil) {


    private var isWhatsApp = false

    inner class DownloadItemGridViewHolder(private val binding: DownloadFileItemGridLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: DownloadItems, itemClicked: ItemClickedListener) {
            binding.menuBtn.hide()
            binding.root.setOnClickListener {
                itemClicked(data, null)
            }
            when (TypeOfDownload.valueOf(type)) {
                TypeOfDownload.IsFolder -> {
                    binding.fileThumbNail.setImageResource(R.drawable.ic_folder)
                }
                TypeOfDownload.IsFiles -> {
                    when (Category.valueOf(data.category)) {
                        Category.PinFolder -> {
                            binding.fileThumbNail.setImageResource(R.drawable.ic_video_pin)
                        }
                        Category.NormalFolder -> {
                            val bm = getThumbNail(data.fileLoc)
                            bm?.let {
                                binding.fileThumbNail.apply {
                                    setPadding(0, 0, 0, 0)
                                    scaleType = ImageView.ScaleType.FIT_XY
                                    setImageBitmap(it)
                                }
                                return@let
                            } ?: binding.fileThumbNail.setImageResource(R.drawable.ic_viedoapplogo)
                        }
                    }
                }
                TypeOfDownload.SecureFolder -> binding.fileThumbNail.setImageResource(R.drawable.ic_video_pin)
            }
            binding.titleTxt.apply {
                text = this.context.getString(
                    R.string.grid_download_info,
                    data.fileTitle,
                    getSizeKbOrMb(data.fileSize).first,
                    data.createdCurrentTimeData
                )
            }
            binding.menuBtn.setOnClickListener {
                Toast.makeText(it.context, "${data.fileTitle} menu Clicked", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        @SuppressLint("StringFormatMatches")
        fun whatsAppDownloadData(data: DownloadItems, itemClicked: ItemClickedListener) {
            binding.menuBtn.hide()
            binding.fileThumbNail.apply {
                setPadding(0, 0, 0, 0)
                scaleType = ImageView.ScaleType.FIT_XY
            }

            binding.root.setOnClickListener {
                itemClicked.invoke(data, (binding.fileThumbNail.drawable as BitmapDrawable).bitmap)
            }
            when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
                WhatsappActivity.Companion.WhatsappClick.IsImage -> {
                    binding.fileThumbNail.setImageURI(data.fileLoc.toUri())
                }
                WhatsappActivity.Companion.WhatsappClick.IsVideo -> {
                    val bm = getThumbNail(data.fileLoc.toUri().path!!)
                    binding.fileThumbNail.setImageBitmap(bm)
                }
            }

            binding.titleTxt.apply {
                text = this.context.getString(
                    R.string.total_vid_view,
                    "${getSizeKbOrMb(data.fileSize).first}\n\n${data.createdCurrentTimeData}"
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

        fun getSizeKbOrMb(len: Long): Pair<String, Int> {
            return DownloadProgressLiveData.getMb(len)
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


    @SuppressLint("NotifyDataSetChanged")
    fun changeWhatsApp(flag: Boolean) {
        isWhatsApp = flag
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DownloadItemGridViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            if (!isWhatsApp)
                holder.makeData(it, itemClicked)
            else
                holder.whatsAppDownloadData(data = it, itemClicked)
        }
    }

}


