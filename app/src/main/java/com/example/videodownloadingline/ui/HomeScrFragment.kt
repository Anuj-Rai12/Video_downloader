package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.HomeSrcFragmentBinding
import com.example.videodownloadingline.utils.changeStatusBarColor
import com.example.videodownloadingline.utils.hideFullSrc


class HomeScrFragment : Fragment(R.layout.home_src_fragment) {
    private lateinit var binding: HomeSrcFragmentBinding
    private var mgr: DownloadManager? = null
    private var lastDownload: Long = -1L

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeSrcFragmentBinding.bind(view)
        initial()


        registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        registerReceiver(
            onNotificationClick,
            IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        )

    }

    private fun registerReceiver(onComplete: BroadcastReceiver, any: Any) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        activity?.hideFullSrc()
        activity?.changeStatusBarColor()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
        unregisterReceiver(onNotificationClick)
    }

    private fun unregisterReceiver(onComplete: BroadcastReceiver) {

    }


    fun startDownload(v: View) {
        val uri: Uri = Uri.parse(getString(R.string.testing_url))
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .mkdirs()

        lastDownload = mgr!!.enqueue(
            DownloadManager.Request(uri)
                .setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or
                            DownloadManager.Request.NETWORK_MOBILE
                )
                .setAllowedOverRoaming(false)
                .setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "test.mp4"
                )
        )
        v.isEnabled = false
        binding.btnClicked.isEnabled = true
        //findViewById(R.id.query).setEnabled(true)
    }

    @SuppressLint("Range")
    fun queryStatus(v: View?) {
        val c: Cursor? = mgr!!.query(DownloadManager.Query().setFilterById(lastDownload))
        if (c == null) {
            Toast.makeText(activity, "Download not found!", Toast.LENGTH_LONG).show()
        } else {
            c.moveToFirst()
            Log.d(
                javaClass.name, "COLUMN_ID: " +
                        c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID))
            )
            Log.d(
                javaClass.name, "COLUMN_BYTES_DOWNLOADED_SO_FAR: " +
                        c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            )
            Log.d(
                javaClass.name, "COLUMN_LAST_MODIFIED_TIMESTAMP: " +
                        c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP))
            )
            Log.d(
                javaClass.name, "COLUMN_LOCAL_URI: " +
                        c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            )
            Log.d(
                javaClass.name, "COLUMN_STATUS: " +
                        c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            )
            Log.d(
                javaClass.name, "COLUMN_REASON: " +
                        c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
            )
            Toast.makeText(activity, statusMessage(c), Toast.LENGTH_LONG).show()
        }
    }

    fun viewLog(v: View?) {
        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }

    @SuppressLint("Range")
    private fun statusMessage(c: Cursor): String? {
        var msg = "???"
        msg =
            when (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_FAILED -> "Download failed!"
                DownloadManager.STATUS_PAUSED -> "Download paused!"
                DownloadManager.STATUS_PENDING -> "Download pending!"
                DownloadManager.STATUS_RUNNING -> "Download in progress!"
                DownloadManager.STATUS_SUCCESSFUL -> "Download complete!"
                else -> "Download is nowhere in sight"
            }
        return msg
    }


    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            //       findViewById(R.id.start).setEnabled(true)
        }
    }

    var onNotificationClick: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show()
        }
    }


}