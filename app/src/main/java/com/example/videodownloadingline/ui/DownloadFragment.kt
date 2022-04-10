package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
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
import com.example.videodownloadingline.utils.RemoteResponse
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel


class DownloadFragment : Fragment(R.layout.download_fragment_layout) {
    private lateinit var binding: DownloadFragmentLayoutBinding
    private var gridAdaptor: DownloadItemGridAdaptor? = null
    private var linearAdaptor: DownloadItemLinearAdaptor? = null
    private var newFolderDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false
    private var openBottomSheetDialog: BottomSheetDialogForDownloadFrag? = null

    private val viewModel: DownloadFragmentViewModel by viewModels()

    private val getStringArray by lazy {
        resources.getStringArray(R.array.sorting_item)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        initial()
        menuClickListener()
        binding.toolBarMainActivity.searchBoxEd.doOnTextChanged { text, _, _, _ ->
            Log.i(TAG, "onViewCreated: $text")
        }
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
        })
        isDialogBoxIsVisible = true
    }


    private fun showSortingDialog() {
        if (newFolderDialogBox == null)
            newFolderDialogBox = AddIconsDialogBox()

        newFolderDialogBox?.displaySortingViewRecycle(
            context = requireActivity(),
            getStringArray,
            listenerForNewFolder = {
                //   newFolderDialogBox?.dismiss()
            }
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setUpData() {

        viewModel.getDownloadItem.observe(viewLifecycleOwner) {
            when (it) {
                is RemoteResponse.Error -> Log.i(TAG, "setUpData: ${it.exception}")
                is RemoteResponse.Loading -> Log.i(TAG, "setUpData: ${it.data}")
                is RemoteResponse.Success -> {
                    val list = it.data as List<DownloadItems>
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
            gridAdaptor = DownloadItemGridAdaptor {
                openBottomSheet(it.fileTitle)
            }
            adapter = gridAdaptor
        }
    }

    private fun openBottomSheet(video: String) {
        openBottomSheetDialog = BottomSheetDialogForDownloadFrag(video)
        openBottomSheetDialog?.show(childFragmentManager, "Open Bottom Sheet")
    }

    private fun setUpRecycleView(layoutManager: LinearLayoutManager) {
        gridAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            linearAdaptor = DownloadItemLinearAdaptor {
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun menuClickListener() {

        binding.toolBarMainActivity.srcBtn.setOnClickListener {
            binding.toolBarMainActivity.toolbarHomeBtn.apply {
                setImageResource(R.drawable.ic_arrow_back)
                show()
            }
            binding.toolBarMainActivity.searchBoxEd.apply {
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    rightToLeft = it.id
                }
                binding.toolBarMainActivity.toolBarLayout.title = ""
                show()
            }
            it.hide()
        }


        binding.toolBarMainActivity.toolbarHomeBtn.setOnClickListener {
            it.hide()
            binding.toolBarMainActivity.searchBoxEd.hide()
            binding.toolBarMainActivity.toolBarLayout.title =
                getString(R.string.content_description_down)
            binding.toolBarMainActivity.srcBtn.show()
        }

        binding.toolBarMainActivity.threeBotMnuBtn.setOnClickListener {
            showSortingDialog()
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        binding.toolBarMainActivity.totalTabOp.hide()
        binding.toolBarMainActivity.toolbarHomeBtn.hide()
        binding.toolBarMainActivity.srcBtn.show()
        binding.toolBarMainActivity.threeBotMnuBtn.setImageResource(R.drawable.ic_new_list_vew)
        binding.toolBarMainActivity.searchBoxEd.hide()
        binding.totalVidTxt.text = getString(R.string.total_vid, 1100)
        binding.viewTxt.text = getString(R.string.total_vid_view, "view:")
        setBtnColor(binding.btnForGridView)
        binding.toolBarMainActivity.toolBarLayout.title =
            getString(R.string.content_description_down)
        binding.toolBarMainActivity.toolBarLayout.setTitleTextColor(Color.WHITE)
    }


}