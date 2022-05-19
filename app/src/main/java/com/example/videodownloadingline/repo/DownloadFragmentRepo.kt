package com.example.videodownloadingline.repo

import android.util.Log
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.utils.RemoteResponse
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.getListOfFile
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

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


    fun fetchAllFolder(file: File) = flow {
        emit(getListOfFile(file))
    }.flowOn(IO)


    fun addDownload(downloadItems: DownloadItems) {
        Log.i(TAG, "addDownload: file is getting saved")
        roomDataBaseInstance.getDownloadItemDao().insertDownloadItem(
            downloadItems
        )
    }


    fun deleteDownload(downloadItems: DownloadItems) {
        roomDataBaseInstance.getDownloadItemDao().deleteItem(downloadItems)
    }


    fun searchFileWithFileName(query: String) = channelFlow {
        roomDataBaseInstance.getDownloadItemDao().searchDownloadFile(query).collectLatest {
            send(RemoteResponse.Success(it))
        }
    }

    fun updateDownloadItem(data: DownloadItems) {
        roomDataBaseInstance.getDownloadItemDao().updateItem(data)
    }

}