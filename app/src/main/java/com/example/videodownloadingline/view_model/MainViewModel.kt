package com.example.videodownloadingline.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloadlink.VideoType
import com.example.videodownloadingline.model.downloadlink.WebViewDownloadUrl
import com.example.videodownloadingline.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.File

class MainViewModel : ViewModel() {
    private val _noOfTab = MutableLiveData<Int>()
    val noOfOpenTab: LiveData<Int>
        get() = _noOfTab


    private val _daisyChannelVideoDownloadLink = MutableLiveData<Event<WebViewDownloadUrl?>>()
    val daisyChannelVideoDownloadLink: LiveData<Event<WebViewDownloadUrl?>>
        get() = _daisyChannelVideoDownloadLink


    private val _downloadId = MutableLiveData<MutableList<Long>>()
    val downloadId: LiveData<MutableList<Long>>
        get() = _downloadId


    private val _downloadVid = MutableLiveData<MutableList<VideoType>>()
    val downloadVid: LiveData<MutableList<VideoType>>
        get() = _downloadVid

    var currentNumTab: Int? = null

    private val _createNewTab = MutableLiveData<Boolean>()
    val createNewTab: LiveData<Boolean>
        get() = _createNewTab
    var removeTab: Pair<Boolean, Int?> = Pair(false, null)

    private val _folderDir = MutableLiveData<List<DownloadItems>?>()
    val folderDir: LiveData<List<DownloadItems>?>
        get() = _folderDir


    fun getFolderDir(file: File) {
        viewModelScope.launch {
            val res = withContext(IO) {
                getListOfFile(file)
            }
            Log.i(TAG, "getFolderDir: Folder File -> $res")
            _folderDir.postValue(res)
        }
    }

    fun removeFolder() {
        _folderDir.postValue(null)
    }

    companion object {
        @Volatile
        private var INSTANCE: MainViewModel? = null


        fun getInstance(): MainViewModel? {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = MainViewModel()
                    return INSTANCE
                }
                return INSTANCE
            }
        }
    }


    init {
        _noOfTab.postValue(1)
        _downloadId.postValue(mutableListOf())
        _downloadVid.postValue(mutableListOf())
        _createNewTab.postValue(false)
    }

    fun changeStateForCreateNewTB(flag: Boolean) {
        _createNewTab.postValue(flag)
    }

    fun daisyChainDownload(data: String?) {
        viewModelScope.launch {
            var videoitemfinal: WebViewDownloadUrl?
            val webScrapper1 = async(start = CoroutineStart.LAZY) { websiteScrapperPart1(data) }
            val webScrapper2 = async(start = CoroutineStart.LAZY) { websiteScrapperPart2(data) }
            val webScrapper3 = async(start = CoroutineStart.LAZY) { websiteScrapperPart3(data) }
            val webScrapper4 = async(start = CoroutineStart.LAZY) { websiteScrapperPart4(data) }
            val webScrapper5 = async(start = CoroutineStart.LAZY) { genericDownloader(data) }
            videoitemfinal = webScrapper1.await()
            if (videoitemfinal != null) {
                _daisyChannelVideoDownloadLink.postValue(Event(videoitemfinal))
                Log.d("testvideo", "found scrapper in 1")
            } else {
                videoitemfinal = webScrapper2.await()
                if (videoitemfinal != null) {
                    Log.d("testvideo", "found scrapper 2")
                    _daisyChannelVideoDownloadLink.postValue(Event(videoitemfinal))
                } else {
                    videoitemfinal = webScrapper3.await()
                    if (videoitemfinal != null) {
                        _daisyChannelVideoDownloadLink.postValue(Event(videoitemfinal))
                        Log.d("testvideo", "found scrapper 3")
                    } else {
                        videoitemfinal = webScrapper4.await()
                        if (videoitemfinal != null) {
                            _daisyChannelVideoDownloadLink.postValue(Event(videoitemfinal))
                            Log.d("testvideo", "found scrapper 4")
                        } else {
                            videoitemfinal = webScrapper5.await()
                            _daisyChannelVideoDownloadLink.postValue(Event(videoitemfinal))
                            Log.d("testvideo", "reached generic")
                        }
                    }
                }
            }
        }
    }

    fun addMoreTab() {
        _noOfTab.value?.let {
            _noOfTab.postValue(it + 1)
        }
    }

    fun removeTab() {
        _noOfTab.value?.let {
            if (it > 1)
                _noOfTab.postValue(it - 1)
        }
    }


    fun getM3U8Url(url: String): Boolean {
        _daisyChannelVideoDownloadLink.postValue(
            Event(
                WebViewDownloadUrl(
                    sdurl = url,
                    videotype = "video/.m3u8"
                )
            )
        )
        return false
    }


    // ID Manipulation --------------------------

    fun addID(id: Long) {
        _downloadId.value?.let {
            it.add(id)
            _downloadId.postValue(it)
        }
    }


    fun removeID(index: Int) {
        _downloadId.value?.let {
            it.removeAt(index)
            _downloadId.postValue(it)
        }
        if (_downloadId.value.isNullOrEmpty()) {
            _downloadId.postValue(mutableListOf())
        }
    }

    fun getIDIndex(id: Long): Int {
        Log.i(TAG, "getIDIndex: ${_downloadId.value}")
        return _downloadId.value?.indexOf(id) ?: -1
    }


    fun removeAllID() {
        _downloadId.value?.let {
            it.clear()
            _downloadId.postValue(it)
        }
        if (_downloadId.value.isNullOrEmpty()) {
            _downloadId.postValue(mutableListOf())
        }
    }

    // Video Manipulation ---------------------------------------------

    fun removeVideo(index: Int) {
        _downloadVid.value?.let {
            it.removeAt(index)
            _downloadVid.postValue(it)
        }
        if (_downloadVid.value.isNullOrEmpty()) {
            _downloadVid.postValue(mutableListOf())
        }
    }

    fun removeAllVideo() {
        _downloadVid.value?.let {
            it.clear()
            _downloadVid.postValue(it)
        }
        _downloadVid.postValue(mutableListOf())
    }

    fun addVideo(videoType: VideoType) {
        _downloadVid.value?.let {
            it.add(videoType)
            _downloadVid.postValue(it)
        }
    }

    fun getVideoDataByIndex(index: Int) = _downloadVid.value?.get(index)

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

}