package com.example.videodownloadingline.model.downloadlink

data class VideoType(
    val height: Int,
    val weight: Int,
    val size: Int,
    val webViewDownloadUrl: WebViewDownloadUrl,
    val url:String
)