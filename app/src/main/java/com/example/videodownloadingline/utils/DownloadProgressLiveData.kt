package com.example.videodownloadingline.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import androidx.lifecycle.LiveData
import com.example.videodownloadingline.model.downloadlink.DownloadItem
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class DownloadProgressLiveData(
    private val activity: Activity,
    private val requestIds: List<Long>
) :
    LiveData<List<DownloadItem>>(),
    CoroutineScope {

    private val downloadManager by lazy {
        activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    @SuppressLint("Range")
    override fun onActive() {
        super.onActive()
        launch {
            while (isActive) {
                val query = DownloadManager.Query().setFilterById(*requestIds.toLongArray())
                val cursor = downloadManager.query(query)
                val list = mutableListOf<DownloadItem>()

                while (cursor.moveToNext()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_PENDING,
                        DownloadManager.STATUS_RUNNING,
                        DownloadManager.STATUS_PAUSED -> with(cursor) {
                            getInt(getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            val uri = getString(getColumnIndex(DownloadManager.COLUMN_URI))
                            val bytesDownloadedSoFar =
                                getInt(getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            val totalSizeBytes =
                                getLong(getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            list.add(
                                DownloadItem(
                                    bytesDownloadedSoFar.toLong(),
                                    totalSizeBytes,
                                    status,
                                    uri
                                )
                            )
                        }
                    }
                }
                postValue(list)
                delay(300)
            }
        }
    }

    companion object {
        fun getMb(bytesDownloadedSoFar: Long): Int {
            return (bytesDownloadedSoFar / 1048576).toDouble().roundToInt()
        }


        fun getKb(bytesDownloadedSoFar: Long): Int {
            return (bytesDownloadedSoFar / 1024).toDouble().roundToInt()
        }
        fun getStatus(status: Int): String {
            return when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> "Success"
                DownloadManager.STATUS_PENDING -> "Pending"
                DownloadManager.STATUS_FAILED -> "Failed"
                DownloadManager.STATUS_PAUSED -> "Paused"
                DownloadManager.STATUS_RUNNING -> "Speed"
                else -> ""
            }
        }

    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }
}