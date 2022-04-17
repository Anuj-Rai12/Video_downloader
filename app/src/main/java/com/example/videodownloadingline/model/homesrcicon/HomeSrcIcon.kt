package com.example.videodownloadingline.model.homesrcicon

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.videodownloadingline.utils.getIconBgLis

@Entity(tableName = "HomeBookMarks")
data class HomeSrcIcon(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String? = null,
    val url: String? = null,
    val bg: Int = getIconBgLis().random()
)
