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
import com.example.videodownloadingline.utils.DownloadProgressLiveData
import com.example.videodownloadingline.utils.changeStatusBarColor
import com.example.videodownloadingline.utils.hideFullSrc
import java.util.*
import kotlin.math.round


const val TAG = "ANUJ"

class HomeScrFragment : Fragment(R.layout.home_src_fragment) {
    private lateinit var binding: HomeSrcFragmentBinding
    private var fileExplore: BroadcastReceiver? = null

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeSrcFragmentBinding.bind(view)
        initial()
        val des = getString(R.string.texting_txt)
        val title = "Testing File"
        val request = DownloadManager.Request(Uri.parse(getString(R.string.testing_urL2)))

        binding.downloadLayout.titleDownload.text = des
        request.setDescription(des)
            .setTitle(title)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "AnujCv.mp4"
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val manage = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val id = manage.enqueue(request)


        DownloadProgressLiveData(
            this.requireActivity().application,
            id
        ).observe(viewLifecycleOwner) {

            binding.downloadLayout.totalSize.text = getString(
                R.string.size_test_tab,
                getMb(it.bytesDownloadedSoFar),
                getMb(it.totalSizeBytes)
            )
            binding.downloadLayout.downloadStatusVid.text = it.stat.lowercase(Locale.getDefault())
            binding.downloadLayout.progressBar.progress =
                (it.bytesDownloadedSoFar * 100.0 / it.totalSizeBytes).toInt()
            Log.i(TAG, "onViewCreated: $it")
        }


        //startDownloading(id, manage)
//        setBroadcastReceiver(id, manage)

        /*
        val thread = Thread {
            val query = DownloadManager.Query()
            query.setFilterById(id)
            var cursor = manage.query(query)

            while (cursor.moveToFirst()) {
                val byteMaxSize =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val byteDownload =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                if (byteMaxSize > -1) {
                    val progress = ((byteDownload.toLong()) * 100.0 / byteMaxSize)
                    binding.downloadLayout.progressBar.progress = progress.toInt()
                    if (binding.downloadLayout.progressBar.progress >= 100) {
                        Toast.makeText(activity, "Downloaded Completed", Toast.LENGTH_SHORT)
                            .show()
                        break
                    }
                }
                cursor.close()
                cursor = manage.query(query)
            }
            if (!cursor.moveToFirst()) {
                Toast.makeText(activity, "Cursor is Empty", Toast.LENGTH_SHORT).show()
            }


        }

        thread.start()*/

    }

    private fun getMb(bytesDownloadedSoFar: Long): Int {
        return round((bytesDownloadedSoFar / 1048576).toDouble()).toInt()
    }





    private fun setBroadcastReceiver(TrueId: Long, manage: DownloadManager) {
        fileExplore = object : BroadcastReceiver() {
            @SuppressLint("Range")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                //startDownloading(TrueId, manage)
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == TrueId) {
                    Toast.makeText(
                        activity,
                        "Downloaded Files Completed from Receiver",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        activity?.registerReceiver(
            fileExplore,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        activity?.hideFullSrc()
        activity?.changeStatusBarColor()
    }


}