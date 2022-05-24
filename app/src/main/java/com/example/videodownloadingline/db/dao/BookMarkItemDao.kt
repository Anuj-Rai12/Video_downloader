package com.example.videodownloadingline.db.dao

import androidx.room.*
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon

@Dao
interface BookMarkItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookMarkItem(item: HomeSrcIcon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookMarkItem(item: List<HomeSrcIcon>)

    @Query("Select * from HomeBookMarks")
    suspend fun getAllBookMark(): List<HomeSrcIcon>

    @Query("delete from HomeBookMarks")
    suspend fun deleteAllBookMark()

    @Delete
    fun delete(obj: HomeSrcIcon): Int

}