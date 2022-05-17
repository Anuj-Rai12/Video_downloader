package com.example.videodownloadingline.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.repo.DownloadFragmentRepo
import com.example.videodownloadingline.utils.Event
import com.example.videodownloadingline.utils.RemoteResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val _event = MutableLiveData<Event<String>>()
    val event: LiveData<Event<String>>
        get() = _event


    private val _downloadItem = MutableLiveData<RemoteResponse<out Any>>()
    val downloadItemDb: LiveData<RemoteResponse<out Any>>
        get() = _downloadItem

    private var repo: DownloadFragmentRepo? = null

    init {
        repo = DownloadFragmentRepo(RoomDataBaseInstance.getInstance(application))
        fetch()
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
                if (res.data?.isNullOrEmpty() == true) {
                    fetch()
                } else {
                    _downloadItem.postValue(res)
                }
            }
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

}