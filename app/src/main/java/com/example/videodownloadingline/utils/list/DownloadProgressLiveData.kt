package com.example.videodownloadingline.utils.list

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.videodownloadingline.ui.TAG
import com.example.videodownloadingline.utils.DownloadProgressLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


data class DownloadItem(
    val bytesDownloadedSoFar: Long = -1,
    val totalSizeBytes: Long = -1,
    val status: Int,
    val uri: String,
    var title: String? = null,
    var des: String? = null,
    var id: Long? = null
)


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
                    val stat = DownloadProgressLiveData.getStatus(status)
                    Log.i(TAG, "onActive: status is => $status and its $stat")
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

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }
}