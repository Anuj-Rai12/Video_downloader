package com.example.videodownloadingline.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.videodownloadingline.db.dao.BookMarkItemDao
import com.example.videodownloadingline.db.dao.DownloadItemDao
import com.example.videodownloadingline.db.dao.SecureFolderItemDao
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.model.securefolder.SecureFolderItem
import com.example.videodownloadingline.utils.BOOK_MARK_IC
import com.example.videodownloadingline.utils.DOWNLOAD_ITEM
import com.example.videodownloadingline.utils.ioThread


@Database(
    entities = [DownloadItems::class, HomeSrcIcon::class, SecureFolderItem::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDataBaseInstance : RoomDatabase() {
    abstract fun getDownloadItemDao(): DownloadItemDao
    abstract fun getBookMarkItemDao(): BookMarkItemDao
    abstract fun getSecureFolderDao(): SecureFolderItemDao


    companion object {

        @Volatile
        private var INSTANCE: RoomDataBaseInstance? = null

        fun getInstance(context: Context): RoomDataBaseInstance {
            synchronized(this) {
                val instance = INSTANCE
                if (instance != null) {
                    return instance
                }

                synchronized(this) {
                    val oldInstance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDataBaseInstance::class.java,
                        "My_Download_TBL"
                    ).addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            ioThread {
                                getInstance(context).getDownloadItemDao().insertDownloadItem(DOWNLOAD_ITEM)
                                getInstance(context).getBookMarkItemDao().insertBookMarkItem(BOOK_MARK_IC)
                            }
                        }
                    }).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = oldInstance
                    return oldInstance
                }

            }
        }


        /*private fun buildDataBase(context: Context): RoomDataBaseInstance {
            return Room.databaseBuilder(
                context,
                RoomDataBaseInstance::class.java,
                "Data_BASE_TBL"
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    ioThread {
                        getInstance(context).getDownloadItemDao().insertDownloadItem(
                            DOWNLOAD_ITEM
                        )
                    }
                }
            }).build()*/
    }

}