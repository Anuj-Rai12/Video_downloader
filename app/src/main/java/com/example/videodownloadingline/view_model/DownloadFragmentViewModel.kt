package com.example.videodownloadingline.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.repo.DownloadFragmentRepo

class DownloadFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: DownloadFragmentRepo =
        DownloadFragmentRepo(RoomDataBaseInstance.getInstance(application))


    val getDownloadItem=repo.getDownloadItem().asLiveData()

}