package com.example.videodownloadingline.db.dao

import androidx.room.*
import com.example.videodownloadingline.model.securefolder.SecureFolderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SecureFolderItemDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSecureFolder(item: SecureFolderItem):Long


    @Update
    fun updateSetPin(item: SecureFolderItem)


    @Query("select * from Secure_Folder_Item where src Like:filePath and pin Like:securePin")
    fun getSecureFolder(filePath: String, securePin: String): Flow<List<SecureFolderItem>>


}