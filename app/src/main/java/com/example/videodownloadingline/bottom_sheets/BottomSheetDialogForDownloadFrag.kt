package com.example.videodownloadingline.bottom_sheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.videoquality.VideoQualityAdaptor
import com.example.videodownloadingline.databinding.ViewBottomSheetDialogBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloadlink.VideoType
import com.example.videodownloadingline.utils.OnBottomSheetClick
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show
import com.example.videodownloadingline.view_model.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogForDownloadFrag(
    private val video: DownloadItems? = null,
    private val enum: Bottom = Bottom.DOWNLOAD_FRAG
) :
    BottomSheetDialogFragment() {

    private lateinit var binding: ViewBottomSheetDialogBinding
    private var deleteDialogBox: AddIconsDialogBox? = null
    var onBottomIconClicked: OnBottomSheetClick? = null
    private lateinit var videoQualityAdaptor: VideoQualityAdaptor
    private var selectedVideoQuality: VideoType? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewBottomSheetDialogBinding.inflate(inflater)
        when (enum) {
            Bottom.DOWNLOAD_FRAG -> {
                binding.titleOfVideo.text = video?.fileTitle
                downloadFrag()
            }
            Bottom.WEB_VIEW_FRAGMENT -> {
                //Show Frag
                changeLayoutFroVideosReq()
                adaptor()
                setData()
                //Close Bottom Sheet
                binding.clsBtn.setOnClickListener {
                    dismiss()
                }
                //Got Response
                binding.downloadButton.setOnClickListener {
                    selectedVideoQuality?.let { res ->
                        onBottomIconClicked?.onItemClicked(res)
                    } ?: Toast.makeText(
                        requireActivity(),
                        "Please Select Video Quality",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setData() {
        videoQualityAdaptor.submitList(list)
        videoQualityAdaptor.notifyDataSetChanged()
    }

    private fun adaptor() {
        binding.mainRec.apply {
            videoQualityAdaptor = VideoQualityAdaptor { flag, data ->
                if (flag) {
                    selectedVideoQuality = data
                    videoQualityAdaptor.selectedItem(data)
                } else {
                    selectedVideoQuality = null
                }
            }
            adapter = videoQualityAdaptor
        }
    }

    private fun changeLayoutFroVideosReq() {
        binding.deleteOfVideo.hide()
        binding.setVideoPin.hide()
        binding.moveTheVideo.hide()
        binding.titleOfVideo.hide()
        binding.space.hide()

        binding.clsBtn.show()
        binding.mainRec.show()
        binding.downloadButton.apply {
            text = this.context.getString(R.string.total_vid_view, "Download")
            show()
        }
    }

    private fun downloadFrag() {
        binding.deleteOfVideo.setOnClickListener {
            openDialogBox()
        }

        binding.titleOfVideo.setOnClickListener {
            onBottomIconClicked?.onItemClicked(Pair(binding.titleOfVideo.text.toString(), video))
        }

        binding.moveTheVideo.setOnClickListener {
            onBottomIconClicked?.onItemClicked(
                Pair(getString(binding.moveTheVideo), video)
            )
        }

        binding.setVideoPin.setOnClickListener {
            onBottomIconClicked?.onItemClicked(
                Pair(getString(binding.setVideoPin), video)
            )
        }
    }

    private fun getString(videoPin: AppCompatTextView): String {
        return videoPin.text.toString().replace("\\s".toRegex(), "")
    }

    private fun openDialogBox() {
        deleteDialogBox = AddIconsDialogBox()
        deleteDialogBox?.showDeleteVideoDialogBox(requireActivity(), listenerNoBtn = {
            deleteDialogBox?.dismiss()
        }, listenerYesBtn = {
            onBottomIconClicked?.onItemClicked(
                Pair(getString(binding.deleteOfVideo), video)
            )
            deleteDialogBox?.dismiss()
        })
    }

    override fun onPause() {
        super.onPause()
        deleteDialogBox?.dismiss()
    }

    override fun getTheme() = R.style.SheetDialog
    override fun onResume() {
        super.onResume()
        MainViewModel.getInstance()?.removeFolder()
    }

    companion object {
        var list: List<VideoType>? = null

        enum class Bottom {
            DOWNLOAD_FRAG,
            WEB_VIEW_FRAGMENT
        }
    }
}