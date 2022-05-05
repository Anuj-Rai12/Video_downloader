package com.example.videodownloadingline.ui.whatsapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videodownloadingline.BuildConfig
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemGridAdaptor
import com.example.videodownloadingline.databinding.FragmentsWhatsappLayoutBinding
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.utils.*
import java.io.File
import java.util.ArrayList


class WhatsAppFragment(private val type: String) : Fragment(R.layout.fragments_whatsapp_layout) {
    private lateinit var binding: FragmentsWhatsappLayoutBinding
    private lateinit var adaptorGird: DownloadItemGridAdaptor
    private var permissionManager: PermissionManager? = null
    private val list = mutableListOf<DownloadItems>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentsWhatsappLayoutBinding.bind(view)
        permissionManager = PermissionManager.from(this)
        requestPermission()
        recycleAdaptor()
        permissionManager?.checkPermission {
            if (it) {
                val res =
                    getWhatsappStoryPath(WhatsappActivity.Companion.WhatsappClick.valueOf(type))

                when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
                    WhatsappActivity.Companion.WhatsappClick.IsImage -> {
                        setData(res, "image/*")
                        adaptorGird.submitList(list)
                    }
                    WhatsappActivity.Companion.WhatsappClick.IsVideo -> {
                        Log.i(TAG, "onViewCreated: $type and $res")
                        setData(res, "video/*")
                        adaptorGird.submitList(list)
                    }
                }
            }
        }
    }

    private fun setData(res: ArrayList<File>, format: String) {
        if (res.isNullOrEmpty())
            return

        res.forEachIndexed { index, file ->
            DownloadItems(
                index,
                "",
                "",
                file.toUri().toString(),
                getFormat(file),
                format,
                fileSize = file.length()
            ).also { res ->
                list.add(res)
            }
        }
    }

    private fun getFormat(file: File) =
        when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
            WhatsappActivity.Companion.WhatsappClick.IsImage -> ""
            WhatsappActivity.Companion.WhatsappClick.IsVideo -> requireContext().videoDuration(file)
        }


    private fun requestPermission() {
        permissionManager?.request(Permission.Storage)
            ?.rationale(getString(R.string.permission_desc, "Storage"))
            ?.checkDetailedPermission { result ->
                if (!result.all { it.value }) {
                    showErrorDialog(result)
                }
            }
    }


    private fun recycleAdaptor() {
        binding.mainRecycle.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adaptorGird = DownloadItemGridAdaptor(type) {
                Log.i(TAG, "recycleAdaptor: $it")
            }
            adapter = adaptorGird
            adaptorGird.changeWhatsApp(true)
        }
    }


    override fun onResume() {
        super.onResume()
        permissionManager?.checkPermission {
            Log.i(TAG, "onResume: Permission $it")
        }
    }

    private fun showErrorDialog(result: Map<Permission, Boolean>) {
        Log.i(TAG, "showErrorDialog: ${result.keys} and ${result.values}")
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