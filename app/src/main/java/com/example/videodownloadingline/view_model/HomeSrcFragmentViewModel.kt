package com.example.videodownloadingline.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.repo.HomeSrcFragmentRepository

class HomeSrcFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: HomeSrcFragmentRepository =
        HomeSrcFragmentRepository(RoomDataBaseInstance.getInstance(application))


    val geBookMarkItem = repo.getBookMarkItem().asLiveData()

}