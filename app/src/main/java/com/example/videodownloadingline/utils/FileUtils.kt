package com.example.videodownloadingline.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.videodownloadingline.ui.whatsapp.WhatsappActivity
import java.io.File
import java.text.DateFormat
import java.util.*


fun getFileDir(fileName: String, context: Context): File {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
    else
        File(
            Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), fileName
        )
}

fun getFileUrl(file: File, context: Context): Uri? {
    return FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName.toString() + ".provider",
        file
    )
}


// Whats App Stories


fun getWhatsappStoryPath(type: WhatsappActivity.Companion.WhatsappClick): ArrayList<File> {
    // image path list
    val list: ArrayList<File> = ArrayList()

    // fetching file path from storage
    //val file = getFileDir(getString(R.string.whatsapp_media_loc), requireContext())
    val file = getWhatsAppPath()
    val listFile = file.listFiles()

    if (listFile != null && listFile.isNullOrEmpty()) {
        Arrays.sort(listFile)
    }

    if (listFile != null) {
        for (imgFile in listFile) {
            when (type) {
                WhatsappActivity.Companion.WhatsappClick.IsImage -> {
                    checkFile(true, imgFile)?.let { list.add(it) }
                }
                WhatsappActivity.Companion.WhatsappClick.IsVideo -> {
                    checkFile(false, imgFile)?.let { list.add(it) }
                }
            }
        }
    }

    // return imgPath List
    return list
}

private fun getWhatsAppPath(): File {
    return if (Build.VERSION.SDK_INT < 30) {
        // Less then 11
        val targetPath =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"

        File(targetPath)
        //allfiles = targetDirector.listFiles()
    } else {
        // Android 11
        val targetPath =
            Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"

        File(targetPath)
        //allfiles = targetDirector.listFiles()
    }
}

private fun checkFile(flag: Boolean, imgFile: File): File? {
    return if (flag) {
        if (imgFile.name.endsWith(".jpg") || imgFile.name.endsWith(".jpeg") || imgFile.name.endsWith(
                ".png"
            )
        ) {
            imgFile
        } else
            null
    } else if (!flag) {
        if (imgFile.name.endsWith(".mp4") || imgFile.name.endsWith(".gif") || imgFile.name.endsWith(
                ".mkv"
            )
        ) {
            imgFile
        } else
            null
    } else
        return null
}


private fun convertMillieToHMmSs(millie: Long): String {
    val seconds = millie / 1000
    val second = seconds % 60
    val minute = seconds / 60 % 60
    val hour = seconds / (60 * 60) % 24
    return if (hour > 0) {
        String.format("%02d:%02d:%02d", hour, minute, second)
    } else {
        String.format("%02d:%02d", minute, second)
    }
}


fun Context.videoDuration(file: File): String {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this, getFileUrl(file, this))
    val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
    Log.i(TAG, "videoDuration: $time")
    retriever.release()
    return convertMillieToHMmSs(time ?: 0)
}


/*val File.getVideoLength: Int
    get() = DownloadProgressLiveData.getMb(this.length())*/


val createdCurrentTimeData: String
    get() = DateFormat.getDateTimeInstance().format(System.currentTimeMillis())


