package com.example.videodownloadingline.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _noOfTab = MutableLiveData<Int>()
    val noOfOpenTab: LiveData<Int>
        get() = _noOfTab


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