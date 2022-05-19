package com.example.videodownloadingline.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.ui.whatsapp.WhatsappActivity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.*
import java.net.URL
import java.text.DateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection


fun getFileDir(fileName: String, context: Context, flag: Boolean = true): File {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && flag)
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

fun getListOfFile(file: File): ArrayList<DownloadItems> {
    val list: ArrayList<DownloadItems> = ArrayList()
    val fileList = file.listFiles()
    if (!fileList.isNullOrEmpty()) {
        Arrays.sort(fileList)
        fileList.forEachIndexed { index, f ->
            Log.i(TAG, "getListOfFile: $f")
            list.add(
                DownloadItems(
                    id = index,
                    fileTitle = f.name,
                    fileThumbLoc = "",
                    fileLength = "",
                    fileExtensionType = "Folder",
                    fileSize = f.length(),
                    fileLoc = f.toString()
                )
            )
        }
    }
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
        if (imgFile.name.endsWith(".jpg") || imgFile.name.endsWith(".jpeg")
            || imgFile.name.endsWith(".png") || imgFile.name.endsWith(".gif")
        ) {
            imgFile
        } else
            null
    } else if (!flag) {
        if (imgFile.name.endsWith(".mp4") || imgFile.name.endsWith(".mkv")
            || imgFile.name.endsWith(".mpeg4") || imgFile.name.endsWith(".h264")
            || imgFile.name.endsWith(".aac") || imgFile.name.endsWith(".ac3")
            || imgFile.name.endsWith(".avi") || imgFile.name.endsWith(".mkv")
            || imgFile.name.endsWith(".mov") || imgFile.name.endsWith(".flv")
            || imgFile.name.endsWith(".3gp")
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


fun Context.videoDuration(file: File): String? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, getFileUrl(file, this))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
        Log.i(TAG, "videoDuration: $time")
        retriever.release()
        convertMillieToHMmSs(time ?: 0)
    } catch (e: Exception) {
        Log.i(TAG, "videoDuration: got error while getting video Duration ${e.message}")
        null
    }

}


fun getMimeType(uri: Uri, context: Context): String? {
    return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr: ContentResolver? = context.contentResolver
        cr?.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
            uri.toString()
        )
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
    }
}


fun Activity.bitUrl(bitmap: Bitmap, title: String): Uri? {
    val byteArray = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
    val path = MediaStore.Images.Media.insertImage(
        contentResolver,
        bitmap,
        "IMG_$title",
        null
    )
    return Uri.parse(path)
}


@RequiresApi(Build.VERSION_CODES.Q)
fun Activity.putVideo(
    url: String,
    fileName: String,
    format: String,
    filePath: String = Environment.DIRECTORY_DOWNLOADS + "/VideoDownload"
): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, format)
        put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            filePath
        )
    }
    val resolver = contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    if (uri != null) {
        URL(url).openStream().use { input ->
            resolver.openOutputStream(uri).use { output ->
                input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
            }
        }
    }
    return uri
}

fun Activity.playVideo(uri: String, format: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setDataAndType(Uri.parse(uri), format)
        startActivity(intent)
    } catch (e: Exception) {
        toastMsg("Can not Play the Video!!")
    }
}

@SuppressLint("Range")
fun Activity.deleteVideo(filepath:String) {
    try {
        val fDelete = File(filepath)
        if (fDelete.exists()) {
            if (fDelete.delete()) {
                toastMsg("File is Deleted Successfully")
            } else {
                toastMsg("Cannot delete File")
            }
        } else {
            toastMsg("File not found")
        }
    } catch (e: Exception) {
        Log.i(TAG, "deleteVideo: Error while deleting file ${e.localizedMessage}")
        toastMsg("Cannot delete File")
    }
}


@RequiresApi(Build.VERSION_CODES.N)
private fun findSizeImgM3u8(m3u8: String): Pair<String, Long> {
    return try {
        val url = URL(m3u8)
        val httpsConnection = url.openConnection() as HttpsURLConnection
        httpsConnection.requestMethod = "GET"
        httpsConnection.connect()
        val inputString = httpsConnection.inputStream
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        inputString.read(buffer)
        outputStream.write(buffer)
        Pair(outputStream.toString(), httpsConnection.contentLengthLong)
    } catch (e: Exception) {
        Log.i(TAG, "findSizeImgM3u8: ${e.message}")
        Pair("", 0)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun findWidthAndHeight(url: String): Pair<Long, List<Int>> {
    val value = runBlocking {
        val string = async(IO) {
            findSizeImgM3u8(url)
        }
        string.await()
    }
    val sampleString = "RESOLUTION="
    val string = value.first
    val size = value.second
    return if (string.isNotEmpty()) {
        val index = string.indexOf(sampleString)
        Log.i(TAG, "findWidthAndHeight: $index")
        if (index != -1) {
            val startingIndex = index + (sampleString.length)
            var testString = string.substring(startingIndex)
            Log.i(TAG, "findWidthAndHeight: String Testing $testString")
            val builder = StringBuilder()
            while (testString.isNotEmpty() && testString[0] != ',') {
                builder.append(testString[0])
                testString = testString.substring(1)
            }
            val list = builder.toString().split("x").map { it.toInt() }
            Log.i(TAG, "findWidthAndHeight: $list")
            //Main res
            Pair(size, list)
        } else {
            //Sample Result
            Pair(size, listOf(240))
        }
    } else {
        //Sample Result
        Pair(size, listOf(360))
    }
}

fun Activity.moveFile(inputPath: String, outputPath: String): Boolean {
    Log.i("moveFile", " inputPath is ---> $inputPath")
    Log.i("moveFile", " outputPath is --> $outputPath")
    Log.i("moveFile", " For input file is --> ${File(inputPath).exists()}")
    val inputStream: InputStream?
    val outputStream: OutputStream?
    return try {
        //create output directory if it doesn't exist
        val dir = File(outputPath)
        if (!dir.exists()) {
            dir.parentFile?.mkdirs()
        }
        inputStream = FileInputStream(inputPath)
        outputStream = FileOutputStream(outputPath)
        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        inputStream.close()
        // write the output file
        outputStream.flush()
        outputStream.close()
        // delete the original file
        File(inputPath).delete()
        true
    } catch (e: FileNotFoundException) {
        Log.i(TAG, "moveFile: ${e.localizedMessage}")
        toastMsg("File is Not Found!!")
        false
    } catch (e: Exception) {
        Log.i(TAG, "moveFile: ${e.localizedMessage}")
        toastMsg("File is Not Created Found!!")
         false
    }
}


val createdCurrentTimeData: String
    get() = DateFormat.getDateTimeInstance().format(System.currentTimeMillis())


