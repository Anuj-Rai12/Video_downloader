package com.example.videodownloadingline.ui.whatsapp


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videodownloadingline.BuildConfig
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemGridAdaptor
import com.example.videodownloadingline.databinding.FragmentsWhatsappLayoutBinding
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel
import java.io.File


class WhatsAppFragment(private val type: String) : Fragment(R.layout.fragments_whatsapp_layout) {
    private lateinit var binding: FragmentsWhatsappLayoutBinding
    private lateinit var adaptorGird: DownloadItemGridAdaptor
    private var permissionManager: PermissionManager? = null
    private val list = mutableListOf<DownloadItems>()

    private val downloadViewModel: DownloadFragmentViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentsWhatsappLayoutBinding.bind(view)
        permissionManager = PermissionManager.from(this)
        requestPermission()
        recycleAdaptor()
        downloadViewModel.event.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { string ->
                binding.root.showSandbar(string, color = Color.GREEN)
            }
        }
        permissionManager?.checkPermission {
            addWhatsApp(it)
        }
    }

    private fun addWhatsApp(flag:Boolean) {
        if (flag) {
            val res =
                getWhatsappStoryPath(WhatsappActivity.Companion.WhatsappClick.valueOf(type))
            when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
                WhatsappActivity.Companion.WhatsappClick.IsImage -> {
                    list.clear()
                    setData(res)
                    adaptorGird.submitList(list)
                }
                WhatsappActivity.Companion.WhatsappClick.IsVideo -> {
                    list.clear()
                    Log.i(TAG, "onViewCreated: $type and $res")
                    setData(res)
                    adaptorGird.submitList(list)
                }
            }
        }
    }

    private fun setData(res: ArrayList<File>) {
        if (res.isNullOrEmpty())
            return

        res.forEachIndexed { index, file ->
            DownloadItems(
                index,
                "",
                "",
                file.toUri().toString(),
                getFileDuration(file),
                getMimeType(file.toUri(), requireContext()) ?: "",
                fileSize = file.length()
            ).also { res ->
                list.add(res)
            }
        }
    }

    private fun getFileDuration(file: File) =
        when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
            WhatsappActivity.Companion.WhatsappClick.IsImage -> ""
            WhatsappActivity.Companion.WhatsappClick.IsVideo -> requireContext().videoDuration(file)
        }


    private fun requestPermission() {
        permissionManager?.request(Permission.Storage)
            ?.rationale(getString(R.string.permission_desc, "Storage"))
            ?.checkDetailedPermission { result ->
                if (!result.all { it.value }) {
                    Log.i(TAG, "showErrorDialog: ${result.keys} and ${result.values}")
                    showErrorDialog()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun recycleAdaptor() {
        binding.mainRecycle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adaptorGird = DownloadItemGridAdaptor(type) { data, bm ->
                saveDialog(data, bm)
            }
            adapter = adaptorGird
            adaptorGird.changeWhatsApp(true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun saveDialog(data: DownloadItems, bm: Bitmap?) {
        bm?.let {
            showDialogBox(
                title = "Download!!",
                desc = "Are you sure you want to download it!!",
                flag = true
            ) {
                val title = data.fileTitle.ifEmpty { data.createdCurrentTimeData }
                when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
                    WhatsappActivity.Companion.WhatsappClick.IsImage -> {
                        val url = requireActivity().bitUrl(it, title)
                        val item = getDownloadData(data, title, url)
                        Log.i(TAG, "saveDialog: $item")
                        downloadViewModel.saveDownload(item)
                    }
                    WhatsappActivity.Companion.WhatsappClick.IsVideo -> {
                        val url = requireActivity().putVideo(
                            data.fileLoc,
                            "Video_$title.mp4",
                            data.fileExtensionType
                        )
                        val item = getDownloadData(data, title, url = url)
                        Log.i(TAG, "saveDialog: $item")
                        downloadViewModel.saveDownload(item)
                    }
                }
            }
            return@let
        } ?: requireActivity().toastMsg("Some Went Wrong :(")
    }

    private fun getDownloadData(data: DownloadItems, title: String, url: Uri?) = data.copy(
        id = 0,
        fileTitle = title,
        fileThumbLoc = data.fileThumbLoc,
        fileLoc = url.toString(),
        fileLength = data.fileLength,
        fileSize = data.fileSize,
        fileExtensionType = data.fileExtensionType,
    )


    override fun onResume() {
        super.onResume()
        permissionManager?.checkPermission {
            addWhatsApp(it)
        }
    }

    private fun showErrorDialog() {
        showDialogBox {     //For Request Permission
            // Open Setting Page
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri: Uri =
                Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            intent.data = uri
            startActivity(intent)
        }
    }
}