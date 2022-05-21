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
import com.example.videodownloadingline.model.downloaditem.Category
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloaditem.TypeOfDownload
import com.example.videodownloadingline.model.securefolder.SecureFolderItem
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel
import com.google.android.material.snackbar.Snackbar


class DownloadFragment(private val type: String) : Fragment(R.layout.download_fragment_layout),
    OnBottomSheetClick {
    private lateinit var binding: DownloadFragmentLayoutBinding
    private var gridAdaptor: DownloadItemGridAdaptor? = null
    private var linearAdaptor: DownloadItemLinearAdaptor? = null
    private var newFolderDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false
    private var openBottomSheetDialog: BottomSheetDialogForDownloadFrag? = null


    private val viewModel: DownloadFragmentViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        binding.viewTxt.text = getString(R.string.total_vid_view, "View:")
        viewModel.folderCreateId.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                binding.root.showSandbar("File is Moved", color = Color.GREEN)
            }
        }

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
                fetchList()
            }
            TypeOfDownload.IsFiles -> {
                binding.addNewFolderBtn.hide()
                setUpData()
            }
            TypeOfDownload.SecureFolder -> {
                fetchList()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchList() {
        viewModel.folderItem.observe(viewLifecycleOwner) {
            setSize(it.size)
            gridAdaptor?.notifyDataSetChanged()
            gridAdaptor?.submitList(it)
            linearAdaptor?.notifyDataSetChanged()
            linearAdaptor?.submitList(it)
        }
    }

    override fun onPause() {
        super.onPause()
        newFolderDialogBox?.dismiss()
        openBottomSheetDialog?.dismiss()
        viewModel.makeFolderCreateIdNull()
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
                    val secureFolderItem = SecureFolderItem(
                        0, folder = txt, src = getFileDir(
                            "/$txt",
                            requireContext()
                        ).absolutePath, pin = ""
                    )
                    (parentFragment as MainDownloadFragment).goToSetPin(
                        secureFolderItem = secureFolderItem,
                        category = Category.PinFolder.name
                    )
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
        when (TypeOfDownload.valueOf(type)) {
            TypeOfDownload.IsFolder -> {
                (parentFragment as MainDownloadFragment).goToViewTab(
                    downloadItems = video,
                    Category.NormalFolder.name
                )
            }
            TypeOfDownload.IsFiles -> {
                openBottomSheetDialog = BottomSheetDialogForDownloadFrag(video)
                openBottomSheetDialog?.onBottomIconClicked = this
                openBottomSheetDialog?.show(childFragmentManager, "Open Bottom Sheet")
            }
            TypeOfDownload.SecureFolder -> {
                val secureFolderItem = SecureFolderItem(0, video.fileTitle, video.fileLoc, "")
                (parentFragment as MainDownloadFragment).goToSetPin(
                    secureFolderItem = secureFolderItem,
                    category = Category.PinFolder.name,
                    isClickFlag = true
                )
            }
        }
    }

    private fun setUpRecycleView(layoutManager: LinearLayoutManager) {
        gridAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            linearAdaptor = DownloadItemLinearAdaptor(type = type, requireContext()) {
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


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun <T> onItemClicked(type: T) {
        val response = type as Pair<*, *>
        val data = response.second as DownloadItems?
        if (data == null) {
            requireActivity().toastMsg("Something Went Wrong")
            return
        }
        openBottomSheetDialog?.dismiss()
        try {
            when (BottomType.valueOf(response.first.toString())) {
                BottomType.Delete -> {
                    data.let { downloadItems: DownloadItems ->
                        activity?.let {
                            it.deleteVideo(downloadItems.fileLoc)
                            viewModel.deleteDownload(downloadItems)
                        }
                    }
                }
                BottomType.MoveTo -> {
                    checkFolderDirDialog(listOf("Secure Folder", "Folder"), data)
                }
                BottomType.SetPin -> {
                    if (data.category == Category.PinFolder.name || data.setPin.isNotEmpty()) {
                        binding.root.showSandbar(
                            "Folder is Already Secure", Snackbar.LENGTH_LONG,
                            color = Color.CYAN
                        )
                        return
                    }
                    (parentFragment as MainDownloadFragment).goToSetPin(
                        downloadItems = data,
                        category = Category.NormalFolder.name
                    )
                }
            }
        } catch (e: Exception) {
            // if is Secure then Got To Secure Folder or Set Pin ?
            activity?.let { act ->
                (response.second as DownloadItems?)?.let { items ->
                    act.playVideo(items.fileLoc, items.fileExtensionType)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkFolderDirDialog(folder: List<String>, response: DownloadItems) {
        if (newFolderDialogBox == null)
            newFolderDialogBox = AddIconsDialogBox()

        newFolderDialogBox?.displayFolderViewRecycle(
            context = requireActivity(),
            folder.toTypedArray(),
            title = "Dir",
            lifecycleOwner = viewLifecycleOwner,
            listenerForNewFolder = { _, filePath, _, flag ->
                newFolderDialogBox?.dismiss()
                val targetPath = filePath + "/" + response.fileThumbLoc
                activity?.apply {
                    moveFile(inputPath = response.fileLoc, outputPath = targetPath).also {
                        nextProcess(it, response, targetPath, flag)
                    }
                }
            }, listenEmpty = { flag ->
                newFolderDialogBox?.dismiss()
                if (flag == true) {
                    MainDownloadFragment.downloadViewPage?.currentItem = 1
                } else {
                    MainDownloadFragment.downloadViewPage?.currentItem = 2
                }
                activity?.toastMsg("Create a Folder to Move File")
            })
    }

    private fun nextProcess(
        it: Boolean,
        response: DownloadItems,
        targetPath: String,
        flag: Boolean
    ) {
        val setPin: String?
        val category = if (flag) {//Normal Folder
            setPin = null
            Category.NormalFolder
        } else {//Secure Folder
            setPin = ""
            Category.PinFolder
        }
        Log.i(TAG, "nextProcess: $category and Flag is ?  $flag")
        if (it) {
            viewModel.updateDownloadItem(response, targetPath, category, setPin)
        } else {
            binding.root.showSandbar("File is Not Moved", color = Color.RED)
        }
    }
}