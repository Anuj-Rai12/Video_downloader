package com.example.videodownloadingline.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri


fun requestDownload(
    context: Context,
    request: DownloadManager.Request,
    title: String,
): DownloadManager.Request {
    val res = getFileDir(title, context = context)
    Log.i(TAG, "requestDownload: $res")
    Log.i(TAG, "requestDownload: ${res.toUri()}")
    return request.setDescription("Please Wait video is Downloading")
        .setTitle(title)
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        .setAllowedOverRoaming(true)
        .setAllowedOverMetered(true)
        .setDestinationUri(Uri.fromFile(res))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
}





