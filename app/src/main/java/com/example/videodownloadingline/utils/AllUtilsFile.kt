package com.example.videodownloadingline.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.example.videodownloadingline.R
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executors

const val TAG = "VIDEO_DOWNLOADER"

fun Activity.showFullSrc() {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

}


fun Activity.hideFullSrc() {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.show(WindowInsets.Type.statusBars())
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    }

}

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}


fun View.hide() {
    this.isVisible = false
}

fun View.show() {
    this.isVisible = true
}

fun AppCompatActivity.hideActionBar() {
    this.supportActionBar!!.hide()
}

fun AppCompatActivity.showActionBar() {
    this.supportActionBar!!.show()
}

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.changeStatusBarColor(color: Int = R.color.Cod_Gray_color) {
    this.window?.statusBarColor = resources.getColor(color, null)
}

fun checkInputField(string: String) = string.isNullOrEmpty() || string.isBlank()

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.getColorInt(color: Int): Int {
    return resources.getColor(color, null)
}


inline fun SearchView.onQueasyListenerChanged(crossinline Listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            Listener(newText.orEmpty())
            return true
        }

    })
}

enum class BottomType {
    Delete,
    MoveTo,
    SetPin,
}

interface OnBottomSheetClick {
    fun onItemClicked(type: String)
}

fun View.showSandbar(msg: String, length: Int = Snackbar.LENGTH_SHORT, color: Int? = null) {
    val snackBar = Snackbar.make(this, msg, length)
    color?.let {
        snackBar.view.setBackgroundColor(it)
    }
    snackBar.show()
}


fun getIconBgLis() = listOf(
    R.color.Dodger_Blue_color,
    R.color.Malachite_color,
    R.color.Cornflower_Blue_color,
    R.color.Chambray_color
)


val DOWNLOAD_ITEM = listOf(
    DownloadItems(
        0,
        "File 1",
        "Image.png",
        "https://www.googl.com",
        "00:33sec",
        ".mp3",
        23,
        1649567923658
    ),
    DownloadItems(
        0,
        "File 2",
        "Image.png",
        "https://www.googl.com",
        "00:33sec",
        ".mp3",
        23,
        1649567943595
    ), DownloadItems(
        0,
        "File 3",
        "Image.png",
        "https://www.googl.com",
        "00:33sec",
        ".mp3",
        23,
        1649567956532
    ), DownloadItems(
        0,
        "File 4",
        "Image.png",
        "https://www.googl.com",
        "00:33sec",
        ".mp3",
        23,
        1649567968552
    ), DownloadItems(
        0,
        "File 5",
        "Image.png",
        "https://www.googl.com",
        "00:33sec",
        ".mp3",
        23,
        1649567968552
    ), DownloadItems(
        0,
        "File 6",
        "Image.png",
        "https://www.googl.com",
        "00:33sec",
        ".mp3",
        23,
        1649567979235
    )
)