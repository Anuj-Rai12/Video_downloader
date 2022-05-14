package com.example.videodownloadingline.adaptor.view_tab_open_adaptor

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.DownloadFileItemLinearLayoutBinding
import com.example.videodownloadingline.model.tabitem.TabItem
import com.example.videodownloadingline.utils.RoundedCornersTransformation
import com.example.videodownloadingline.utils.getIconBgLis
import com.example.videodownloadingline.utils.hide
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

typealias ListenerItem = (data: TabItem) -> Unit

class ViewTabOpenAdaptor(private val itemClicked: ListenerItem) :
    ListAdapter<TabItem, ViewTabOpenAdaptor.ListenerViewTabViewHolder>(diffUtil) {
    inner class ListenerViewTabViewHolder(private val binding: DownloadFileItemLinearLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun makeData(data: TabItem, itemClicked: ListenerItem) {
            binding.dataTxtInfo.hide()
            binding.menuIcBtn.hide()
            binding.mainTxtFile.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToBottom = binding.fileThumbNail.id
            }
            binding.mainTxtFile.text = data.url
            binding.root.setOnClickListener {
                itemClicked(data)
            }
            if (data.url == null) {
                binding.fileThumbNail.apply {
                    backgroundTintList = getTintColor(this, getIconBgLis().random())
                    setImageResource(R.drawable.ic_viedoapplogo)
                }
            } else
                Picasso.get()
                    .load(getFabUrl(data.url.toUri()))
                    .resize(80, 80)
                    .centerCrop()
                    .transform(RoundedCornersTransformation(20, 0))
                    .into(binding.fileThumbNail, object : Callback {
                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onSuccess() {
                            binding.fileThumbNail.apply {
                                backgroundTintList = getTintColor(this, getIconBgLis().random())
                            }
                        }

                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onError(e: Exception?) {
                            binding.fileThumbNail.apply {
                                backgroundTintList = getTintColor(this, getIconBgLis().random())
                                setImageResource(R.drawable.ic_viedoapplogo)
                            }
                        }
                    })
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

    private fun getFabUrl(uri: Uri) = uri.buildUpon().path("favicon.ico").build()

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TabItem>() {
            override fun areItemsTheSame(
                oldItem: TabItem,
                newItem: TabItem
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TabItem,
                newItem: TabItem
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListenerViewTabViewHolder {
        val binding = DownloadFileItemLinearLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListenerViewTabViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ListenerViewTabViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.makeData(it, itemClicked)
        }
    }

}