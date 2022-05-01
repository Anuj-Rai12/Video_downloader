package com.example.videodownloadingline.utils



import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


const val PERMISSION_REQUESTS = 1

fun Activity.allPermissionsGranted(): Boolean {
    for (permission in this.getRequiredPermission()!!) {
        //if (permission == Manifest.permission.WRITE_SETTINGS) continue
        //if (permission == Manifest.permission.FOREGROUND_SERVICE) continue
        if (this.isPermissionGranted(permission!!)) {
            Log.d(TAG, "permission not granted $permission")
            return false
        }
    }
    return true
}


fun Activity.getRequiredPermission(): Array<String?>? {
    return try {
        val info = packageManager
            .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        val ps = info.requestedPermissions
        if (ps != null && ps.isNotEmpty()) {
            ps
        } else {
            arrayOfNulls(0)
        }
    } catch (e: Exception) {
        arrayOfNulls(0)
    }
}


fun Activity.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) != PackageManager.PERMISSION_GRANTED
}


fun Activity.getRuntimePermissions() {
    val allNeededPermissions: MutableList<String> = ArrayList()
    for (permission in getRequiredPermission()!!) {
        if (isPermissionGranted(permission!!)) {
            allNeededPermissions.add(permission)
        }
    }
    if (allNeededPermissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
        )
    }
}