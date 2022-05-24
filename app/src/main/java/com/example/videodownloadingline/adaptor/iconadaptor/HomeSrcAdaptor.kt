package com.example.videodownloadingline.adaptor.iconadaptor

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.HomeSrcIconsLayoutBinding
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.RoundedCornersTransformation
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


typealias ItemClicked = (data: HomeSrcIcon, isAddIcon: Boolean) -> Unit
typealias ItemLongClicked = (data: HomeSrcIcon, isAddIcon: Boolean) -> Unit

class HomeSrcAdaptor(
    private val itemClicked: ItemClicked,
    private val itemLongClicked: ItemLongClicked
) :
    ListAdapter<HomeSrcIcon, HomeSrcAdaptor.HomeSrcIconViewHolder>(diffUtil) {
    inner class HomeSrcIconViewHolder(private val binding: HomeSrcIconsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.M)
        fun makeData(
            data: HomeSrcIcon,
            itemClicked: ItemClicked,
            itemLongClicked: ItemLongClicked
        ) {

            if (data.name != null) {
                val fab = getFabUrl(Uri.parse(data.url))
                if (fab != null) {
                    binding.addHomeSrcBtn.show()
                    binding.homeSrcName.apply {
                        updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = binding.addHomeSrcBtn.id
                            leftToLeft = binding.addHomeSrcBtn.id
                            rightToRight = binding.addHomeSrcBtn.id
                        }
                        text = data.name
                    }
                    Picasso.get()
                        .load(fab)
                        .resize(80, 80)
                        .centerCrop()
                        .transform(RoundedCornersTransformation(20, 0))
                        .into(binding.addHomeSrcBtn, object : Callback {
                            override fun onSuccess() {
                                binding.addHomeSrcBtn.apply {
                                    backgroundTintList = getTintColor(this, data.bg)
                                }
                            }

                            override fun onError(e: Exception?) {
                                binding.addHomeSrcBtn.hide()
                                binding.nameTxt.apply {
                                    show()
                                    backgroundTintList = getTintColor(this, data.bg)
                                    text = data.name.first().uppercaseChar().toString()
                                }
                                binding.homeSrcName.apply {
                                    show()
                                    updateLayoutParams<ConstraintLayout.LayoutParams> {
                                        topToBottom = binding.nameTxt.id
                                        leftToLeft = binding.nameTxt.id
                                        rightToRight = binding.nameTxt.id
                                    }
                                    text = data.name
                                }
                            }
                        })

                } else {
                    binding.addHomeSrcBtn.hide()
                    binding.nameTxt.apply {
                        show()
                        backgroundTintList = getTintColor(this, data.bg)
                        text = data.name.first().uppercaseChar().toString()
                    }
                    binding.homeSrcName.apply {
                        show()
                        text = data.name
                    }
                }
            } else {
                binding.homeSrcName.hide()
                binding.nameTxt.hide()
                binding.addHomeSrcBtn.apply {
                    show()
                    setImageResource(R.drawable.ic_add)
                    backgroundTintList = getTintColor(this, data.bg)
                }
            }
            binding.root.setOnClickListener {
                if (data.name.isNullOrEmpty()) {
                    itemClicked(data, true)
                } else {
                    itemClicked(data, false)
                }
            }


            binding.root.setOnLongClickListener {
                if (data.name.isNullOrEmpty()) {
                    itemLongClicked(data, true)
                } else {
                    itemLongClicked(data, false)
                }
                return@setOnLongClickListener true
            }


        }

        private fun getFabUrl(uri: Uri) = uri.buildUpon().path("favicon.ico").build()

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
            holder.makeData(it, itemClicked, itemLongClicked)
        }
    }

}