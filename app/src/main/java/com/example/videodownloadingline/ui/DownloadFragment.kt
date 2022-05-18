package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemGridAdaptor
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemLinearAdaptor
import com.example.videodownloadingline.bottom_sheets.BottomSheetDialogForDownloadFrag
import com.example.videodownloadingline.databinding.DownloadFragmentLayoutBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloaditem.TypeOfDownload
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel
import com.example.videodownloadingline.view_model.MainViewModel


class DownloadFragment(private val type: String) : Fragment(R.layout.download_fragment_layout),
    OnBottomSheetClick {
    private lateinit var binding: DownloadFragmentLayoutBinding
    private var gridAdaptor: DownloadItemGridAdaptor? = null
    private var linearAdaptor: DownloadItemLinearAdaptor? = null
    private var newFolderDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false
    private var openBottomSheetDialog: BottomSheetDialogForDownloadFrag? = null
    private var isOptionFilesFlag = true

    private val viewModel: DownloadFragmentViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        binding.viewTxt.text = getString(R.string.total_vid_view, "View:")
        setUpRecycleView(GridLayoutManager(requireActivity(), 2))
        changeLayoutView()
        soAllFileOrFolder()
        binding.addNewFolderBtn.setOnClickListener {
            createFolderDialog()
        }

    }

    private fun soAllFileOrFolder() {
        when (TypeOfDownload.valueOf(type)) {
            TypeOfDownload.IsFolder -> {
                isOptionFilesFlag = false
                fetchList()
            }
            TypeOfDownload.IsFiles -> {
                isOptionFilesFlag = true
                binding.addNewFolderBtn.hide()
                setUpData()
            }
            TypeOfDownload.SecureFolder -> {
                isOptionFilesFlag = false
                fetchList()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchList() {
        viewModel.folderItem.observe(viewLifecycleOwner) {
            setSize(it.size)
            if (!it.isNullOrEmpty()) {
                gridAdaptor?.notifyDataSetChanged()
                gridAdaptor?.submitList(it)
                linearAdaptor?.notifyDataSetChanged()
                linearAdaptor?.submitList(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        newFolderDialogBox?.dismiss()
        openBottomSheetDialog?.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.M)
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun selectPinOptionDialog(txt: String) {
        if (newFolderDialogBox == null)
            newFolderDialogBox = AddIconsDialogBox()

        newFolderDialogBox?.showOptionForOptPin(
            context = requireActivity(),
            text = txt,
            listenSetPin = {
                if (it) {//WithOut Pin
                    val targetPath = getFileDir(
                        "${getString(R.string.file_path_2)}/${txt}",
                        requireContext(),
                        false
                    )
                    if (!targetPath.exists()) {
                        targetPath.mkdirs()
                        binding.root.showSandbar("File is Created", color = Color.GREEN)
                        getFolderFromDownloads()
                    } else {
                        requireActivity().toastMsg("File is Already Created")
                    }
                    MainDownloadFragment.downloadViewPage?.currentItem = 1
                } else {//With Pin
                    val targetPath = getFileDir("/$txt", requireContext())
                    if (!targetPath.exists()) {
                        targetPath.mkdirs()
                        binding.root.showSandbar("File is Created", color = Color.GREEN)
                        getFolderFromSecure()
                    } else {
                        requireActivity().toastMsg("File is Already Created")
                    }
                    MainDownloadFragment.downloadViewPage?.currentItem = 2
                }
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
                    if (it.data is Boolean) {
                        Log.i(TAG, "setUpData: List is Empty")
                    } else {
                        val list = it.data as List<DownloadItems>
                        Log.i(TAG, "setUpData: $list")
                        setSize(list.size)
                        gridAdaptor?.notifyDataSetChanged()
                        gridAdaptor?.submitList(list)
                        linearAdaptor?.notifyDataSetChanged()
                        linearAdaptor?.submitList(list)
                    }
                }
            }
        }
    }

    private fun setSize(size: Int) {
        when (TypeOfDownload.valueOf(type)) {
            TypeOfDownload.IsFolder -> binding.totalVidTxt.text =
                getString(R.string.total_vid_view, "$size Folder")
            TypeOfDownload.IsFiles -> binding.totalVidTxt.text = getString(R.string.total_vid, size)
            TypeOfDownload.SecureFolder -> binding.totalVidTxt.text =
                getString(R.string.total_vid_view, "$size Private Folder")
        }
    }

    override fun onResume() {
        super.onResume()
        when (TypeOfDownload.valueOf(type)) {
            TypeOfDownload.IsFolder -> getFolderFromDownloads()
            TypeOfDownload.IsFiles -> viewModel.fetch()
            TypeOfDownload.SecureFolder -> getFolderFromSecure()
        }
    }

    private fun getFolderFromDownloads(path: String = getString(R.string.file_path_2)) {
        val targetPath = getFileDir(path, requireContext(), false)
        viewModel.getListOfFolder(targetPath)
    }

    private fun getFolderFromSecure(path: String = "") {
        val filePath = getFileDir(path, requireContext())
        viewModel.getListOfFolder(filePath)
    }

    private fun setUpRecycleView(layoutManager: GridLayoutManager) {
        linearAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
            gridAdaptor = DownloadItemGridAdaptor(type = type, requireContext()) { data, _ ->
                openBottomSheet(data)
            }
            adapter = gridAdaptor
        }
    }

    private fun openBottomSheet(video: DownloadItems) {
        if (!isOptionFilesFlag) return
        openBottomSheetDialog = BottomSheetDialogForDownloadFrag(video)
        openBottomSheetDialog?.onBottomIconClicked = this
        openBottomSheetDialog?.show(childFragmentManager, "Open Bottom Sheet")
    }

    private fun setUpRecycleView(layoutManager: LinearLayoutManager) {
        gridAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            linearAdaptor = DownloadItemLinearAdaptor(type = type) {
                openBottomSheet(it)
            }
            this.layoutManager = layoutManager
            adapter = linearAdaptor
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeLayoutView() {
        binding.btnForGridView.setOnClickListener {
            requireActivity().apply {
                setBtnColor(binding.btnForListView, R.color.white)
                setBtnColor(binding.btnForGridView)
            }
            setUpRecycleView(GridLayoutManager(requireActivity(), 2))
            soAllFileOrFolder()
        }
        binding.btnForListView.setOnClickListener {
            requireActivity().setBtnColor(binding.btnForGridView, R.color.white)
            requireActivity().setBtnColor(binding.btnForListView)
            setUpRecycleView((LinearLayoutManager(requireActivity())))
            soAllFileOrFolder()
        }
    }


    override fun <T> onItemClicked(type: T) {
        val response = type as Pair<*, *>
        openBottomSheetDialog?.dismiss()
        try {
            when (BottomType.valueOf(response.first.toString())) {
                BottomType.Delete -> {
                    (response.second as DownloadItems?)?.let { downloadItems: DownloadItems ->
                        activity?.let {
                            it.deleteVideo("/VideoDownload/${downloadItems.fileThumbLoc}")
                            viewModel.deleteDownload(downloadItems)
                        }
                    }
                }
                BottomType.MoveTo -> {
                    checkFolderDirDialog(listOf("Secure Folder", "Folder"))
                }
                BottomType.SetPin -> {
                    (parentFragment as MainDownloadFragment).goToSetPin()
                }
            }
        } catch (e: Exception) {
            activity?.let { act ->
                (response.second as DownloadItems?)?.let { items ->
                    act.playVideo(items.fileLoc, items.fileExtensionType)
                }
            }
        }
    }

    private fun checkFolderDirDialog(folder: List<String>) {
        if (newFolderDialogBox == null) {
            newFolderDialogBox = AddIconsDialogBox()
        }
        newFolderDialogBox?.displayFolderViewRecycle(
            context = requireActivity(),
            folder.toTypedArray(),
            title = "Dir", lifecycleOwner = viewLifecycleOwner, required = requireContext()
        ) { data, _, flag ->
            if (!flag && data.isNotEmpty() || data.isNotBlank()) {
                newFolderDialogBox?.dismiss()
                activity?.toastMsg(data + flag)
                MainViewModel.getInstance()?.removeFolder()
            }
        }
    }
}