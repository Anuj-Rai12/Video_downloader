package com.example.videodownloadingline.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videodownloadingline.model.downloadlink.WebViewDownloadUrl
import com.example.videodownloadingline.utils.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _noOfTab = MutableLiveData<Int>()
    val noOfOpenTab: LiveData<Int>
        get() = _noOfTab


    private val _daisyChannelVideoDownloadLink = MutableLiveData<Event<WebViewDownloadUrl?>>()
    val daisyChannelVideoDownloadLink: LiveData<Event<WebViewDownloadUrl?>>
        get() = _daisyChannelVideoDownloadLink

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
}