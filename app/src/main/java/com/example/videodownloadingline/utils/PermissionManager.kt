package com.example.videodownloadingline.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.BuildConfig
import com.example.videodownloadingline.R
import com.example.videodownloadingline.utils.Permission.Companion.from
import java.lang.ref.WeakReference


class PermissionManager private constructor(private val fragment: WeakReference<Fragment>) {

    private val requiredPermissions = mutableListOf<Permission>()
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<Permission, Boolean>) -> Unit = {}
    private var dialogInstance: AlertDialog? = null

    private val permissionCheck =
        fragment.get()?.registerForActivityResult(RequestMultiplePermissions()) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }

    private val android11PermissionCheck = fragment.get()
        ?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.i(TAG, "inActivity Result: file is Success ${it.resultCode}")
            dialogInstance?.dismiss()
            if (it.resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Log.i(TAG, "inActivity Result: file is Success ${it.resultCode} Part 2")
                    val map =
                        mapOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE to Environment.isExternalStorageManager())
                    Log.i(TAG, "testing Manifest permission: $map")
                    sendResultAndCleanUp(map)
                }
            }
        }


    companion object {
        fun from(fragment: Fragment) = PermissionManager(WeakReference(fragment))
    }

    fun rationale(description: String): PermissionManager {
        rationale = description
        return this
    }

    fun getDialogInstance() = dialogInstance
    fun request(vararg permission: Permission): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    fun checkPermission(callback: (Boolean) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun checkDetailedPermission(callback: (Map<Permission, Boolean>) -> Unit) {
        this.detailedCallback = callback
        handlePermissionRequest()
    }

    private fun handlePermissionRequest() {
        fragment.get()?.let { fragment ->
            when {
                areAllPermissionsGranted(fragment) -> sendPositiveResult()
                shouldShowPermissionRationale(fragment) -> displayRationale(fragment)
                else -> requestPermissions()
            }
        }
    }

    private fun displayRationale(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= 30) {
            checkForAndroid11()
            return
        }
        fragment.requireActivity()
            .showDialogBox(desc = rationale ?: fragment.getString(R.string.permission_desc)) {
                requestPermissions()
            }
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associateWith { true })
    }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all { it.value })
        detailedCallback(grantResults.mapKeys { from(it.key) })
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= 30) {
            checkForAndroid11()
        } else
            permissionCheck?.launch(getPermissionList())
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkForAndroid11() {
        fragment.get()?.let {
            dialogInstance = it.requireActivity().showDialogBox(
                btn = "Accept",
                flag = true,
                cancelButton = "Deny",
                isCancelBtnEnable = true, callDeny = {
                    detailedCallback(mapOf(Permission.Storage to false))
                }) {
                if (!Environment.isExternalStorageManager())
                    getPermissionForAndroid11()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getPermissionForAndroid11() {
        try {
            val intend = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            intend.addCategory("android.intent.category.DEFAULT")
            intend.data = Uri.parse(String.format("package:%s", BuildConfig.APPLICATION_ID))
            android11PermissionCheck?.launch(intend)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            android11PermissionCheck?.launch(intent)
        }
    }


    private fun areAllPermissionsGranted(fragment: Fragment) =
        requiredPermissions.all { it.isGranted(fragment) }

    private fun shouldShowPermissionRationale(fragment: Fragment) =
        requiredPermissions.any { it.requiresRationale(fragment) }

    private fun getPermissionList() =
        requiredPermissions.flatMap { it.permissions.toList() }.toTypedArray()

    private fun Permission.isGranted(fragment: Fragment) =
        permissions.all {
            if (Build.VERSION.SDK_INT >= 30) {
                Environment.isExternalStorageManager()
            } else
                hasPermission(fragment, it)
        }

    private fun Permission.requiresRationale(fragment: Fragment) =
        permissions.any { fragment.shouldShowRequestPermissionRationale(it) }

    private fun hasPermission(fragment: Fragment, permission: String) =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
}