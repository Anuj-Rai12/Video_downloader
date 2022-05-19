package com.example.videodownloadingline.model.downloaditem

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat

@Entity(tableName = "Download_Items")
data class DownloadItems(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fileTitle: String,
    val fileThumbLoc: String,
    val fileLoc: String,
    val fileLength: String,
    val fileExtensionType: String,
    val fileSize: Long,
    val downloadCreatedAt: Long = System.currentTimeMillis(),
    val setPin: String = "",
    val category: String = Category.NormalFolder.name
) {
    val createdCurrentTimeData: String
        get() = DateFormat.getDateTimeInstance().format(downloadCreatedAt)
}

enum class Category {
    PinFolder,
    NormalFolder
}

enum class TypeOfDownload {
    IsFolder,
    IsFiles,
    SecureFolder
}