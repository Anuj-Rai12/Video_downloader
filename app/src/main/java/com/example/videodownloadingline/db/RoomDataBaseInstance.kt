package com.example.videodownloadingline.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.videodownloadingline.db.dao.DownloadItemDao
import com.example.videodownloadingline.model.downloaditem.DownloadItems


@Database(entities = [DownloadItems::class], version = 1, exportSchema = true)
abstract class RoomDataBaseInstance : RoomDatabase() {
    abstract fun getDownloadItemDao(): DownloadItemDao


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
                    ).createFromAsset("db/My_Download_Items.db").build()
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