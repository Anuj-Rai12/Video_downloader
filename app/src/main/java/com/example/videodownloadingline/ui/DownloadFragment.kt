package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemGridAdaptor
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemLinearAdaptor
import com.example.videodownloadingline.bottom_sheets.BottomSheetDialogForDownloadFrag
import com.example.videodownloadingline.databinding.DownloadFragmentLayoutBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel


class DownloadFragment : Fragment(R.layout.download_fragment_layout), OnBottomSheetClick {
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
        setUpRecycleView(GridLayoutManager(requireActivity(), 2))
        setUpData()
        changeLayoutView()
        binding.addNewFolderBtn.setOnClickListener {
            createFolderDialog()
        }
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.download_frag_menu, menu)
        val icSort = menu.findItem(R.id.menu_box)
        icSort.setOnMenuItemClickListener {
            showSortingDialog()
            return@setOnMenuItemClickListener true
        }

        super.onCreateOptionsMenu(menu, inflater)
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
                    MainActivity.bottomNavigation?.show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title = getString(R.string.content_description_down)
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
        openBottomSheetDialog?.onBottomIconClicked = this
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
    private fun initial() {
        MainActivity.bottomNavigation?.show()
    }

    override fun onItemClicked(type: String) {
        when (BottomType.valueOf(type)) {
            BottomType.Delete -> Log.i(TAG, "onItemClicked: working on it")
            BottomType.MoveTo -> Log.i(TAG, "onItemClicked: working on it")
            BottomType.SetPin -> {
                MainActivity.bottomNavigation?.hide()
                MainActivity.viewPager2?.currentItem = 3
            }
        }
    }


}