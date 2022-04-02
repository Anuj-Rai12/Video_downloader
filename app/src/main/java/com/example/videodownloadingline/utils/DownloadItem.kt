package com.example.videodownloadingline.utils

import android.annotation.SuppressLint
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.videodownloadingline.ui.TAG
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

data class DownloadItem(
    val bytesDownloadedSoFar: Long = -1,
    val totalSizeBytes: Long = -1,
    val status: Int,
    val stat: String
)

class DownloadProgressLiveData(private val application: Application, private val requestId: Long) :
    LiveData<DownloadItem>(),
    CoroutineScope {

    private val downloadManager by lazy {
        application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    @SuppressLint("Range")
    override fun onActive() {
        super.onActive()
        launch {
            while (isActive) {
                val query = DownloadManager.Query().setFilterById(requestId)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val stat = getStatus(status)
                    //Log.i(TAG, "onActive: status is => $status")
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL,
                        DownloadManager.STATUS_PENDING,
                        DownloadManager.STATUS_FAILED,
                        DownloadManager.STATUS_PAUSED -> postValue(
                            DownloadItem(
                                status = status,
                                stat = stat
                            )
                        )
                        else -> {
                            val bytesDownloadedSoFar =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            val totalSizeBytes =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            postValue(
                                DownloadItem(
                                    bytesDownloadedSoFar.toLong(),
                                    totalSizeBytes,
                                    status,
                                    stat
                                )
                            )
                        }
                    }
                    if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED)
                        cancel()
                } else {
                    postValue(
                        DownloadItem(
                            status = DownloadManager.STATUS_FAILED,
                            stat = "STATUS FAILED"
                        )
                    )
                    cancel()
                }
                cursor.close()
                delay(300)
            }
        }
    }

    companion object {
        fun getStatus(status: Int): String {
            return when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> "success"
                DownloadManager.STATUS_PENDING -> "Pending"
                DownloadManager.STATUS_FAILED -> "Failed"
                DownloadManager.STATUS_PAUSED -> "Passed"
                else -> ""
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }

}