package com.example.videodownloadingline.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.ViewBottomSheetDialogBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.utils.OnBottomSheetClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogForDownloadFrag(
    private val videoTitle: String,
    private val enum: Bottom = Bottom.DOWNLOAD_FRAG
) :
    BottomSheetDialogFragment() {

    private lateinit var binding: ViewBottomSheetDialogBinding
    private var deleteDialogBox: AddIconsDialogBox? = null
    var onBottomIconClicked: OnBottomSheetClick? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewBottomSheetDialogBinding.inflate(inflater)
        when (enum) {
            Bottom.DOWNLOAD_FRAG -> {
                downloadFrag()
            }
            Bottom.WEB_VIEW_FRAGMENT -> {
                //Show Frag
            }
        }
        binding.titleOfVideo.text = videoTitle
        return binding.root
    }

    private fun downloadFrag() {
        binding.deleteOfVideo.setOnClickListener {
            openDialogBox()
        }

        binding.moveTheVideo.setOnClickListener {
            onBottomIconClicked?.onItemClicked(
                binding.moveTheVideo.text.toString().replace("\\s".toRegex(), "")
            )
        }


        binding.setVideoPin.setOnClickListener {
            onBottomIconClicked?.onItemClicked(
                binding.setVideoPin.text.toString().replace("\\s".toRegex(), "")
            )
        }
    }

    private fun openDialogBox() {
        deleteDialogBox = AddIconsDialogBox()
        deleteDialogBox?.showDeleteVideoDialogBox(requireActivity(), listenerNoBtn = {
            deleteDialogBox?.dismiss()
        }, listenerYesBtn = {
            onBottomIconClicked?.onItemClicked(
                binding.deleteOfVideo.text.toString().replace("\\s".toRegex(), "")
            )
            deleteDialogBox?.dismiss()
        })
    }

    override fun onPause() {
        super.onPause()
        deleteDialogBox?.dismiss()
    }

    override fun getTheme() = R.style.SheetDialog

    companion object {
        enum class Bottom {
            DOWNLOAD_FRAG,
            WEB_VIEW_FRAGMENT
        }
    }
}