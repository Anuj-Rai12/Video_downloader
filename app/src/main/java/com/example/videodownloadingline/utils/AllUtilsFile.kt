package com.example.videodownloadingline.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.R
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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


/*fun manipulateColor(color: Int, factor: Float): Int {
    val a: Int = Color.alpha(color)
    val r = (Color.red(color) * factor).roundToInt()
    val g = (Color.green(color) * factor).roundToInt()
    val b = (Color.blue(color) * factor).roundToInt()
    return Color.argb(
        a,
        r.coerceAtMost(255),
        g.coerceAtMost(255),
        b.coerceAtMost(255)
    )
}
*/

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
    fun <T> onItemClicked(type: T)
}

fun View.showSandbar(msg: String, length: Int = Snackbar.LENGTH_SHORT, color: Int? = null) {
    val snackBar = Snackbar.make(this, msg, length)
    color?.let {
        snackBar.view.setBackgroundColor(it)
    }
    snackBar.show()
}


fun Activity.toastMsg(msg: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, duration).show()
}

inline fun <reified T> Activity.goToNextActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}


fun getIconBgLis() = listOf(
    R.color.Dodger_Blue_color,
    R.color.Malachite_color,
    R.color.Cornflower_Blue_color,
    R.color.Chambray_color
)

@SuppressLint("UseCompatLoadingForDrawables")
fun Fragment.showDialogBox(
    title: String = getString(R.string.permission_title),
    desc: String = getString(R.string.permission_desc, "Storage"),
    btn: String = getString(R.string.permission_btn),
    flag: Boolean = false,
    callback: () -> Unit
) {
    MaterialAlertDialogBuilder(
        requireContext(),
        R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
    )
        .setTitle(title)
        .setMessage(desc)
        .setCancelable(flag)
        .setBackground(resources.getDrawable(R.drawable.dialog_box_shape, null))
        .setPositiveButton(btn) { dialog, _ ->
            callback.invoke()
            dialog.dismiss()
        }.show()
}


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

val BOOK_MARK_IC = listOf(
    HomeSrcIcon(0, "FaceBook", "https://www.facebook.com/", 2131099651),
    HomeSrcIcon(0, "Instagram", "https://www.instagram.com/", 2131099655),
    HomeSrcIcon(0, "Whatsapp status", "https://www.whatsapp.com/", 2131099653),
    HomeSrcIcon(0, "Twitter", "	https://www.twitter.com/", 2131099651),
    HomeSrcIcon(0, "DailyMotion", "https://www.dailymotion.com/", 2131099653),
    HomeSrcIcon(0, "Vimeo", "https://vimeo.com/", 2131099651),
    HomeSrcIcon(
        0,
        "Sample",
        "https://www.learningcontainer.com/mp4-sample-video-files-download/",
        2131099653
    )
)
