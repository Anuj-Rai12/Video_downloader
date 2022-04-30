package com.example.videodownloadingline.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.DateFormat


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


val createdCurrentTimeData: String
    get() = DateFormat.getDateTimeInstance().format(System.currentTimeMillis())


