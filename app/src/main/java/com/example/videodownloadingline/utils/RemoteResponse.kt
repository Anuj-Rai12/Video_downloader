package com.example.videodownloadingline.utils

sealed class RemoteResponse<T>(
    val data: T? = null,
    val exception: Exception? = null
) {
    class Loading<T>(data: T?) : RemoteResponse<T>(data)
    class Success<T>(data: T) : RemoteResponse<T>(data)
    class Error<T>(data: T? = null, exception: Exception?) : RemoteResponse<T>(data, exception)
}
