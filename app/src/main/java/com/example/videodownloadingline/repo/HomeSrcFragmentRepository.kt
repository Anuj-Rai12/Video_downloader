package com.example.videodownloadingline.repo

import android.util.Log
import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.RemoteResponse
import com.example.videodownloadingline.utils.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

class HomeSrcFragmentRepository(private val roomDataBaseInstance: RoomDataBaseInstance) {

    fun getBookMarkItem() = flow {
        emit(RemoteResponse.Loading("Loading.."))
        val data = try {
            val info = runBlocking {
                return@runBlocking async(IO) {
                    roomDataBaseInstance.getBookMarkItemDao().getAllBookMark()
                }
            }
            val value = mutableListOf<HomeSrcIcon>()
            value.addAll(info.await())
            value.add(HomeSrcIcon(id = value.size + 2, name = null, url = null))
            Log.i(TAG, "getBookMarkItem: testing File $value")
            RemoteResponse.Success(value)
        } catch (e: Exception) {
            RemoteResponse.Error(null, e)
        }
        emit(data)
    }.flowOn(Dispatchers.IO)


    fun addBookMarkItem(homeSrcIcon: HomeSrcIcon) = flow {
        emit(RemoteResponse.Loading("Adding Item.."))
        val data = try {
            roomDataBaseInstance.getBookMarkItemDao().insertBookMarkItem(homeSrcIcon)
            RemoteResponse.Success("Video is Added")
        } catch (e: Exception) {
            RemoteResponse.Error(null, e)
        }
        emit(data)
    }.flowOn(Dispatchers.IO)


    fun deleteBookMarkIc(homeSrcIcon: HomeSrcIcon): Int {
        return roomDataBaseInstance.getBookMarkItemDao().delete(homeSrcIcon)
    }


}