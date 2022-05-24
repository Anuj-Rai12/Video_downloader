package com.example.videodownloadingline.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.SetLockSrcFragmentLayoutBinding
import com.example.videodownloadingline.model.downloaditem.Category
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.securefolder.SecureFolderItem
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.DownloadFragmentViewModel
import com.example.videodownloadingline.view_model.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.ArrayList

class SetPinActivity : AppCompatActivity() {
    private val binding by lazy {
        SetLockSrcFragmentLayoutBinding.inflate(layoutInflater)
    }

    private var mainViewModel: MainViewModel? = null
    private var setPinKey: String? = null
    private var category: String? = null
    private var extras: Bundle? = null
    private val viewModel: DownloadFragmentViewModel by viewModels()
    private var isFlagClicked: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainViewModel = MainViewModel.getInstance()
        viewModel.folderCreateId.observe(this) {
            it.getContentIfNotHandled()?.let { res ->
                Log.i(TAG, "onCreate: $res")
                MainDownloadFragment.downloadViewPage?.currentItem = 2
                onBackPressed()
            }
        }

        viewModel.eventSetPin.observe(this) {
            it.getContentIfNotHandled()?.let { res ->
                when (res.second) {
                    getString(R.string.folder_is_found) -> {
                        goToNextActivity<ViewTabActivity>(
                            downloadItems = res.first,
                            forSetPin = true,
                            category = Category.PinFolder.name
                        )
                        finish()
                    }
                    getString(R.string.folder_is_not_found) -> {
                        binding.root.showSandbar("Wrong Pin", color = Color.RED)
                    }

                    getString(R.string.file_is_found) -> {
                        //Public Folder
                        playVideo(res.first.fileLoc)
                        onBackPressed()
                    }
                    getString(R.string.file_is_not_found) -> {
                        binding.root.showSandbar("Wrong Pin File Cannot be Open", color = Color.RED)
                    }
                }
            }
        }


        extras = intent.extras
        if (extras != null) {
            val name = extras!!.getString(getString(R.string.set_pin_cat))
            isFlagClicked = extras!!.getBoolean(getString(R.string.set_pin_click))
            if (isFlagClicked) {
                "Give PIN to access It!!".also { binding.lockTitle.text = it }
                "For security ,give a PIN".also { binding.lockDesc.text = it }
            }
            if (name != null) {
                category = name
            }
        }

        binding.setLockPin.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty() || text.isBlank()) {
                initUi()
            } else {
                setPinKey = "$text".trim()
                binding.clrBtn.setTextColor(setColor(R.color.Surfie_Green_color))
                if (setPinKey!!.length >= 6) {
                    binding.nextBtn.backgroundTintList = setColor(R.color.Surfie_Green_color)
                    "Confirm".also { binding.nextBtn.text = it }
                }
            }
        }

        binding.nextBtn.setOnClickListener {
            setPinKey?.let {
                Log.i(TAG, "onCreate: Strong Password $it")
                if (isValidPassword(it)) {
                    getFolderData(it)
                } else {
                    binding.root.showSandbar(
                        "PIN is Weak!!",
                        length = Snackbar.LENGTH_LONG,
                        color = Color.RED,
                        text = "HINT",
                    ) {
                        this.showDialogBox(
                            "Create Strong Password!!",
                            desc = msg(),
                            btn = "got it",
                            flag = true
                        ) {
                            initUi()
                            binding.setLockPin.setText("")
                        }
                    }
                }
            } ?: binding.root.showSandbar("Please Create Strong Pin", color = Color.RED)
        }
        binding.clrBtn.setOnClickListener {
            initUi()
            binding.setLockPin.setText("")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initUi() {
        setPinKey = null
        binding.nextBtn.text = getString(R.string.nxt_btn)
        binding.clrBtn.setTextColor(setColor())
        binding.nextBtn.backgroundTintList = setColor()
    }

    private fun getFolderData(pin: String) {
        if (category.isNullOrEmpty() || extras == null) {
            toastMsg("Unknown Error")
            return
        }
        when (Category.valueOf(category!!)) {
            Category.PinFolder -> {//Set Pin for Secured Folder
                val valueList =
                    extras!!.getParcelableArrayList<SecureFolderItem>(getString(R.string.set_pin_txt))
                if (isFlagClicked)
                    checkPinToOpenFolder(valueList, pin)
                else
                    forPinFolder(valueList, pin)
            }
            Category.NormalFolder -> {//Set Pin of Normal File
                val valueList =
                    extras!!.getParcelableArrayList<DownloadItems>(getString(R.string.set_pin_txt))
                if (isFlagClicked) {
                    //Valid Pin
                    checkPinToOpenFile(valueList, pin)
                } else {
                    addPinToFile(valueList, pin)
                }
            }
        }
    }

    private fun checkPinToOpenFile(valueList: ArrayList<DownloadItems>?, pin: String) {
        valueList?.first()?.let { res ->
            if (File(res.fileLoc).exists()) {
                viewModel.checkPinToOpenFile(src = "%${res.fileLoc}%", pin = "%$pin%", res = res)
            } else {
                toastMsg("File not Found!!")
                return
            }
        } ?: binding.root.showSandbar("Error Unknown is File Not Open")
    }

    private fun addPinToFile(valueList: ArrayList<DownloadItems>?, pin: String) {
        valueList?.first()?.let { res ->
            val file = File(res.fileLoc)
            if (file.exists()) {
                viewModel.updateDownloadItem(
                    downloadItems = res,
                    filePath = res.fileLoc,
                    category = Category.valueOf(res.category),
                    setPin = pin
                )
            } else {
                toastMsg("File is Not Exits!!")
                onBackPressed()
            }
        } ?: binding.root.showSandbar("Unknown Error cannot find File", color = Color.RED)
    }

    private fun checkPinToOpenFolder(valueList: ArrayList<SecureFolderItem>?, pin: String) {
        valueList?.first()?.let { res ->
            Log.i(TAG, "checkPinToOpenFolder: Pin is -> ${res.src}")
            Log.i(TAG, "checkPinToOpenFolder: Pin is -> $pin")
            viewModel.checkPinToOpenFolder("%${res.src}%", "%$pin%", res)
        } ?: binding.root.showSandbar("Cannot open Folder", color = Color.RED)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setColor(color: Int = R.color.Emperor_color): ColorStateList {
        return ColorStateList.valueOf(getColorInt(color))
    }

    private fun forPinFolder(valueList: ArrayList<SecureFolderItem>?, pin: String) {
        valueList?.first()?.let { res ->
            val targetPath = File(res.src)
            if (!targetPath.exists()) {
                targetPath.mkdirs()
                addFolderToDataBase(res, pin)
                binding.root.showSandbar("File is Created", color = Color.GREEN)
            } else {
                toastMsg("Same File is Already Present in Directory")
                onBackPressed()
            }
        } ?: binding.root.showSandbar("Unfortunate Error!!", color = Color.RED)
    }

    private fun addFolderToDataBase(res: SecureFolderItem, pin: String) {
        val secureFolderItem = SecureFolderItem(res.id, res.folder, res.src, pin)
        viewModel.addPinFolder(secureFolderItem)
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}