package com.example.videodownloadingline.model.tabitem

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TabItem(
    val id: Int,
    val url: String? = null
) : Parcelable