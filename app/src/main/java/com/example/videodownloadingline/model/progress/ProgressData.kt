package com.example.videodownloadingline.model.progress

data class ProgressData(
    val id: Int,
    val title: String,
    val progress: Int,
    val size: Int,
    val status: Vid
)

enum class Vid {
    pause,
    speed
}