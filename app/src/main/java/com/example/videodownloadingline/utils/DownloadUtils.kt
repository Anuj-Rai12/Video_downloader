package com.example.videodownloadingline.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri


fun requestDownload(
    context: Context,
    request: DownloadManager.Request,
    title: String,
    url: String,
): DownloadManager.Request {
    val extraHeaders: MutableMap<String, String> = HashMap()
    extraHeaders["Referer"] = url

//    val defaultHttpDataSourceFac = DefaultHttpDataSource.Factory()
//    defaultHttpDataSourceFac.setDefaultRequestProperties(extraHeaders)

    return request.setDescription("Please Wait video is Downloading")
        .setTitle(title)
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
//        .addRequestHeader("Referer", extraHeaders.toString())
        .setAllowedOverRoaming(true)
        .setAllowedOverMetered(true)
        .setDestinationUri(Uri.fromFile(getFileDir(title, context = context)))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
}





