package com.example.videodownloadingline.db.dao

import androidx.room.*
import com.example.videodownloadingline.model.downloaditem.DownloadItems

@Dao
interface DownloadItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloadItem(item: DownloadItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloadItem(item: List<DownloadItems>)

    @Delete
    fun deleteItem(downloadItems: DownloadItems)

    @Query("Select * from Download_Items")
    suspend fun getAllDownload(): List<DownloadItems>

    @Query("delete from Download_Items")
    fun deleteItem()

}