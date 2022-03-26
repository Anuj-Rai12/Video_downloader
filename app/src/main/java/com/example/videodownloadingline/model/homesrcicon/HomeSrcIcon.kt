package com.example.videodownloadingline.model.homesrcicon

import com.example.videodownloadingline.utils.getIconBgLis

data class HomeSrcIcon(
    val id: Int,
    val name: String?,
    val url: String?,
    val bg: Int = getIconBgLis().random()
)
