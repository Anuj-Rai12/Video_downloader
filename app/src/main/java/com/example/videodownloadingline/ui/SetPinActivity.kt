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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainViewModel = MainViewModel.getInstance()
        viewModel.folderCreateId.observe(this) {
            if (it != null) {
                Log.i(TAG, "onCreate: $it")
                MainDownloadFragment.downloadViewPage?.currentItem = 2
                onBackPressed()
            }
        }
        extras = intent.extras
        if (extras != null) {
            val name = extras!!.getString(getString(R.string.set_pin_cat))
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
            } ?: binding.root.showSandbar("Please Create Strong Pin", Color.RED)
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
                forPinFolder(valueList, pin)
            }
            Category.NormalFolder -> {//Set Pin of Normal File
                val valueList =
                    extras!!.getParcelableArrayList<DownloadItems>(getString(R.string.set_pin_txt))

            }
        }
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
        val secureFolderItem = SecureFolderItem(res.id, res.folder, res.folder, pin)
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