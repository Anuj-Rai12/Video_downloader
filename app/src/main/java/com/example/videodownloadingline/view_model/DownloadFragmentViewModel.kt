package com.example.videodownloadingline.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.downloaditem.Category
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.securefolder.SecureFolderItem
import com.example.videodownloadingline.repo.DownloadFragmentRepo
import com.example.videodownloadingline.utils.Event
import com.example.videodownloadingline.utils.RemoteResponse
import com.example.videodownloadingline.utils.TAG
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class DownloadFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val _event = MutableLiveData<Event<String>>()
    val event: LiveData<Event<String>>
        get() = _event

    private val _eventSetPin = MutableLiveData<Event<Pair<DownloadItems, String>>>()
    val eventSetPin: LiveData<Event<Pair<DownloadItems, String>>>
        get() = _eventSetPin


    private val _downloadItem = MutableLiveData<RemoteResponse<out Any>>()
    val downloadItemDb: LiveData<RemoteResponse<out Any>>
        get() = _downloadItem

    private var repo: DownloadFragmentRepo? = null


    private val _folderItem = MutableLiveData<List<DownloadItems>>()
    val folderItem: LiveData<List<DownloadItems>>
        get() = _folderItem


    private val _folderCreateId = MutableLiveData<Long>()
    val folderCreateId: LiveData<Long>
        get() = _folderCreateId

    init {
        repo = DownloadFragmentRepo(RoomDataBaseInstance.getInstance(application))
        //fetch()
    }


    fun getListOfFolder(file: File) {
        viewModelScope.launch {
            repo?.fetchAllFolder(file)?.collectLatest {
                _folderItem.postValue(it)
            }
        }
    }

    fun fetch() {
        viewModelScope.launch {
            repo?.getDownloadItem()?.collectLatest {
                _downloadItem.postValue(it)
            }
        }
    }


    fun saveDownload(downloadItems: DownloadItems) {
        viewModelScope.launch {
            val async = async(IO) {
                repo?.addDownload(downloadItems)
            }
            async.await()
            _event.postValue(Event("File is Saved"))
        }
    }


    fun searchQuery(src: String) {
        viewModelScope.launch {
            repo?.searchFileWithFileName(src)?.collectLatest { res ->
                if (res.data?.isEmpty() == true) {
                    fetch()
                } else {
                    _downloadItem.postValue(res)
                }
            }
        }
    }


    fun addPinFolder(secureFolderItem: SecureFolderItem) {
        viewModelScope.launch {
            val res = async(IO) {
                repo?.addPinFolder(secureFolderItem)
            }
            _folderCreateId.postValue(res.await())
        }
    }

    fun deleteDownload(downloadItems: DownloadItems) {
        viewModelScope.launch {
            val async = async(IO) {
                repo?.deleteDownload(downloadItems)
            }
            async.await()
            _event.postValue(Event("File is Deleted"))
            fetch()
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    fun updateDownloadItem(downloadItems: DownloadItems, filePath: String, category: Category) {
        val download = DownloadItems(
            downloadItems.id,
            downloadItems.fileTitle,
            downloadItems.fileThumbLoc,
            filePath,
            downloadItems.fileLength,
            downloadItems.fileExtensionType,
            downloadItems.fileSize,
            downloadItems.downloadCreatedAt,
            downloadItems.setPin,
            category = category.name
        )
        viewModelScope.launch {
            val res = async(IO) {
                repo?.updateDownloadItem(download)
            }
            res.await()
        }
    }

    fun checkPinToOpenFolder(src: String, pin: String, res: SecureFolderItem) {
        viewModelScope.launch {
            val downloadItems = DownloadItems(
                0,
                res.folder, "",
                res.src, "", "", 21
            )
            repo?.checkPinToOpenFolder(src, pin)?.collectLatest {
                Log.i(TAG, "checkPinToOpenFolder: Valid File in data Base $it")
                if (it.isNotEmpty()) {
                    _eventSetPin.postValue(Event(Pair(downloadItems, "folder_in_db_is_found")))
                } else {
                    _eventSetPin.postValue(Event(Pair(downloadItems, "folder_is_not_found")))
                }
            }
        }
    }

}