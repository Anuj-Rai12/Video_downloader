package com.example.videodownloadingline.ui.whatsapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.BuildConfig
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.FragmentsWhatsappLayoutBinding
import com.example.videodownloadingline.utils.Permission
import com.example.videodownloadingline.utils.PermissionManager
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.showDialogBox


class WhatsAppFragment(private val type: String) : Fragment(R.layout.fragments_whatsapp_layout) {

    private lateinit var binding: FragmentsWhatsappLayoutBinding
    private var permissionManager: PermissionManager? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentsWhatsappLayoutBinding.bind(view)
        permissionManager = PermissionManager.from(this)
        requestPermission()
        when (WhatsappActivity.Companion.WhatsappClick.valueOf(type)) {
            WhatsappActivity.Companion.WhatsappClick.IsImage -> {

            }
            WhatsappActivity.Companion.WhatsappClick.IsVideo -> {

            }
        }
    }

    private fun requestPermission() {
        Log.i(TAG, "requestPermission: checking permission")
        permissionManager?.request(Permission.Storage)
            ?.rationale(getString(R.string.permission_desc, "Storage"))
            ?.checkDetailedPermission { result ->
                if (!result.all { it.value }) {
                    showErrorDialog(result)
                }
            }
    }


    private fun showErrorDialog(result: Map<Permission, Boolean>) {
        Log.i(TAG, "showErrorDialog: $result")
        showDialogBox {     //For Request Permission
            // Open Setting Page
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri: Uri =
                Uri.fromParts("package",BuildConfig.APPLICATION_ID, null)
            intent.data = uri
            startActivity(intent)
        }
    }
}