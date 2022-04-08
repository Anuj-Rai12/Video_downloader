package com.example.videodownloadingline.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.ViewBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogForDownloadFrag(private val videoTitle: String) :
    BottomSheetDialogFragment() {

    private lateinit var binding: ViewBottomSheetDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewBottomSheetDialogBinding.inflate(inflater)

        binding.titleOfVideo.text = videoTitle
        return binding.root
    }

    override fun getTheme()= R.style.SheetDialog

}