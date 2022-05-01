package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemGridAdaptor
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemLinearAdaptor
import com.example.videodownloadingline.bottom_sheets.BottomSheetDialogForDownloadFrag
import com.example.videodownloadingline.databinding.DownloadFragmentLayoutBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.utils.BottomType
import com.example.videodownloadingline.utils.OnBottomSheetClick
import com.example.videodownloadingline.utils.RemoteResponse
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel


class DownloadFragment(private val type: String) : Fragment(R.layout.download_fragment_layout),
    OnBottomSheetClick {
    private lateinit var binding: DownloadFragmentLayoutBinding
    private var gridAdaptor: DownloadItemGridAdaptor? = null
    private var linearAdaptor: DownloadItemLinearAdaptor? = null
    private var newFolderDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false
    private var openBottomSheetDialog: BottomSheetDialogForDownloadFrag? = null

    private val viewModel: DownloadFragmentViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        binding.viewTxt.text = getString(R.string.total_vid_view, "View:")
        setUpRecycleView(GridLayoutManager(requireActivity(), 2))
        setUpData()
        changeLayoutView()
        binding.addNewFolderBtn.setOnClickListener {
            createFolderDialog()
        }

    }

    override fun onPause() {
        super.onPause()
        newFolderDialogBox?.dismiss()
        openBottomSheetDialog?.dismiss()
    }

    private fun createFolderDialog() {
        newFolderDialogBox = AddIconsDialogBox()
        newFolderDialogBox?.createNewFolder(context = requireActivity(), listenerForDismiss = {
            newFolderDialogBox?.dismiss()
            isDialogBoxIsVisible = false
        }, listenerForNewFolder = {
            Log.i(TAG, "showDialogBox: $it")
            newFolderDialogBox?.dismiss()
            selectPinOptionDialog(it)
        })
        isDialogBoxIsVisible = true
    }

    private fun selectPinOptionDialog(txt: String) {
        if (newFolderDialogBox == null)
            newFolderDialogBox = AddIconsDialogBox()

        newFolderDialogBox?.showOptionForOptPin(
            context = requireActivity(),
            text = txt,
            listenSetPin = {
                newFolderDialogBox?.dismiss()
            }
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setUpData() {

        viewModel.downloadItemDb.observe(viewLifecycleOwner) {
            when (it) {
                is RemoteResponse.Error -> Log.i(TAG, "setUpData: ${it.exception}")
                is RemoteResponse.Loading -> Log.i(TAG, "setUpData: ${it.data}")
                is RemoteResponse.Success -> {
                    val list = it.data as List<DownloadItems>
                    binding.totalVidTxt.text = getString(R.string.total_vid, list.size)
                    gridAdaptor?.notifyDataSetChanged()
                    gridAdaptor?.submitList(list)
                    linearAdaptor?.notifyDataSetChanged()
                    linearAdaptor?.submitList(list)
                }
            }
        }
    }


    private fun setUpRecycleView(layoutManager: GridLayoutManager) {
        linearAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
            gridAdaptor = DownloadItemGridAdaptor(type = type) {
                openBottomSheet(it.fileTitle)
            }
            adapter = gridAdaptor
        }
    }

    private fun openBottomSheet(video: String) {
        openBottomSheetDialog = BottomSheetDialogForDownloadFrag(video)
        openBottomSheetDialog?.onBottomIconClicked = this
        openBottomSheetDialog?.show(childFragmentManager, "Open Bottom Sheet")
    }

    private fun setUpRecycleView(layoutManager: LinearLayoutManager) {
        gridAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            linearAdaptor = DownloadItemLinearAdaptor(type = type) {
                openBottomSheet(it.fileTitle)
            }
            this.layoutManager = layoutManager
            adapter = linearAdaptor
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeLayoutView() {
        binding.btnForGridView.setOnClickListener {
            setBtnColor(binding.btnForListView, R.color.white)
            setBtnColor(binding.btnForGridView)
            setUpRecycleView(GridLayoutManager(requireActivity(), 2))
            setUpData()
        }
        binding.btnForListView.setOnClickListener {
            setBtnColor(binding.btnForGridView, R.color.white)
            setBtnColor(binding.btnForListView)
            setUpRecycleView((LinearLayoutManager(requireActivity())))
            setUpData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setBtnColor(view: AppCompatImageButton, color: Int = R.color.Surfie_Green_color) {
        view.setColorFilter(
            requireActivity().getColor(color),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
    }


    override fun <T> onItemClicked(type: T) {
        when (BottomType.valueOf(type as String)) {
            BottomType.Delete -> Log.i(TAG, "onItemClicked: working on it")
            BottomType.MoveTo -> Log.i(TAG, "onItemClicked: working on it")
            BottomType.SetPin -> {
                openBottomSheetDialog?.dismiss()
                (parentFragment as MainDownloadFragment).goToSetPin()
            }
        }
    }


}