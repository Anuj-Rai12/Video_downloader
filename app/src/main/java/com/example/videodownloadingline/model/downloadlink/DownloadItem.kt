package com.example.videodownloadingline.model.downloadlink

data class DownloadItem(
    val bytesDownloadedSoFar: Long = -1,
    val totalSizeBytes: Long = -1,
    val status: Int,
    val uri: String,
    var title: String? = null,
    var des: String? = null,
    var id: Long? = null
)