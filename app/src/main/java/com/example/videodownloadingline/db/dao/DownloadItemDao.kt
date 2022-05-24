package com.example.videodownloadingline.db.dao

import androidx.room.*
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloadItem(item: DownloadItems)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloadItem(item: List<DownloadItems>)

    @Delete
    fun deleteItem(downloadItems: DownloadItems)


    @Update
    fun updateItem(downloadItems: DownloadItems): Int

    @Query("Select * from Download_Items")
    suspend fun getAllDownload(): List<DownloadItems>

    @Query("delete from Download_Items")
    fun deleteItem()


    @Query("Select *From Download_Items where fileTitle Like:src")
    fun searchDownloadFile(src: String): Flow<List<DownloadItems>>


    @Query("Select *From Download_Items where fileLoc Like:src and setPin Like:pin")
    fun searchDownloadFile(src: String, pin: String): Flow<List<DownloadItems>>


    @Query("Select *From Download_Items where fileLoc Like:src and fileThumbLoc Like:fileTitle")
    fun searchDownloadInNormalFolder(src: String, fileTitle: String): Flow<List<DownloadItems>>


    @Query("Select *From Download_Items Order by downloadCreatedAt desc")
    fun filterDownloadItemDate(): Flow<List<DownloadItems>>


    @Query("Select *From Download_Items Order by fileSize desc")
    fun filterDownloadItemSize(): Flow<List<DownloadItems>>


    @Query("Select *From Download_Items Order by fileExtensionType desc")
    fun filterDownloadItemType(): Flow<List<DownloadItems>>


    @Query("Select *From Download_Items Order by fileTitle desc")
    fun filterDownloadItemName(): Flow<List<DownloadItems>>
//or itemDescription Like:searchQuery or itemCategory Like :searchQuery
//or salePrice Like :searchQuery order by salePrice desc

}