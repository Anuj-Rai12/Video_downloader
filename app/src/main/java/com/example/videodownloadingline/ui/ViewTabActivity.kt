package com.example.videodownloadingline.ui


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemGridAdaptor
import com.example.videodownloadingline.adaptor.view_tab_open_adaptor.ViewTabOpenAdaptor
import com.example.videodownloadingline.databinding.ActivityViewTabBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.downloaditem.Category
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.downloaditem.TypeOfDownload
import com.example.videodownloadingline.model.tabitem.TabItem
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel
import com.example.videodownloadingline.view_model.MainViewModel
import java.io.File


class ViewTabActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityViewTabBinding.inflate(layoutInflater)
    }

    private lateinit var viewTabAdaptor: ViewTabOpenAdaptor

    private var newFolderDialogBox: AddIconsDialogBox? = null

    private lateinit var gridAdaptor: DownloadItemGridAdaptor
    private var mainViewModel: MainViewModel? = null
    private var tablist: ArrayList<TabItem>? = null

    private val viewModel: DownloadFragmentViewModel by viewModels()

    private val normalFolder by lazy {
        intent.extras?.getParcelableArrayList<DownloadItems>(getString(R.string.set_pin_txt))
    }
    private val category by lazy {
        intent.extras?.getString(getString(R.string.set_pin_cat))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainViewModel = MainViewModel.getInstance()

        viewModel.eventSetPin.observe(this) {
            it.getContentIfNotHandled()?.let { pair ->
                when (pair.second) {
                    getString(R.string.file_is_found) -> {
                        if (pair.first.setPin.isNotEmpty()) {
                            Intent(this@ViewTabActivity, SetPinActivity::class.java).apply {
                                putParcelableArrayListExtra(
                                    getString(R.string.set_pin_txt),
                                    arrayListOf(pair.first)
                                )
                                putExtra(
                                    getString(R.string.set_pin_cat),
                                    Category.NormalFolder.name
                                )
                                putExtra(getString(R.string.set_pin_click), true)
                            }.run {
                                startActivity(this)
                            }
                        } else {
                            playVideo(pair.first.fileLoc)
                        }
                    }
                    getString(R.string.file_is_not_found) -> {
                        Log.i(TAG, "onCreate: file is Not Found")
                        playVideo(pair.first.fileLoc)
                    }
                }
            }
        }

        val extras = intent.extras
        if (extras != null) {
            tablist = extras.getParcelableArrayList("TabItem")
            tablist?.let { value ->
                Log.i(TAG, "onCreate: $value")
                setRecycleView()
                viewTabAdaptor.submitList(value)
            }
            normalFolder?.first()?.let { download ->
                Log.i(TAG, "onCreate: At View TAB $download")
                setRecycleView2()
                viewModel.getListOfFolder(File(download.fileLoc))
            }
        }
        viewModel.folderItem.observe(this) {
            Log.i(TAG, "onCreate: $it")
            gridAdaptor.submitList(it)
        }
    }

    private fun setRecycleView2() {
        binding.recycleViewForTab.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@ViewTabActivity, 2)
            gridAdaptor = DownloadItemGridAdaptor(
                TypeOfDownload.IsFiles.name,
                context = this@ViewTabActivity
            ) { data, _ ->
                showOptionDialog(data)
            }
            adapter = gridAdaptor
        }
    }


    private fun showOptionDialog(data: DownloadItems) {
        if (newFolderDialogBox == null)
            newFolderDialogBox = AddIconsDialogBox()

        newFolderDialogBox?.displaySortingViewRecycle(
            context = this,
            data = arrayOf(FileOption.PlayVideo.name, FileOption.Share.name),
            title = data.fileTitle,
            listenerForNewFolder = { res, _ ->
                newFolderDialogBox?.dismiss()
                when (FileOption.valueOf(res)) {
                    FileOption.PlayVideo -> {
                        playVideoFile(data)
                    }
                    FileOption.Share -> {
                        shareFile(data)
                    }
                }
            }
        )
    }

    private fun shareFile(data: DownloadItems) {
        if (category == null) {
            toastMsg("Cannot Open File Something Went Wrong")
            return
        }
        when (Category.valueOf(category!!)) {
            Category.PinFolder -> {
                val url = getFileUrl(File(data.fileLoc), this)
                url?.let { shareVideo(url) } ?: binding.root.showSandbar(
                    "Cannot share the Video!!",
                    color = Color.RED
                )
            }
            Category.NormalFolder -> {
                shareVideo(Uri.parse(data.fileLoc))
            }
        }
    }

    private fun playVideoFile(data: DownloadItems) {
        if (category == null) {
            toastMsg("Cannot Open File Something Went Wrong")
            return
        }
        Log.i(TAG, "validData: $data")
        when (Category.valueOf(category!!)) {
            Category.PinFolder -> {
                val url = getFileUrl(File(data.fileLoc), this)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                intent.setDataAndType(Uri.parse(url.toString()), "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
            Category.NormalFolder -> {
                //Valid it by checking Pin File
                viewModel.searchFileInNormalFolder(data.fileLoc, data.fileTitle, data)
            }
        }


    }

    private fun setRecycleView() {
        binding.recycleViewForTab.apply {
            layoutManager = GridLayoutManager(this@ViewTabActivity, 2)
            setHasFixedSize(true)
            viewTabAdaptor = ViewTabOpenAdaptor { data, flag ->
                Log.i(TAG, "setRecycleView: $data and $flag")
                if (!flag)
                    mainViewModel?.currentNumTab = data.id - 1
                else
                    mainViewModel?.removeTab = Pair(true, data.id - 1)

                onBackPressed()
            }
            adapter = viewTabAdaptor
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (normalFolder != null) {
            supportActionBar!!.title = normalFolder!!.first().fileTitle
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        } else {
            supportActionBar!!.title = "New Tab"
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_add)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (tablist != null) {
            mainViewModel?.changeStateForCreateNewTB(flag = true)
        }
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        newFolderDialogBox?.dismiss()
    }

    enum class FileOption {
        PlayVideo,
        Share
    }

    private fun shareVideo(uri: Uri) {
        ShareCompat.IntentBuilder(this)
            .setStream(uri)
            .setType("video/*")
            .setChooserTitle("Share video...")
            .setChooserTitle("Share Video")
            .setSubject("Enjoy the Video")
            .setText("Download By VideoDownload App 2022")
            .startChooser()

//        MediaScannerConnection.scanFile(this, arrayOf(pathFile), null) { _, uri ->
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "video/*"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download By VideoDownload App 2022")
//            shareIntent.putExtra(Intent.EXTRA_TITLE, "Enjoy the Video")
//            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
//            startActivity(Intent.createChooser(shareIntent, "Share Video"))
//        }
    }
}