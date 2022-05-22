package com.example.videodownloadingline.repo

import com.example.videodownloadingline.db.RoomDataBaseInstance
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.RemoteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HomeSrcFragmentRepository(private val roomDataBaseInstance: RoomDataBaseInstance) {

    fun getBookMarkItem() = flow {
        emit(RemoteResponse.Loading("Loading.."))
        val data = try {
            val info = roomDataBaseInstance.getBookMarkItemDao().getAllBookMark()
            val value = mutableListOf<HomeSrcIcon>()
            value.addAll(info)
            /*if (value.isEmpty()){
                value.addAll(BOOK_MARK_IC)
            }*/
            value.add(
                HomeSrcIcon(
                    id = value.size + 2,
                    name = null,
                    url = null
                )
            )
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