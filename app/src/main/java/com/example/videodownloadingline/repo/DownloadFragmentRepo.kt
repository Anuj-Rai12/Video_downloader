package com.example.videodownloadingline.repo

import android.util.Log
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.securefolder.SecureFolderItem
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


    fun searchFileWithFileName(src: String, pin: String) = channelFlow {
        roomDataBaseInstance.getDownloadItemDao().searchDownloadFile(src, pin).collectLatest {
            send(RemoteResponse.Success(it))
        }
    }


    fun searchFileInNormalFolder(src: String, fileName: String) = channelFlow {
        roomDataBaseInstance.getDownloadItemDao().searchDownloadInNormalFolder(src, fileName)
            .collectLatest {
                send(it)
            }
    }


    fun updateDownloadItem(data: DownloadItems): Long {
        return roomDataBaseInstance.getDownloadItemDao().updateItem(data).toLong()
    }


    fun addPinFolder(secureFolderItem: SecureFolderItem): Long {
        return roomDataBaseInstance.getSecureFolderDao().insertSecureFolder(secureFolderItem)
    }

    fun checkPinToOpenFolder(src: String, pin: String) = channelFlow {
        roomDataBaseInstance.getSecureFolderDao().getSecureFolder(src, pin).collectLatest {
            send(it)
        }
    }

    fun filterDownloadItem(index: Int) = channelFlow {
        val dao = roomDataBaseInstance.getDownloadItemDao()
        when (index) {
            0 -> {//Sort by date
                dao.filterDownloadItemDate().collectLatest {
                    send(RemoteResponse.Success(it))
                }
            }
            1 -> {//Sort by size
                dao.filterDownloadItemSize().collectLatest {
                    send(RemoteResponse.Success(it))
                }
            }
            2 -> {//Sort by type
                dao.filterDownloadItemType().collectLatest {
                    send(RemoteResponse.Success(it))
                }
            }
            3 -> {//Sort by name
                dao.filterDownloadItemName().collectLatest {
                    send(RemoteResponse.Success(it))
                }
            }
        }
    }

}