package com.example.videodownloadingline.model.downloaditem

import java.text.DateFormat

data class SampleDownloadItem(
    val id: Int,
    val name: String,
    val size: Int,
    private val created: Long = System.currentTimeMillis()
) {
     val createdCurrentTimeData: String
        get() = DateFormat.getDateTimeInstance().format(created)
}
