package com.example.videodownloadingline.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri


fun requestDownload(
    context: Context,
    request: DownloadManager.Request,
    title: String,
): DownloadManager.Request {
    return request.setDescription("Please Wait video is Downloading")
        .setTitle(title)
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setDestinationUri(Uri.fromFile(getFileDir(title, context = context)))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
}





