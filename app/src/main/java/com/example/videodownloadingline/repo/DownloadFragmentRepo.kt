package com.example.videodownloadingline.repo

import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.utils.RemoteResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DownloadFragmentRepo(private val roomDataBaseInstance: RoomDataBaseInstance) {

    fun getDownloadItem() = flow {
        emit(RemoteResponse.Loading("Loading.."))
        val data = try {
            val info = roomDataBaseInstance.getDownloadItemDao().getAllDownload()
            RemoteResponse.Success(info)
        } catch (e: Exception) {
            RemoteResponse.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)
}