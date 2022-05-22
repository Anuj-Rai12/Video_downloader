package com.example.videodownloadingline.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.repo.HomeSrcFragmentRepository
import com.example.videodownloadingline.utils.Event
import com.example.videodownloadingline.utils.RemoteResponse
import com.example.videodownloadingline.utils.TAG
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeSrcFragmentViewModel(application: Application) : AndroidViewModel(application) {


    private val _getBookMarkIcon = MutableLiveData<RemoteResponse<out Any>>()
    val getBookMarkResponse: LiveData<RemoteResponse<out Any>>
        get() = _getBookMarkIcon


    private var repo: HomeSrcFragmentRepository? = null

    private val _eventForDeleteBookMarkIc = MutableLiveData<Event<Int?>>()
    val eventForDeleteBookMarkIc: LiveData<Event<Int?>>
        get() = _eventForDeleteBookMarkIc

    init {
        repo = HomeSrcFragmentRepository(RoomDataBaseInstance.getInstance(application))
        viewModelScope.launch {
            getResponse()
        }
    }

    private suspend fun getResponse() {
        repo?.getBookMarkItem()?.collectLatest {
            _getBookMarkIcon.postValue(it)
        }
    }

    fun addVideoItem(homeSrcIcon: HomeSrcIcon) {
        viewModelScope.launch {
            repo?.addBookMarkItem(homeSrcIcon)?.collectLatest {
                when (it) {
                    is RemoteResponse.Error -> Log.i(TAG, "addVideoItem: ${it.exception?.message}")
                    is RemoteResponse.Loading -> Log.i(TAG, "addVideoItem: ${it.data}")
                    is RemoteResponse.Success -> {
                        Log.i(TAG, "addVideoItem: ${it.data}")
                        getResponse()
                    }
                }
            }
        }
    }

    fun deleteBookMarkIc(homeSrcIcon: HomeSrcIcon) {
        viewModelScope.launch {
            val res = async(IO) {
                repo?.deleteBookMarkIc(homeSrcIcon)
            }
            _eventForDeleteBookMarkIc.postValue(Event(res.await()))
            getResponse()
        }
    }


    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}