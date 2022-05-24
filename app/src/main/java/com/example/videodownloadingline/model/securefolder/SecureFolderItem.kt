package com.example.videodownloadingline.model.securefolder

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Secure_Folder_Item")
data class SecureFolderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val folder: String,
    val src: String,
    val pin: String
) : Parcelable