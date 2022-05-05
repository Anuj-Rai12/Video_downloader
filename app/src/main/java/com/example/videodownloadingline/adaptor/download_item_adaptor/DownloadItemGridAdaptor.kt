package com.example.videodownloadingline.adaptor.download_item_adaptor

import android.annotation.SuppressLint
import android.content.res.Resources
import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Size
import android.util.TypedValue
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFileItemGridLayoutBinding
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloaditem.TypeOfDownload
import com.example.videodownloadingline.ui.whatsapp.WhatsappActivity
import com.example.videodownloadingline.utils.DownloadProgressLiveData
import com.example.videodownloadingline.utils.hide
import java.io.File


typealias ItemClickedListener = (data: DownloadItems) -> Unit

class DownloadItemGridAdaptor(
    private val type: String,
    private val itemClicked: ItemClickedListener
) :
    ListAdapter<DownloadItems, DownloadItemGridAdaptor.DownloadItemGridViewHolder>(diffUtil) {


    private var isWhatsApp = false

    inner class DownloadItemGridViewHolder(private val binding: DownloadFileItemGridLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun makeData(data: DownloadItems, itemClicked: ItemClickedListener) {
            binding.root.setOnClickListener {
                itemClicked(data)
            }
            when (TypeOfDownload.valueOf(type)) {
                TypeOfDownload.IsFolder -> binding.fileThumbNail.setImageResource(R.drawable.ic_viedoapplogo)
                TypeOfDownload.IsFiles -> binding.fileThumbNail.setImageResource(R.drawable.ic_viedoapplogo)
                TypeOfDownload.SecureFolder -> binding.fileThumbNail.setImageResource(R.drawable.ic_video_pin)
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
                Toast.makeText(it.context, "${data.fileTitle} menu Clicked", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        @SuppressLint("StringFormatMatches")
        fun whatsAppDownloadData(data: DownloadItems, itemClicked: ItemClickedListener) {
            binding.menuBtn.hide()
            binding.root.setOnClickListener {
                itemClicked.invoke(data)
            }
            when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
                WhatsappActivity.Companion.WhatsappClick.IsImage -> {
                    binding.fileThumbNail.apply {
                        setPadding(0, 0, 0, 0)
                        scaleType = ImageView.ScaleType.FIT_XY
                        setImageURI(data.fileLoc.toUri())
                    }
                }
                WhatsappActivity.Companion.WhatsappClick.IsVideo -> {
                    val bm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ThumbnailUtils.createVideoThumbnail(
                            File(data.fileLoc.toUri().path!!),
                            Size(200f.toPx(), 200f.toPx()),
                            CancellationSignal()
                        )
                    } else {
                        ThumbnailUtils.createVideoThumbnail(
                            data.fileLoc.toUri().path!!,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                    }
                    binding.fileThumbNail.apply {
                        scaleType = ImageView.ScaleType.FIT_XY
                        setImageBitmap(bm)
                    }
                }
            }

            var size = DownloadProgressLiveData.getMb(data.fileSize)
            var str = "${size}MB"
            if (size >= 0) {
                size = DownloadProgressLiveData.getKb(data.fileSize)
                str = "${size}KB"
            }
            binding.titleTxt.apply {
                text = this.context.getString(
                    R.string.total_vid_view,
                    "${str}\n\n${data.createdCurrentTimeData}"
                )
            }
        }

        private fun Float.toPx() =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this,
                Resources.getSystem().displayMetrics
            )
                .toInt()
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


