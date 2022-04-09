package com.example.videodownloadingline.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.ViewBottomSheetDialogBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogForDownloadFrag(private val videoTitle: String) :
    BottomSheetDialogFragment() {

    private lateinit var binding: ViewBottomSheetDialogBinding
    private var deleteDialogBox: AddIconsDialogBox? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewBottomSheetDialogBinding.inflate(inflater)
        binding.deleteOfVideo.setOnClickListener {
            openDialogBox()
        }
        binding.titleOfVideo.text = videoTitle
        return binding.root
    }

    private fun openDialogBox() {
        deleteDialogBox = AddIconsDialogBox()
        deleteDialogBox?.showDeleteVideoDialogBox(requireActivity(), listenerNoBtn = {
            deleteDialogBox?.dismiss()
        }, listenerYesBtn = {
            deleteDialogBox?.dismiss()
        })
    }

    override fun onPause() {
        super.onPause()
        deleteDialogBox?.dismiss()
    }

    override fun getTheme() = R.style.SheetDialog

}